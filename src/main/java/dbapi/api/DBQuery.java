package dbapi.api;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


/**
 * 
 * @author alex
 * 
 *         Nov 5, 2012
 */
public class DBQuery<T>
{
    private final Class<T> cls;
    private final T model;
    private final QueryRecorder recorder;


    private DBQuery(final T model, final QueryRecorder recorder, final Class<T> cls)
    {
        super();
        this.cls = cls;
        this.model = model;
        this.recorder = recorder;
    }



    public Class<T> getType()
    {
        return cls;
    }

    public T getModel()
    {
        return model;
    }

    public Map<String, Object> getCriteria()
    {
        return recorder.crit;
    }

    public static class QueryRecorder implements MethodInterceptor
    {
        Map<String, Object> crit = new HashMap<String, Object>();

        @Override
        public Object intercept(final Object object, final Method method, final Object[] args,
                final MethodProxy methodProxy) throws Throwable
                {
            final String name = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
            crit.put(name, args[0]);
            return methodProxy.invokeSuper(object, args);
                }
    };

    public static <T> DBQuery<T> on(final Class<T> cls)
    {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        final QueryRecorder rec = new QueryRecorder();
        enhancer.setCallback(rec);
        final T proxied = (T) enhancer.create();
        return new DBQuery<T>(proxied, rec, cls);
    }
}
