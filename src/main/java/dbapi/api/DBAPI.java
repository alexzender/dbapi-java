package dbapi.api;

import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import dbapi.GuiceModule;

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

    public static DBSessionFactory getFactory(final DBConfig config,
            final Set<Class<?>> entities)
    {
        return getFactory(config, entities, null);
    }

    public static DBSessionFactory getFactory(final DBConfig config, final Set<Class<?>> entities, Injector injector)
    {
        if(null == injector)
        {
            injector = Guice.createInjector(new GuiceModule());
        }
        final DBSessionFactory factory = injector.getInstance(DBSessionFactory.class);

        factory.init(config, entities);

        return factory;
    }

}
