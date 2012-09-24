package dbapi.kernel.annotation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.index.FieldVisitor;
import dbapi.kernel.annotation.index.IdVisitor;
import dbapi.kernel.annotation.index.SetterFinder;
import dbapi.kernel.annotation.index.TableNameIndexer;

/**
 * 
 * @author alex
 *
 */
public class AnnotatedEntityIndexer
{
    private static Logger log = Logger.getLogger(AnnotatedEntityIndexer.class);
    
    @Inject
    private TableNameIndexer tableName;
    
    @Inject
    private IdVisitor idVisitor;
    
    @Inject
    private FieldVisitor columnVisitor;
    
    @Inject
    private SetterFinder setterVisitor;
    
    public AnnotatedEntity build(Class<?> cls)
    {
        AnnotatedEntity ann = new AnnotatedEntity();
        
        ann.setClazz(cls);
        
        tableName.visit(cls, ann);
        
        //index methods
        Method[] methods = cls.getMethods();  
        
        for (Method method : methods) 
        {
            String methodName = method.getName();
            
            if(canSkipMethod(method))
            {
                continue;
            }
          
            boolean idMethod = idVisitor.visitMethod(method, cls, ann);
            
            ann.addGetter(method);
            
            AnnotatedField field = new AnnotatedField();
            field.setGetter(method);           
            field.setId(idMethod);
            
            //discover true column name in the target system if any
            columnVisitor.visitMethod(method, field);
            
            setterVisitor.visit(cls, method, field);
            
            String fieldName = methodName.substring(3,4).toLowerCase() + methodName.substring(4);            
            ann.getFields().put(fieldName, field);            
        }
        
        Field [] fields =  cls.getDeclaredFields();
        
        for (Field field : fields)
        {
            if(canSkipField(field))
            {
                continue;
            }
            
            idVisitor.visitField(cls, field, ann);
            
            columnVisitor.visitField(field, ann);
        }
        
        //TODO inheritance routines, not supported yet
        /*Class rootClass = null;
        for (Class c = cls.getSuperclass(); c.getSuperclass() != null; c = c.getSuperclass()) 
        {
            MappedSuperclass mappedSuperclass = (MappedSuperclass) c.getAnnotation(MappedSuperclass.class);
            Entity entity = (Entity) c.getAnnotation(Entity.class);
            Inheritance inheritance = (Inheritance) c.getAnnotation(Inheritance.class);
            if (mappedSuperclass != null || entity != null) 
            {
                // buildMethods
                if (entity != null) 
                {
                    rootClass = c;
                }
                //putEntityListeners(ai, superClass);
            }
        }*/
        
       
        return ann;
    }
    
    private boolean canSkipField(Field field)
    {
        // no need to save this one
        Transient transientM = field.getAnnotation(Transient.class);
        if (transientM != null)
        {
            return true; 
        }
        
        //TODO: no support for relations for now
        if(Collection.class.isAssignableFrom(field.getType()))
        {
            return true;
        }
        
        return false;
    }

    
    private boolean canSkipMethod(Method method)
    {
        //TODO: check for setters as well
        String methodName = method.getName();
        if ( ! methodName.startsWith("get") || methodName.startsWith("getClass"))
        {
            return true;
        }
        
        // skip transient fields
        Transient transientM = method.getAnnotation(Transient.class);
        if (transientM != null)
        {
            return true; 
        }
        
        //skip collections for now
        if (Collection.class.isAssignableFrom(method.getReturnType()))
        {
            return true;
        }

        //skip collection mappings for now
        ManyToOne mto = method.getAnnotation(ManyToOne.class);
        OneToMany otm = method.getAnnotation(OneToMany.class);
        OneToOne oto = method.getAnnotation(OneToOne.class);
        if(null != mto || null != otm || null != oto)
        {
            return true;
        }
        
        return false;
    }
}
