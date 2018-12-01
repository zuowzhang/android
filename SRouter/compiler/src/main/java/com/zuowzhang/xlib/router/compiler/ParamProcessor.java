package com.zuowzhang.xlib.router.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.zuowzhang.xlib.router.annotation.Constants;
import com.zuowzhang.xlib.router.annotation.Param;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ParamProcessor extends AbstractRouterProcessor {
    private Map<TypeElement, List<Element>> parentAndChildren = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Param.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "ParamProcessor process");
        Set<? extends Element> paramElements = roundEnvironment.getElementsAnnotatedWith(Param.class);
        if (paramElements != null && paramElements.size() > 0) {
            InjectorUtilGenerator.genJavaFile(mFiler);
            return genJavaFile(paramElements);
        }
        return false;
    }

    private FieldSpec createParamSetField() {
        //Map<String, String> paramKeySet = new HashMap<>()
        ParameterizedTypeName mapTypeName =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class));
        return FieldSpec.builder(mapTypeName, Constants.INJECTOR_FIELD_PARAM_META_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T<>()", HashMap.class)
                .build();
    }

    private void createInitStaticBlock(TypeSpec.Builder builder, List<Element> children) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        for (Element element : children) {
            Param param = element.getAnnotation(Param.class);
            String fieldName = param.name();
            if (fieldName == null || fieldName.length() == 0) {
                fieldName = element.getSimpleName().toString();
            }
            codeBlockBuilder.addStatement("$N.put($S, $S)",
                    Constants.INJECTOR_FIELD_PARAM_META_NAME,
                    fieldName,
                    ElementUtil.getParamTypeByElement(mTypes, mElements, element).toString());
        }
        builder.addStaticBlock(codeBlockBuilder.build());
    }

    private boolean categoryParams(Set<? extends Element> paramElements) {
        for (Element element : paramElements) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            mMessager.printMessage(Diagnostic.Kind.NOTE,
                    "element = " + element.getSimpleName() + "; " +
                            "enclosingElement = " + enclosingElement.getQualifiedName());
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                String message = "The inject fields CAN NOT BE 'private'!!! please check field ["
                        + element.getSimpleName() + "] in class [" + enclosingElement.getQualifiedName() + "]";
                mMessager.printMessage(Diagnostic.Kind.ERROR, message);
                continue;
            }
            if (parentAndChildren.containsKey(enclosingElement)) {
                parentAndChildren.get(enclosingElement).add(element);
            } else {
                List<Element> childList = new ArrayList<>();
                childList.add(element);
                parentAndChildren.put(enclosingElement, childList);
            }
        }
        return parentAndChildren.size() > 0;
    }

    private MethodSpec createParseParamMethod() {
        ClassName injectorUtilClassName = ClassName.bestGuess(Constants.PACKAGE_NAME_GEN + "." + Constants.INJECTOR_UTIL_CLASS_NAME);
        return MethodSpec.methodBuilder(Constants.INJECTOR_METHOD_PARSER_PARAMS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constants.INTENT_CLASS_NAME), "intent").build())
                .addStatement("$T.parseParams(paramKeyMap, path, intent)", injectorUtilClassName)
                .build();
    }

    private MethodSpec createInjectMethod(String targetClassName, List<Element> children) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constants.INJECTOR_METHOD_INJECT_NAME);
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(targetClassName), "target").build())
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constants.INTENT_CLASS_NAME), "intent").build())
                .beginControlFlow("if(intent != null && intent.getExtras() != null)");
        for (Element element : children) {
            Param param = element.getAnnotation(Param.class);
            String paramName = param.name();
            String fieldName = element.getSimpleName().toString();
            if (paramName == null || paramName.length() == 0) {
                paramName = fieldName;
            }
            if (ElementUtil.isTypeSerializableOrParcelable(mTypes, mElements, element)) {
                builder.addStatement("target.$N = ($N)intent.$N($S)",
                        fieldName,
                        element.asType().toString(),
                        ElementUtil.getExtraStatement(mTypes, mElements, element),
                        paramName);
            } else {
                builder.addStatement("target.$N = intent.$N($S)",
                        fieldName,
                        ElementUtil.getExtraStatement(mTypes, mElements, element),
                        paramName);
            }
        }
        builder.endControlFlow();//end if(intent != null && ....
        return builder.build();
    }

    private TypeSpec createInjector(TypeElement parent, List<Element> children) {
        String parentName = parent.getQualifiedName().toString();
        String injectorName = parent.getSimpleName() + "$$" + Constants.INJECTOR_CLASS_NAME;
        TypeSpec.Builder builder = TypeSpec.classBuilder(injectorName)
                .addModifiers(Modifier.PUBLIC)
                .addField(createParamSetField())
                .addMethod(createParseParamMethod())
                .addMethod(createInjectMethod(parentName, children));
        createInitStaticBlock(builder, children);

        return builder.build();
    }

    private boolean genJavaFile(Set<? extends Element> paramElements) {
        boolean result = false;
        if (categoryParams(paramElements)) {
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChildren.entrySet()) {
                TypeElement parent = entry.getKey();
                List<Element> children = entry.getValue();
                String parentName = parent.getQualifiedName().toString();
                String packageName = parentName.substring(0, parentName.lastIndexOf("."));
                TypeSpec injector = createInjector(parent, children);
                try {
                    JavaFile.builder(packageName, injector)
                            .build()
                            .writeTo(mFiler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            parentAndChildren.clear();
        }
        return result;
    }
}
