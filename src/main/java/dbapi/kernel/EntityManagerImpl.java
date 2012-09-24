package dbapi.kernel;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.google.common.base.Preconditions;

import dbapi.api.UnsupportedFeatureException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.query.DBQuery;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.FindCommand;
import dbapi.plugins.PersistCommand;
import dbapi.plugins.RemoveCommand;
/**
 * 
 * @author alex
 *
 */
public class EntityManagerImpl
    implements EntityManager
{
    private Kernel kernel;
    private DBPlugin db;
    private volatile boolean opened;
        
    public void init(Kernel kernel)
    {
        this.kernel = kernel;
        this.db = kernel.getDb();
        opened = true;
    }
    
    private AnnotatedEntity validateAndLookup(Object entity)
    {
        Preconditions.checkArgument(null != entity, "Valid entity object is required");
        
        AnnotatedEntity def = kernel.getAnnotationService().lookup(entity.getClass().getName());   
        
        Preconditions.checkArgument(null != def, "The entity class " + entity.getClass().getName() + " hasn't been registered in the JPA runtime yet.");
        
        return def;
    }
    
    @Override
    public void persist(Object entity)
    {
        PersistCommand cmd = db.getPersistCommand();
        
        AnnotatedEntity def = validateAndLookup(entity);
        
        cmd.execute(entity, def);        
    }

    @Override
    public <T> T merge(T entity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Object entity)
    {
        RemoveCommand cmd = db.getRemoveCommand();
        
        AnnotatedEntity def = validateAndLookup(entity);
        
        cmd.execute(entity, def);       
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey)
    {
        Preconditions.checkArgument(null != entityClass, "The entity class is required");
        Preconditions.checkArgument(null != primaryKey, "The key is required");
        
        AnnotatedEntity def = kernel.getAnnotationService().lookup(entityClass.getName());   
        
        Preconditions.checkArgument(null != def, "The entity class " + def.getClass().getName() + " hasn't been registered in the JPA runtime yet.");
        
        FindCommand finder = db.getFindCommand();
        
        T res = finder.find(entityClass, def, primaryKey);
        
        return res;
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey)
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public void flush()
    {
        kernel.getDb().flush();        
    }

    @Override
    public void setFlushMode(FlushModeType flushMode)
    {
        throw new UnsupportedFeatureException();        
    }

    @Override
    public FlushModeType getFlushMode()
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode)
    {
        throw new UnsupportedFeatureException();
        
    }

    @Override
    public void refresh(Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean contains(Object entity)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Query createQuery(String queryStr)
    {
        DBQuery parsed = kernel.getQueryService().parseQueryString(queryStr);
        
        return parsed;
    }

    @Override
    public Query createNamedQuery(String name)
    {
        throw new UnsupportedFeatureException();        
    }

    @Override
    public Query createNativeQuery(String sqlString)
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass)
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping)
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public void joinTransaction()
    {
        throw new UnsupportedFeatureException();
        
    }

    @Override
    public Object getDelegate()
    {
        throw new UnsupportedFeatureException();
    }

    @Override
    public void close()
    {
        opened = false;
        //TODO: free resources, add guard to prohibit method usage
    }

    @Override
    public boolean isOpen()
    {
        return opened;
    }

    @Override
    public EntityTransaction getTransaction()
    {
        throw new UnsupportedFeatureException();
    }

}
