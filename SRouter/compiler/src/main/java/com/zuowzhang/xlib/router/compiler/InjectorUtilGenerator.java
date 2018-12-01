package com.zuowzhang.xlib.router.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.zuowzhang.xlib.router.annotation.Constants;
import com.zuowzhang.xlib.router.annotation.ParamType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class InjectorUtilGenerator {
    private static boolean isGeneratedInjectUtil = false;

    private static MethodSpec createGetParamsMethod() {
        ParameterizedTypeName mapTypeName =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class));
        return MethodSpec.methodBuilder(Constants.INJECTOR_UTIL_METHOD_GET_PARAMS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(mapTypeName)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .addStatement("$T<$T, $T> params = new $T<>()", Map.class, String.class, String.class, HashMap.class)
                .addStatement("$T[] urlParts = path.split($S)", String.class, "\\?")
                .beginControlFlow("if(urlParts.length > 1)")
                .addStatement("$T query = urlParts[1]", String.class)
                .beginControlFlow("for($T item : query.split($S))", String.class, "&")
                .addStatement("$T[] pair = item.split($S)", String.class, "=")
                .beginControlFlow("try")
                .beginControlFlow("if(pair.length > 1)")
                .addStatement("$T key = $T.decode(pair[0], $S)", String.class, URLDecoder.class, "UTF-8")
                .addStatement("$T value = $T.decode(pair[1], $S)", String.class, URLDecoder.class, "UTF-8")
                .addStatement("params.put(key, value)")
                .endControlFlow()//end if(pair.length > 1)
                .nextControlFlow("catch ($T e)", UnsupportedEncodingException.class)
                .addStatement("e.printStackTrace()")
                .endControlFlow()//end try
                .endControlFlow()//end for($T item : query.split($S))
                .endControlFlow()//end if(urlParts.length > 1)
                .addStatement("return params")
                .build();
    }

    private static MethodSpec createParseParamsMethod() {
        ParameterizedTypeName mapTypeName =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class));
        ClassName paramTypeName = ClassName.get(ParamType.class);
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constants.INJECTOR_UTIL_METHOD_PARSER_PARAMS_NAME);

        CodeBlock.Builder switchBuilder = CodeBlock.builder()
                .beginControlFlow("switch(paramType)")
                .add("case BOOLEAN:\n").indent()
                .addStatement("intent.putExtra(key, Boolean.parseBoolean(value))")
                .addStatement("break").unindent()
                .add("case BYTE:\n").indent()
                .addStatement("intent.putExtra(key, Byte.parseByte(value))")
                .addStatement("break").unindent()
                .add("case SHORT:\n").indent()
                .addStatement("intent.putExtra(key, Short.parseShort(value))")
                .addStatement("break").unindent()
                .add("case INT:\n").indent()
                .addStatement("intent.putExtra(key, Integer.parseInt(value))")
                .addStatement("break").unindent()
                .add("case LONG:\n").indent()
                .addStatement("intent.putExtra(key, Long.parseLong(value))")
                .addStatement("break").unindent()
                .add("case CHAR:\n").indent()
                .addStatement("intent.putExtra(key, value.charAt(0))")
                .addStatement("break").unindent()
                .add("case FLOAT:\n").indent()
                .addStatement("intent.putExtra(key, Float.parseFloat(value))")
                .addStatement("break").unindent()
                .add("case DOUBLE:\n").indent()
                .addStatement("intent.putExtra(key, Double.parseDouble(value))")
                .addStatement("break").unindent()
                .add("default:\n").indent()
                .addStatement("intent.putExtra(key, value)")
                .addStatement("break").unindent()
                .endControlFlow();

        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(mapTypeName, Constants.INJECTOR_FIELD_PARAM_META_NAME).build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constants.INTENT_CLASS_NAME), "intent").build())
                .addStatement("$T params = $N(path)", mapTypeName, "getParams")
                .beginControlFlow("for($T key : params.keySet())", String.class)
                .addStatement("$T paramType = $T.valueOf($N.get(key))", paramTypeName, paramTypeName, Constants.INJECTOR_FIELD_PARAM_META_NAME)
                .addStatement("$T value = params.get(key)", ClassName.get(String.class))
                .addCode(switchBuilder.build())
                .endControlFlow();//end for($T key : $N(path)...

        return builder.build();
    }

    public static void genJavaFile(Filer filer) {
        synchronized (InjectorUtilGenerator.class) {
            if (!isGeneratedInjectUtil) {
                isGeneratedInjectUtil = true;
                TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.INJECTOR_UTIL_CLASS_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(createGetParamsMethod())
                        .addMethod(createParseParamsMethod());
                try {
                    JavaFile.builder(Constants.PACKAGE_NAME_GEN, builder.build())
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
