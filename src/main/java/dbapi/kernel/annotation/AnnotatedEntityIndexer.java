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

    public AnnotatedEntity build(final Class<?> cls)
    {
        final AnnotatedEntity ann = new AnnotatedEntity();

        ann.setClazz(cls);

        tableName.visit(cls, ann);

        //index methods
        final Method[] methods = cls.getMethods();

        for (final Method method : methods)
        {
            final String methodName = method.getName();

            if(canSkipMethod(method))
            {
                continue;
            }

            final boolean idMethod = idVisitor.visitMethod(method, cls, ann);

            ann.addGetter(method);

            final AnnotatedField field = new AnnotatedField();
            field.setGetter(method);
            field.setId(idMethod);

            //discover true column name in the target system if any
            columnVisitor.visitMethod(method, field);

            setterVisitor.visit(cls, method, field);

            final String fieldName = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
            ann.getFields().put(fieldName, field);
        }

        final Field [] fields =  cls.getDeclaredFields();

        for (final Field field : fields)
        {
            if(canSkipField(field))
            {
                continue;
            }

            idVisitor.visitField(cls, field, ann);

            columnVisitor.visitField(field, ann);
        }



        return ann;
    }

    private boolean canSkipField(final Field field)
    {
        // no need to save this one
        final DBTransient transientM = field.getAnnotation(DBTransient.class);
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

        //skip collections for now
        if (Collection.class.isAssignableFrom(method.getReturnType()))
        {
            return true;
        }

        //skip collection mappings for now


        return false;
    }
}
