package dbapi.kernel.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collection;

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
    private AnnotatedEntity complexDef;

    public String getColumn()
    {
        return column;
    }

    public void setColumn(final String column)
    {
        this.column = column;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(final int length)
    {
        this.length = length;
    }

    public Method getGetter()
    {
        return getter;
    }

    public void setGetter(final Method getter)
    {
        this.getter = getter;
    }

    public Method getSetter()
    {
        return setter;
    }

    public void setSetter(final Method setter)
    {
        this.setter = setter;
    }

    public Field getClassField()
    {
        return classField;
    }

    public void setClassField(final Field classField)
    {
        this.classField = classField;
    }

    public boolean isId()
    {
        return id;
    }

    public void setId(final boolean id)
    {
        this.id = id;
    }

    public Class<?> getType()
    {
        if (null != classField)
        {
            return classField.getType();
        }
        else if (null != getter)
        {
            return getter.getReturnType();
        }
        return null;
    }

    public boolean isComplexType()
    {
        final Class<?> type = getType();
        final boolean res = !isCollection() && !type.isAnnotation() && !type.isAnonymousClass() && !type.isArray() && !type.isEnum()
                && !type.isInterface() && !type.isLocalClass() && !type.isMemberClass() && !type.isPrimitive() && !type.isSynthetic()
                && !type.equals(String.class) && !type.equals(Integer.class) && !type.equals(Double.class) && !type.equals(Float.class)
                && !type.equals(Long.class);
        return res;
    }

    public boolean isCollection()
    {
        final Class<?> type = getType();
        final boolean res = type.isArray() || type.isAssignableFrom(Collection.class);
        return res;
    }

    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
    {
        if (null != classField)
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

    public void setLob(final boolean lob)
    {
        this.lob = lob;
    }

    public String getColumnDefinition()
    {
        return columnDefinition;
    }

    public void setColumnDefinition(final String columnDefinition)
    {
        this.columnDefinition = columnDefinition;
    }

    @Override
    public String toString()
    {
        return "column=" + column + ", field=" + classField;
    }

    public byte[] getValueBytes(final Object obj)
    {
        byte[] res = null;
        final Object data = getValue(obj);

        // TODO: replace if/else by dispatcher
        if (null == data)
        {
            res = "".getBytes();
        }
        else if (data instanceof String)
        {
            res = ((String) data).getBytes();
        }
        else if (data instanceof Long)
        {
            final Long longData = (Long) data;
            final ByteBuffer buffer = ByteBuffer.allocate(8).putLong(longData);
            buffer.position(0);
            res = new byte[buffer.remaining()];
            buffer.get(res);
        }

        return res;
    }

    public Object getValue(final Object obj)
    {
        Object data = null;
        try
        {
            if (null == getGetter())
            {
                getClassField().setAccessible(true);
                data = getClassField().get(obj);

            }
            else
            {
                data = getGetter().invoke(obj);
            }
        }
        catch (final IllegalAccessException ex)
        {
            throw new KernelException(ex);
        }
        catch (final InvocationTargetException ex)
        {
            throw new KernelException(ex);
        }
        return data;
    }

    public AnnotatedEntity getComplexDef()
    {
        return complexDef;
    }

    public void setComplexDef(final AnnotatedEntity complexDef)
    {
        this.complexDef = complexDef;
    }

}
