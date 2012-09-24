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
    public void setTable(String table)
    {
        this.table = table;
    }
    public Method getIdGetter()
    {
        return idGetter;
    }
    public void setIdGetter(Method idGetter)
    {
        this.idGetter = idGetter;
    }
    public Method getIdSetter()
    {
        return idSetter;
    }
    public void setIdSetter(Method idSetter)
    {
        this.idSetter = idSetter;
    }
    public Class<?> getClazz()
    {
        return clazz;
    }
    public void setClazz(Class<?> clazz)
    {
        this.clazz = clazz;
    }
    public Map<String, Method> getGetters()
    {
        return getters;
    }
    public void setGetters(Map<String, Method> getters)
    {
        this.getters = getters;
    }
    public Map<String, AnnotatedField> getFields()
    {
        return fields;
    }
    public void setFields(Map<String, AnnotatedField> fields)
    {
        this.fields = fields;
    }    
    public void addGetter(Method method)
    {
        getters.put(method.getName(), method);
    }
    
    public ByteBuffer getIdBytes(Object entity)
    {
        ByteBuffer res = null;
        try
        {
            Object obj = getIdGetter().invoke(entity);
            if(null == obj)
            {
                return res;
            }
            else if(obj instanceof Long)
            {
                Long longValue = (Long) obj;
                return Utils.longToByteBuffer(longValue);
            }
            else if (obj instanceof String)
            {
                String strValue = (String) obj;
                return ByteBuffer.wrap(strValue.getBytes());
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new KernelException(e);
        }
        
        return res;
    }
    public void setId(Object entity, Object id)
    {
        try
        {
            getIdSetter().invoke(entity, id);            
        }
        catch (IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new KernelException(e);
        }
    }
   
}
