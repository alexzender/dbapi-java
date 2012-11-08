package dbapi.api;


import com.google.common.base.Preconditions;

import dbapi.kernel.Kernel;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.GetCommand;
import dbapi.plugins.SaveCommand;
import dbapi.plugins.DeleteCommand;
/**
 * 
 * @author alex
 *
 */
public class DBSession

{
    private Kernel kernel;
    private DBPlugin db;
    private volatile boolean opened;

    public void init(final Kernel kernel)
    {
        this.kernel = kernel;
        this.db = kernel.getDb();
        opened = true;
    }

    private AnnotatedEntity validateAndLookup(final Object entity)
    {
        Preconditions.checkArgument(null != entity, "Valid entity object is required");

        final AnnotatedEntity def = kernel.getAnnotationService().lookup(entity.getClass().getName());

        Preconditions.checkArgument(null != def, "The entity class " + entity.getClass().getName() + " hasn't been registered in the JPA runtime yet.");

        return def;
    }

    public <T> DBQuery<T> queryOn(final Class<T> cls)
    {
        final DBQuery<T> query = DBQuery.<T> on(cls);
        return query;
    }

    public void save(final Object entity)
    {
        final SaveCommand cmd = db.getPersistCommand();

        final AnnotatedEntity def = validateAndLookup(entity);

        cmd.execute(entity, def);
    }


    public <T> T update(final T entity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void delete(final Object entity)
    {
        final DeleteCommand cmd = db.getRemoveCommand();

        final AnnotatedEntity def = validateAndLookup(entity);

        cmd.execute(entity, def);
    }

    public <T> T get(final Class<T> entityClass, final String primaryKey)
    {
        Preconditions.checkArgument(null != entityClass, "The entity class is required");
        Preconditions.checkArgument(null != primaryKey, "The key is required");

        final AnnotatedEntity def = kernel.getAnnotationService().lookup(entityClass.getName());

        Preconditions.checkArgument(null != def, "The entity class " + def.getClass().getName() + " hasn't been registered in the JPA runtime yet.");

        final GetCommand finder = db.getFindCommand();

        final T res = finder.find(entityClass, def, primaryKey);

        return res;
    }


    public void flush()
    {
        kernel.getDb().flush();
    }


    public void close()
    {
        db.close();
        opened = false;
    }


    public boolean isOpen()
    {
        return opened;
    }

}
