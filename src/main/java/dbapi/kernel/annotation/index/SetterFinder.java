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
    
    public void visit(Class<?> cls, Method method, AnnotatedField field)
    {
        String methodName = method.getName();
        try
        {
            
            Method setter = cls.getMethod("set" + methodName.substring(3), method.getReturnType());
            if (setter.getParameterTypes().length != 1)
            {
                throw new IllegalArgumentException("The setter method " + methodName + " must have one parameter only. Class: " + cls.getName());
            }
            else if (setter.getParameterTypes()[0] != method.getReturnType())
            {
                throw new IllegalArgumentException("The setter method " + methodName + " doesn't return valid type of object");
            }                
            field.setSetter(setter);
        }
        catch (SecurityException e)
        {
            log.debug("Failed to process class " + cls.getName() + ", method " + methodName, e);
            throw new KernelException(e);
        }
        catch (NoSuchMethodException e)
        {
            log.debug("Error when processing method " + methodName + " in class " + cls.getName(), e);
            throw new KernelException(e);
        }
        
    }
}
