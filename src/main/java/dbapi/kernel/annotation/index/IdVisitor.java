package dbapi.kernel.annotation.index;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;

import org.apache.log4j.Logger;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedEntity;
/**
 * 
 * @author alex
 *
 */
public class IdVisitor   
{
    private static Logger log = Logger.getLogger(IdVisitor.class);
    
   
    public boolean visitMethod(Method method, Class<?> cls, AnnotatedEntity ann)
    {
        Id id = method.getAnnotation(Id.class);
        
        if (id == null)
        {
            return false;
        }
        
        ann.setIdGetter(method);
                
        String setterName = "set" + method.getName().substring(3);
        
        try
        {
            Method idSetter = cls.getMethod(setterName, method.getReturnType());
            ann.setIdSetter(idSetter);
        }
        catch (SecurityException e)
        {
            log.error("", e);
        }
        catch (NoSuchMethodException e)
        {
            log.debug("Failed to find id setter method for " + cls.getName());
        }
        
        
        return true;
    }

    
    public void visitField(Class<?> cls, Field field, AnnotatedEntity ann)
    {
        Id id = field.getAnnotation(Id.class);
        if (id != null)
        {
            try
            {
                BeanInfo info = Introspector.getBeanInfo(cls);
                for (PropertyDescriptor descriptor : info.getPropertyDescriptors())
                {
                    if (descriptor.getName().equals(field.getName()))
                    {
                        ann.setIdGetter(descriptor.getReadMethod());
                        ann.setIdSetter(descriptor.getWriteMethod());
                        break;
                    }
                }
            }
            catch (IntrospectionException e)
            {
                throw new KernelException(e);
            }
        }
    }
}
