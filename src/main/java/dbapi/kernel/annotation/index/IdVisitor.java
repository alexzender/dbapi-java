package dbapi.kernel.annotation.index;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import dbapi.api.KernelException;
import dbapi.api.meta.DBId;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
/**
 * 
 * @author alex
 *
 */
public class IdVisitor
{
    private static Logger log = Logger.getLogger(IdVisitor.class);


    public boolean visitMethod(final Method method, final Class<?> cls, final AnnotatedEntity ann)
    {
        final DBId id = method.getAnnotation(DBId.class);

        if (id == null)
        {
            return false;
        }

        ann.setIdGetter(method);

        final String setterName = "set" + method.getName().substring(3);

        try
        {
            final Method idSetter = cls.getMethod(setterName, method.getReturnType());
            ann.setIdSetter(idSetter);
        }
        catch (final SecurityException e)
        {
            log.error("", e);
        }
        catch (final NoSuchMethodException e)
        {
            log.debug("Failed to find id setter method for " + cls.getName());
        }


        return true;
    }


    public boolean visitField(final Class<?> cls, final Field field, final AnnotatedEntity ann)
    {
        boolean isId = false;
        final DBId id = field.getAnnotation(DBId.class);
        if (id != null)
        {
            isId = true;

            final AnnotatedField fieldDef = ann.getFields().get(field.getName());
            fieldDef.setId(true);

            try
            {
                final BeanInfo info = Introspector.getBeanInfo(cls);
                for (final PropertyDescriptor descriptor : info.getPropertyDescriptors())
                {
                    if (descriptor.getName().equals(field.getName()))
                    {
                        ann.setIdGetter(descriptor.getReadMethod());
                        ann.setIdSetter(descriptor.getWriteMethod());
                        break;
                    }
                }
            }
            catch (final IntrospectionException e)
            {
                throw new KernelException(e);
            }
        }
        return isId;
    }
}
