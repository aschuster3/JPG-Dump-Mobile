package com.jpgdump.mobile.util;


public final class ContextFormattingLogger {
    private final ContextLogger delegate;
    
    public int d(String fmt, Object ...args) {
        return delegate.d(String.format(fmt, args));
    }
    
    public int d(Throwable t, String fmt, Object ...args) {
        return delegate.d(String.format(fmt, args), t);
    }
    
    public int e(String fmt, Object ...args) {
        return delegate.e(String.format(fmt, args));
    }
    
    public int e(Throwable t, String fmt, Object ...args) {
        return delegate.e(String.format(fmt, args), t);
    }
    
    public int i(String fmt, Object ...args) {
        return delegate.i(String.format(fmt, args));
    }
    
    public int i(Throwable t, String fmt, Object ...args) {
        return delegate.i(String.format(fmt, args), t);
    }
    
    public int v(String fmt, Object ...args) {
        return delegate.v(String.format(fmt, args));
    }
    
    public int v(Throwable t, String fmt, Object ...args) {
        return delegate.v(String.format(fmt, args), t);
    }
    
    public int w(String fmt, Object ...args) {
        return delegate.w(String.format(fmt, args));
    }
    
    public int w(Throwable t, String fmt, Object ...args) {
        return delegate.w(String.format(fmt, args), t);
    }
    
    public int w(Throwable t) {
        return delegate.w(t);
    }
    
    public int wtf(String fmt, Object ...args) {
        return delegate.wtf(String.format(fmt, args));
    }
    
    public int wtf(Throwable t, String fmt, Object ...args) {
        return delegate.wtf(String.format(fmt, args), t);
    }
    
    public int wtf(Throwable t) {
        return delegate.wtf(t);
    }
    
    private ContextFormattingLogger(ContextLogger delegate) {
        this.delegate = delegate;
    }
    
    public static ContextFormattingLogger getLogger(Object self) {
        return getLogger(self.getClass());
    }
    
    public static ContextFormattingLogger getLogger(Class<?> clazz) {
        return new ContextFormattingLogger(ContextLogger.getLogger(clazz));
    }
}
