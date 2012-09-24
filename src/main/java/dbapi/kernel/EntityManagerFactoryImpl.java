package dbapi.kernel;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.Injector;

/**
 * 
 * @author alex
 *
 */
public class EntityManagerFactoryImpl
    implements EntityManagerFactory
{
    private boolean open = false;
    
    @Inject
    private Kernel kernel;
    
    @Inject
    private Injector guice;
    
    public void init(Map<String, String> config,  Set<Class<?>> entities)
    {
        kernel.init(config, entities);
        open = true;
    }
    
    @Override
    public EntityManager createEntityManager()
    {
        EntityManagerImpl em = new EntityManagerImpl();
        guice.injectMembers(em);
        em.init(kernel);
        return em;
    }

    @Override
    public EntityManager createEntityManager(Map map)
    {
        return createEntityManager();
    }

    @Override
    public void close()
    {
        kernel.getDb().close();        
    }

    @Override
    public boolean isOpen()
    {
        return open;
    }

}
