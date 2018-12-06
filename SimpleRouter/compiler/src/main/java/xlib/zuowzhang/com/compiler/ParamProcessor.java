package xlib.zuowzhang.com.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

import xlib.zuowzhang.com.annotation.Constants;
import xlib.zuowzhang.com.annotation.Param;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
public class ParamProcessor extends AbsRouterProcessor {
    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(Param.class.getCanonicalName());
        return sets;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> paramElements = roundEnvironment.getElementsAnnotatedWith(Param.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE, "paramElements.size = " + paramElements.size());
        if (paramElements != null && paramElements.size() > 0) {
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
                if (parentAndChild.containsKey(enclosingElement)) {
                    parentAndChild.get(enclosingElement).add(element);
                } else {
                    List<Element> childList = new ArrayList<>();
                    childList.add(element);
                    parentAndChild.put(enclosingElement, childList);
                }
            }
            return genInjector();
        }
        return false;
    }

    private String getExtrasStatement(Element element) {
        TypeKind kind = element.asType().getKind();
        if (kind.isPrimitive()) {
            switch (kind) {
                case INT:
                    return "getExtras().getInt";
                case BYTE:
                    return "getExtras().getByte";
                case CHAR:
                    return "getExtras().getChar";
                case LONG:
                    return "getExtras().getLong";
                case FLOAT:
                    return "getExtras().getFloat";
                case DOUBLE:
                    return "getExtras().getDouble";
                case SHORT:
                    return "getExtras().getShort";
                case BOOLEAN:
                    return "getExtras().getBoolean";
            }
        }
        return "getExtras().getString";
    }

    private String putExtraStatement(Element element) {
        TypeKind kind = element.asType().getKind();
        switch (kind) {
            case INT:
                return "Integer.parseInt";
            case BYTE:
                return "Byte.parseByte";
            case CHAR:
                return "value.charAt";
            case LONG:
                return "Long.parseLong";
            case FLOAT:
                return "Float.parseFloat";
            case DOUBLE:
                return "Double.parseDouble";
            case SHORT:
                return "Short.parseShort";
            case BOOLEAN:
                return "Boolean.parseBoolean";
        }
        return "";
    }

    private boolean genInjector() {
        boolean result = false;
        if (!parentAndChild.isEmpty()) {
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {

                TypeElement parent = entry.getKey();
                List<Element> childList = entry.getValue();

                String qualifiedName = parent.getQualifiedName().toString();
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                String injectorClassName = parent.getSimpleName() + "$$" + Constants.PARAM_INJECT_CLASS_NAME;

                ParameterSpec targetParamSpec =
                        ParameterSpec.builder(ClassName.bestGuess(qualifiedName), "target").build();
                MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(Constants.PARAM_INJECT_METHOD_INJECT_NAME)
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(targetParamSpec)
                        .addStatement("$T intent = $N.getIntent()",
                                ClassName.bestGuess(Constants.INTENT),
                                "target")
                        .beginControlFlow("if(intent != null && intent.getExtras() != null)");

                MethodSpec.Builder getParamFromPath = MethodSpec.methodBuilder(Constants.PARAM_INJECT_METHOD_GET_PARAM_FROM_PATH_NAME)
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                        .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constants.INTENT), "intent").build())
                        .addStatement("$T[] urlParts = $N.split($S)", String.class, "path", "\\?")
                        .beginControlFlow("if(urlParts.length > 1)")
                        .addStatement("$T<$T, $T> kvs = new $T<>()", Map.class, String.class, String.class, HashMap.class)
                        .addStatement("$T query = urlParts[1]", String.class)
                        .beginControlFlow("for($T item : query.split($S))", String.class, "&")
                        .addStatement("$T[] pair = item.split($S)", String.class, "=")
                        .beginControlFlow("try")
                        .beginControlFlow("if(pair.length > 1)")
                        .addStatement("$T key = $T.decode(pair[0], $S)", String.class, URLDecoder.class, "UTF-8")
                        .addStatement("$T value = $T.decode(pair[1], $S)", String.class, URLDecoder.class, "UTF-8")
                        .beginControlFlow("if(key != null && key.length() > 0 && value != null && value.length() > 0)")
                        .addStatement("kvs.put(key, value)")
                        .endControlFlow();

                for (Element element : childList) {
                    Param param = element.getAnnotation(Param.class);
                    String paramName = param.name();
                    String fieldName = element.getSimpleName().toString();
                    if (paramName == null || paramName.length() == 0) {
                        paramName = fieldName;
                    }
                    injectMethodBuilder.addStatement("$N.$N = intent.$N($S)",
                            "target",
                            fieldName,
                            getExtrasStatement(element),
                            paramName);
                    getParamFromPath.addStatement("intent.putExtra($S, $N(kvs.get($S)))", fieldName, putExtraStatement(element), fieldName);
                }

                getParamFromPath
                        .endControlFlow()
                        .nextControlFlow("catch ($T e)", UnsupportedEncodingException.class)
                        .addStatement("e.printStackTrace()")
                        .endControlFlow()
                        .endControlFlow()
                        .endControlFlow();

                injectMethodBuilder.endControlFlow();

                TypeSpec.Builder injectBuilder = TypeSpec.classBuilder(injectorClassName)
                        .addMethod(injectMethodBuilder.build())
                        .addMethod(getParamFromPath.build())
                        .addModifiers(PUBLIC);

                try {
                    JavaFile.builder(packageName, injectBuilder.build())
                            .build()
                            .writeTo(mFiler);
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            parentAndChild.clear();
        }
        return result;
    }
}
