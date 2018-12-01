package com.zuowzhang.xlib.router.compiler;

import com.zuowzhang.xlib.router.annotation.Constants;
import com.zuowzhang.xlib.router.annotation.ParamType;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ElementUtil {

    public static String getExtraStatement(Types types, Elements elements, Element element) {
        TypeMirror paramTypeMirror = element.asType();
        TypeMirror serializableTypeMirror = elements.getTypeElement(Constants.SERIALIZABLE_CLASS_NAME).asType();
        TypeMirror parcelableTypeMirror = elements.getTypeElement(Constants.PARCELABLE_CLASS_NAME).asType();
        TypeMirror stringTypeMirror = elements.getTypeElement(String.class.getCanonicalName()).asType();
        switch (paramTypeMirror.getKind()) {
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
        if (types.isSubtype(paramTypeMirror, stringTypeMirror)) {
            return "getExtras().getString";
        } else if (types.isSubtype(paramTypeMirror, serializableTypeMirror)) {
            return "getExtras().getSerializable";
        } else if(types.isSubtype(paramTypeMirror, parcelableTypeMirror)) {
            return "getExtras().getParcelable";
        }

        return "getExtras().getString";
    }

    public static boolean isTypeSerializableOrParcelable(Types types, Elements elements, Element element) {
        TypeMirror paramTypeMirror = element.asType();
        TypeMirror serializableTypeMirror = elements.getTypeElement(Constants.SERIALIZABLE_CLASS_NAME).asType();
        TypeMirror parcelableTypeMirror = elements.getTypeElement(Constants.PARCELABLE_CLASS_NAME).asType();
        return types.isSubtype(paramTypeMirror, serializableTypeMirror) ||
                types.isSubtype(paramTypeMirror, parcelableTypeMirror);
    }

    public static ParamType getParamTypeByElement(Types types, Elements elements, Element element) {
        TypeMirror paramTypeMirror = element.asType();
        TypeMirror serializableTypeMirror = elements.getTypeElement(Constants.SERIALIZABLE_CLASS_NAME).asType();
        TypeMirror parcelableTypeMirror = elements.getTypeElement(Constants.PARCELABLE_CLASS_NAME).asType();
        TypeMirror stringTypeMirror = elements.getTypeElement(String.class.getCanonicalName()).asType();
        switch (paramTypeMirror.getKind()) {
            case INT:
                return ParamType.INT;
            case BYTE:
                return ParamType.BYTE;
            case CHAR:
                return ParamType.CHAR;
            case LONG:
                return ParamType.LONG;
            case FLOAT:
                return ParamType.FLOAT;
            case DOUBLE:
                return ParamType.DOUBLE;
            case SHORT:
                return ParamType.SHORT;
            case BOOLEAN:
                return ParamType.BOOLEAN;
        }
        if (types.isSubtype(paramTypeMirror, stringTypeMirror)) {
            return ParamType.STRING;
        } else if (types.isSubtype(paramTypeMirror, serializableTypeMirror)) {
            return ParamType.SERIALIZABLE;
        } else if(types.isSubtype(paramTypeMirror, parcelableTypeMirror)) {
            return ParamType.PARCELABLE;
        }
        return ParamType.STRING;
    }

}
