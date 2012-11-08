package dbapi.kernel.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import dbapi.api.meta.DBTransient;
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

    public  AnnotatedEntity build(final Class<?> cls)
    {
        final AnnotatedEntity ann = new AnnotatedEntity();

        ann.setType(cls);

        tableName.visit(cls, ann);

        processFields(cls, ann);

        //index methods
        processMethods(cls, ann);

        return ann;
    }

    private void processFields(final Class<?> cls, final AnnotatedEntity ann)
    {
        final Field[] fields = cls.getDeclaredFields();

        // index fields first
        for (final Field field : fields)
        {
            if (canSkipField(field))
            {
                continue;
            }

            final AnnotatedField index = columnVisitor.visitField(field, ann);

            setterVisitor.visit(cls, index);
            idVisitor.visitField(cls, field, ann);
        }
    }

    private void processMethods(final Class<?> cls, final AnnotatedEntity ann)
    {
        final Method[] methods = cls.getMethods();

        for (final Method method : methods)
        {
            final String methodName = method.getName();

            if(canSkipMethod(method))
            {
                continue;
            }

            final String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

            //already processed?
            if (ann.getFields().containsKey(fieldName))
            {
                continue;
            }


            ann.addGetter(method);

            //discover true column name in the target system if any
            final AnnotatedField field = columnVisitor.visitMethod(method, ann);

            final boolean idMethod = idVisitor.visitMethod(method, cls, ann);
            field.setId(idMethod);

            setterVisitor.visit(cls, field);
        }
    }

    private boolean skipCollection(final Class<?> cls, final String name)
    {
        /*        final TypeVariable<Class<?>>[] params = ((Class) cls).getTypeParameters();
        if (params.length == 0 || params.length > 1)
        {
            log.warn("Ignoring collection field. Unsufficient generic information for the  " + name);
            return true;
        }*/
        return false;
    }

    private boolean canSkipField(final Field field)
    {
        // no need to save this one
        final DBTransient transientM = field.getAnnotation(DBTransient.class);
        if (transientM != null)
        {
            return true;
        }

        // skip arrays for now
        if (field.getType().isArray())
        {
            return true;
        }

        if (Collection.class.isAssignableFrom(field.getType()) && skipCollection(field.getType(), field.getName()))
        {
            return true;
        }

        return false;
    }


    private boolean canSkipMethod(final Method method)
    {
        //TODO: check for setters as well
        final String methodName = method.getName();
        if ( ! methodName.startsWith("get") || methodName.startsWith("getClass"))
        {
            return true;
        }

        // skip transient fields
        final DBTransient transientM = method.getAnnotation(DBTransient.class);
        if (transientM != null)
        {
            return true;
        }

        // skip arrays for now
        if (method.getReturnType().isArray())
        {
            return true;
        }

        if (method.getReturnType().isAssignableFrom(Collection.class) && skipCollection(method.getReturnType(), method.getName()))
        {
            return true;
        }


        return false;
    }
}
