package dbapi.kernel.annotation.index;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedField;
/**
 * 
 * @author alex
 *
 */
public class SetterFinder
{
    private static Logger log = Logger.getLogger(SetterFinder.class);

    public void visit(final Class<?> cls, final AnnotatedField field)
    {
        try
        {
            final String setterName = "set" +  field.getColumn().substring(0, 1).toUpperCase() + field.getColumn().substring(1);
            final Method setter = cls.getMethod(setterName, field.getType());
            if (setter.getParameterTypes().length != 1)
            {
                throw new IllegalArgumentException("The setter method " + setterName + " must have one parameter only. Class: " + cls.getName());
            }
            else if (setter.getParameterTypes()[0] != field.getType())
            {
                throw new IllegalArgumentException("The setter method " + setterName + " doesn't return valid type of object");
            }
            field.setSetter(setter);
        }
        catch (final SecurityException e)
        {
            log.debug("Failed to process class " + cls.getName() + ", method " + field.getColumn(), e);
            throw new KernelException(e);
        }
        catch (final NoSuchMethodException e)
        {
            log.debug("Error when processing method " + field.getColumn() + " in class " + cls.getName(), e);
            throw new KernelException(e);
        }

    }
}
