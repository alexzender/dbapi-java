package dbapi.api;

import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Injector;

import dbapi.kernel.Kernel;

/**
 * 
 * @author alex
 * 
 *         Nov 5, 2012
 */
public class DBSessionFactory
{
    private boolean open = false;

    @Inject
    private Kernel kernel;

    @Inject
    private Injector guice;

    public void init(final DBConfig config, final Set<Class<?>> entities)
    {
        kernel.init(config, entities);
        open = true;
    }

    public DBSession createSession()
    {
        final DBSession em = new DBSession();
        guice.injectMembers(em);
        em.init(kernel);
        return em;
    }

    public void close()
    {
        kernel.getDb().close();
        open = false;
    }

    public boolean isOpen()
    {
        return open;
    }
}
