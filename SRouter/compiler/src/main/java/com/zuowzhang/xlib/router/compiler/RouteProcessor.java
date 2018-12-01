package com.zuowzhang.xlib.router.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.zuowzhang.xlib.router.annotation.Constants;
import com.zuowzhang.xlib.router.annotation.IRouter;
import com.zuowzhang.xlib.router.annotation.Route;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class RouteProcessor extends AbstractRouterProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Route.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (routeElements != null && routeElements.size() > 0) {
            return genJavaFile(routeElements);
        }
        return false;
    }

    private boolean genJavaFile(Set<? extends Element> routeElements) {
        try {
            JavaFile.builder(Constants.PACKAGE_NAME_GEN, createAppRouter(routeElements))
                    .build()
                    .writeTo(mFiler);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private MethodSpec createInitMethod(Set<? extends Element> routeElements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        for (Element element : routeElements) {
            Route route = element.getAnnotation(Route.class);
            builder.addStatement("$N.put($S, $T.class)",
                    Constants.APP_ROUTER_FIELD_MAPPING_NAME,
                    route.path(),
                    element);
        }
        return builder.build();
    }

    private MethodSpec createMatchMethod() {
        return MethodSpec.methodBuilder(Constants.APP_ROUTER_METHOD_MATCH_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(WildcardTypeName.OBJECT)))
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .addStatement("$T[] urlParts = $N.split($S)", String.class, "path", "\\?")
                .addStatement("return $N.get(urlParts[0])", Constants.APP_ROUTER_FIELD_MAPPING_NAME)
                .build();
    }

    private FieldSpec createMappingField() {
        //Map<String, Class<?>> mapping = new HashMap()
        ParameterizedTypeName classTypeName =
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(WildcardTypeName.OBJECT));
        ParameterizedTypeName mapTypeName =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        classTypeName);
        return FieldSpec.builder(mapTypeName, Constants.APP_ROUTER_FIELD_MAPPING_NAME)
                .addModifiers(Modifier.PRIVATE)
                .initializer("new $T<>()", HashMap.class)
                .build();
    }

    private TypeSpec createAppRouter(Set<? extends Element> routeElements) {
        return TypeSpec.classBuilder(Constants.APP_ROUTER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addField(createMappingField())
                .addSuperinterface(ClassName.get(IRouter.class))
                .addMethod(createInitMethod(routeElements))
                .addMethod(createMatchMethod())
                .build();
    }
}
