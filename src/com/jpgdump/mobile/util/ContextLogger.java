package com.jpgdump.mobile.util;

import android.util.Log;

public final class ContextLogger {
    private final String name;
    public int d(String msg) {
        return Log.d(name, msg);
    }
    
    public int d(String msg, Throwable t) {
        return Log.d(name, msg, t);
    }
    
    public int e(String msg) {
        return Log.e(name, msg);
    }
    
    public int e(String msg, Throwable t) {
        return Log.e(name, msg, t);
    }
    
    public int i(String msg) {
        return Log.i(name, msg);
    }
    
    public int i(String msg, Throwable t) {
        return Log.i(name, msg, t);
    }
    
    public int v(String msg) {
        return Log.v(name, msg);
    }
    
    public int v(String msg, Throwable t) {
        return Log.v(name, msg, t);
    }
    
    public int w(String msg) {
        return Log.w(name, msg);
    }
    
    public int w(String msg, Throwable t) {
        return Log.w(name, msg, t);
    }
    
    public int w(Throwable t) {
        return Log.w(name, t);
    }
    
    public int wtf(String msg) {
        return Log.wtf(name, msg);
    }
    
    public int wtf(String msg, Throwable t) {
        return Log.wtf(name, msg, t);
    }
    
    public int wtf(Throwable t) {
        return Log.wtf(name, t);
    }
    
    private ContextLogger(String name) {
        this.name = name;
    }
    
    public static ContextLogger getLogger(Object self) {
        return getLogger(self.getClass());
    }
    
    public static ContextLogger getLogger(Class<?> clazz) {
        return new ContextLogger(clazz.getSimpleName());
    }
}
