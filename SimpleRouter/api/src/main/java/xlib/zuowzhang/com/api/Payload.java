package xlib.zuowzhang.com.api;

import android.os.Bundle;

import xlib.zuowzhang.com.annotation.RouteType;

public class Payload {
    private String path;
    private int flags;
    private Bundle params;
    private RouteType mType;

    public Payload(String path, int flags, Bundle params) {
        this.path = path;
        this.flags = flags;
        this.params = params;
    }

    public String getPath() {
        return path;
    }

    public int getFlags() {
        return flags;
    }

    public Bundle getParams() {
        return params;
    }

    public RouteType getType() {
        return mType;
    }

    public static class Builder {
        private String path;
        private int flags;
        private Bundle params = new Bundle();

        public Builder(String path) {
            this.path = path;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder flags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder flag(int flag) {
            this.flags |= flag;
            return this;
        }

        public Builder addParam(String key, String value) {
            params.putString(key, value);
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

        public Payload build() {
            return new Payload(path, flags, params);
        }
    }
}
