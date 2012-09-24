package dbapi.api;

import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import dbapi.GuiceModule;
import dbapi.kernel.EntityManagerFactoryImpl;

/**
 * 
 * @author alex
 *
 */
public class DBAPI
{
    public static AbstractModule getBindings()
    {
        return new GuiceModule();
    }
    
    public static EntityManagerFactory getFactory(Map<String, String> config, 
            Set<Class<?>> entities)
    {
        return getFactory(config, entities, null);
    }
    
    public static EntityManagerFactory getFactory(Map<String, String> config, Set<Class<?>> entities, Injector injector)
    {
        if(null == injector)
        {
            injector = Guice.createInjector(new GuiceModule());
        }
        EntityManagerFactoryImpl factory =  injector.getInstance(EntityManagerFactoryImpl.class);
        
        factory.init(config, entities);
        
        return factory;
    }
    
}
