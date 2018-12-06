package xlib.zuowzhang.com.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

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

import xlib.zuowzhang.com.annotation.Constants;
import xlib.zuowzhang.com.annotation.IRouter;
import xlib.zuowzhang.com.annotation.Router;

@AutoService(Processor.class)
public class RouterProcessor extends AbsRouterProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(Router.class.getCanonicalName());
        return sets;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> routerElements = roundEnvironment.getElementsAnnotatedWith(Router.class);
        if (routerElements != null && routerElements.size() > 0) {
            TypeSpec appRouter = gen(routerElements);
            try {
                JavaFile.builder(Constants.ROUTER_PACKAGE_NAME, appRouter)
                        .build()
                        .writeTo(mFiler);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private TypeSpec gen(Set<? extends Element> routeElements) {
        TypeSpec.Builder appRouter = TypeSpec.classBuilder(Constants.APP_ROUTER_CLASS_NAME)
                .addSuperinterface(IRouter.class)
                .addField(createMapping())
                .addMethod(createInit(routeElements))
                .addMethod(createMatch())
                .addModifiers(Modifier.PUBLIC);
        return appRouter.build();
    }

    private FieldSpec createMapping() {
        //Map<String, Class>
        ParameterizedTypeName activityTypeName =
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(TypeName.OBJECT));
        ParameterizedTypeName mapTypeName =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        activityTypeName);
        return FieldSpec.builder(mapTypeName, "mPathMapping", Modifier.PRIVATE)
                .initializer("new $T<>()", HashMap.class)
                .build();
    }

    private MethodSpec createInit(Set<? extends Element> routerElements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constants.APP_ROUTER_METHOD_INIT_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        for (Element element : routerElements) {
            Router router = element.getAnnotation(Router.class);
            builder.addStatement("$N.put($S, $T.class)", "mPathMapping", router.path(), element);
        }
        return builder.build();
    }

    private MethodSpec createMatch() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constants.APP_ROUTER_METHOD_MATCH_NAME)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(TypeName.OBJECT)))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "path").build())
                .addStatement("$T[] urlParts = $N.split($S)", String.class, "path", "\\?")
                .addStatement("return $N.get(urlParts[0])", "mPathMapping");
        return builder.build();
    }
}
