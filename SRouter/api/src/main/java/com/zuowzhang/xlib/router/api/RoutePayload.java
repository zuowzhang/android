package com.zuowzhang.xlib.router.api;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public class RoutePayload {
    private RouteType type;
    private String path;
    private Bundle params;
    private int flags;
    private String action;

    public RouteType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public Bundle getParams() {
        return params;
    }

    public int getFlags() {
        return flags;
    }

    public String getAction() {
        return action;
    }

    public RoutePayload(RouteType type, String path, Bundle params, int flags, String action) {
        this.type = type;
        this.path = path;
        this.params = params;
        this.flags = flags;
        this.action = action;
    }

    public static class Builder {
        private RouteType type = RouteType.ACTIVITY;
        private String path;
        private Bundle params = new Bundle();
        private int flags;
        private String action;

        public Builder(String path) {
            this.path = path;
        }

        public Builder type(RouteType type) {
            this.type = type;
            return this;
        }

        public Builder setFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder addParam(String key, String value) {
            params.putString(key, value);
            return this;
        }

        public Builder addParam(String key, short value) {
            params.putShort(key, value);
            return this;
        }

        public Builder addParam(String key, int value) {
            params.putInt(key, value);
            return this;
        }

        public Builder addParam(String key, Byte value) {
            params.putByte(key, value);
            return this;
        }

        public Builder addParam(String key, long value) {
            params.putLong(key, value);
            return this;
        }

        public Builder addParam(String key, float value) {
            params.putFloat(key, value);
            return this;
        }

        public Builder addParam(String key, double value) {
            params.putDouble(key, value);
            return this;
        }

        public Builder addParam(String key, char value) {
            params.putChar(key, value);
            return this;
        }

        public Builder addParam(String key, Serializable value) {
            params.putSerializable(key, value);
            return this;
        }

        public Builder addParam(String key, Parcelable value) {
            params.putParcelable(key, value);
            return this;
        }

        public RoutePayload build() {
            return new RoutePayload(type, path, params, flags, action);
        }
    }
}
