package dbapi.test.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dbapi.api.DBAPI;
import dbapi.api.DBConfig;
import dbapi.api.DBSession;
import dbapi.api.DBSessionFactory;
import dbapi.test.system.model.User;

/**
 * 
 * @author alex
 *
 */

public class CassandraCRUDTest
{
    private static final Logger log = Logger.getLogger(CassandraCRUDTest.class);

    private static DBSessionFactory factory;
    private DBSession em;

    private final List<User> cleanUpQueue = new ArrayList<User>();

    @BeforeClass
    public static void setUpGlobal()
    {
        BasicConfigurator.configure();

        final DBConfig config = new DBConfig();
        config.selectCassandra();
        config.setDatabase("tests");
        config.setHost("localhost");
        config.setPort(9160);

        final Set<Class<?>> entities = new HashSet<Class<?>>();
        entities.add(User.class);

        factory = DBAPI.getFactory(config, entities);
        log.debug("Created factory");
    }

    @AfterClass
    public static void tearDownGlobal()
    {
        log.debug("Terminating factory");
        if(factory != null)
        {
            factory.close();
            log.debug("Factory terminated.");
        }
    }

    @Before
    public void setUp()
    {
        em = factory.createSession();
        log.debug("Entity Manager created");
    }

    @After
    public void tearDown()
    {
        for(final User user : cleanUpQueue)
        {
            try
            {
                em.delete(user);
            }
            catch(final Throwable th)
            {
                log.error("Failed to clean up user id:" + user.getId(), th);
            }
        }
    }

    @Test
    public void testCreateDelete()
    {
        log.info("CRUD scenario started");

        final User user = new User();
        user.setUsername("john");
        user.setDisplayName("John Doe");
        user.setPassword("1234");

        em.save(user);

        assertNotNull("The ID is missed in the newly created object", user.getId());

        cleanUpQueue.add(user);

        User userCopy = em.get(User.class, user.getId());

        assertNotNull("Failed to lookup newly created object ", userCopy);
        assertEquals("Failed to read the copy of a newly created User object", user, userCopy);

        em.delete(user);

        cleanUpQueue.remove(user);

        userCopy = em.get(User.class, user.getId());

        assertNull("Found previously deleted object", userCopy);

        log.info("CRUD scenario finished.");
    }


}
