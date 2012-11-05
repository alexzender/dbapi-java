package dbapi.kernel.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import dbapi.api.KernelException;
import dbapi.plugins.cassandra.Utils;

/**
 * 
 * @author alex
 *
 */
public class AnnotatedEntity
{
    private String table;
    private Method idGetter;
    private Method idSetter;

    private Class<?> clazz;
    private Map<String, Method> getters = new HashMap<String, Method>();
    private Map<String, AnnotatedField> fields = new HashMap<String, AnnotatedField>();


    public String getTable()
    {
        return table;
    }
    public void setTable(final String table)
    {
        this.table = table;
    }
    public Method getIdGetter()
    {
        return idGetter;
    }
    public void setIdGetter(final Method idGetter)
    {
        this.idGetter = idGetter;
    }
    public Method getIdSetter()
    {
        return idSetter;
    }
    public void setIdSetter(final Method idSetter)
    {
        this.idSetter = idSetter;
    }
    public Class<?> getClazz()
    {
        return clazz;
    }
    public void setClazz(final Class<?> clazz)
    {
        this.clazz = clazz;
    }
    public Map<String, Method> getGetters()
    {
        return getters;
    }
    public void setGetters(final Map<String, Method> getters)
    {
        this.getters = getters;
    }
    public Map<String, AnnotatedField> getFields()
    {
        return fields;
    }
    public void setFields(final Map<String, AnnotatedField> fields)
    {
        this.fields = fields;
    }
    public void addGetter(final Method method)
    {
        getters.put(method.getName(), method);
    }

    public Object getId(final Object entity)
    {
        Object obj;
        try
        {
            obj = getIdGetter().invoke(entity);
        }
        catch (final IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new KernelException(e);
        }
        return obj;
    }

    public ByteBuffer getIdBytes(final Object entity)
    {
        final ByteBuffer res = null;
        try
        {
            final Object obj = getIdGetter().invoke(entity);
            if(null == obj)
            {
                return res;
            }
            else if(obj instanceof Long)
            {
                final Long longValue = (Long) obj;
                return Utils.longToByteBuffer(longValue);
            }
            else if (obj instanceof String)
            {
                final String strValue = (String) obj;
                return ByteBuffer.wrap(strValue.getBytes());
            }
        }
        catch (final IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new KernelException(e);
        }

        return res;
    }
    public void setId(final Object entity, final Object id)
    {
        try
        {
            getIdSetter().invoke(entity, id);
        }
        catch (final IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new KernelException(e);
        }
    }

}
