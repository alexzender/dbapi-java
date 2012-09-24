package dbapi.kernel.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import dbapi.api.KernelException;

/**
 * 
 * @author alex
 *
 */
public class AnnotatedField
{
    private String column;
    private int length;
    private String columnDefinition;
    private Method getter;
    private Method setter;
    private Field classField;
    private boolean id;
    private boolean lob = false;
        
    public String getColumn()
    {
        return column;
    }
    public void setColumn(String column)
    {
        this.column = column;
    }
    public int getLength() 
    {
        return length;
    }
    public void setLength(int length) 
    {
        this.length = length;
    }
    public Method getGetter()
    {
        return getter;
    }
    public void setGetter(Method getter)
    {
        this.getter = getter;
    }
    public Method getSetter()
    {
        return setter;
    }
    public void setSetter(Method setter)
    {
        this.setter = setter;
    }   
    public Field getClassField()
    {
        return classField;
    }
    public void setClassField(Field classField)
    {
        this.classField = classField;
    }
    public boolean isId()
    {
        return id;
    }
    public void setId(boolean id)
    {
        this.id = id;
    }
    public Class<?> getType()
    {
        if(null != classField)
        {
            return classField.getType();
        }
        else if(null != getter)
        {
            return getter.getReturnType();
        }
        return null;
    }
    
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) 
    {
        if(null != classField)
        {
            return classField.getAnnotation(annotationClass);
        }
        else if (null != getter)
        {
            return getter.getAnnotation(annotationClass);
        }

        return null;
    }
    public boolean isLob()
    {
        return lob;
    }
    public void setLob(boolean lob)
    {
        this.lob = lob;
    }
    
    public String getColumnDefinition()
    {
        return columnDefinition;
    }
    public void setColumnDefinition(String columnDefinition)
    {
        this.columnDefinition = columnDefinition;
    }
    @Override
    public String toString()
    {
        return "column=" + column + ", field=" + classField;
    }
    
    public byte[] getValue(Object obj)
    {
        byte[] res = null;
        Object data = null;
        try
        {
            if(null == getGetter())
            {
                getClassField().setAccessible(true);
                data = getClassField().get(obj);
                
            }
            else
            {
                data = getGetter().invoke(obj);
            }
        }
        catch(IllegalAccessException ex)
        {
            throw new KernelException(ex);
        }
        catch(InvocationTargetException ex)
        {
            throw new KernelException(ex);
        }
        if(null == data)
        {
            res = "".getBytes();
        }
        else if(data instanceof String)
        {
            res = ((String) data).getBytes();
        }
        else if(data instanceof Long)
        {
            Long longData = (Long) data;
            ByteBuffer buffer = ByteBuffer.allocate(8).putLong(longData);
            buffer.position(0);
            res = new byte[buffer.remaining()];
            buffer.get(res);
        }
        
        
        return res;
    }
    
}
