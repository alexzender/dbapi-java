package dbapi.test.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dbapi.api.DBAPI;
import dbapi.test.system.model.User;

/**
 * 
 * @author alex
 *
 */

public class CRUDTest
{
    private static final Logger log = Logger.getLogger(CRUDTest.class);
    
    private static EntityManagerFactory factory;
    private EntityManager em;
    
    private List<User> cleanUpQueue = new ArrayList<User>();
    
    @BeforeClass
    public static void setUpGlobal()
    {
        BasicConfigurator.configure();
        
        Map<String, String> config = new HashMap<String, String>();
        config.put("db.type", "cassandra");
        config.put("plugins.cassandra.keyspace", "tests");
        config.put("plugins.cassandra.host", "localhost");
        config.put("plugins.cassandra.port", "9160");
        
        
        Set<Class<?>> entities = new HashSet<Class<?>>();
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
        em = factory.createEntityManager();
        log.debug("Entity Manager created");
    }
    
    @After
    public void tearDown()
    {
        for(User user : cleanUpQueue)
        {
            try
            {
                em.remove(user);
            }
            catch(Throwable th)
            {
                log.error("Failed to clean up user id:" + user.getId(), th);
            }
        }
    }
    
    @Test
    public void testCreateDelete()
    {
        log.info("CRUD scenario started");
        
        User user = new User();
        user.setUsername("john");
        user.setDisplayName("John Doe");
        user.setPassword("1234");
        
        em.persist(user);
        
        assertNotNull("The ID is missed in the newly created object", user.getId());
        
        cleanUpQueue.add(user);
        
        User userCopy = em.find(User.class, user.getId());
        
        assertNotNull("Failed to lookup newly created object ", userCopy);
        assertEquals("Failed to read the copy of a newly created User object", user, userCopy);
        
        em.remove(user);
        
        cleanUpQueue.remove(user);
        
        userCopy = em.find(User.class, user.getId());
        
        assertNull("Found previously deleted object", userCopy);
        
        log.info("CRUD scenario finished.");
    }
    
    
}
