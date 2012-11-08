package dbapi.api;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import dbapi.api.DBQuery;
import dbapi.test.model.TestUser;

/**
 * 
 * @author alex
 * 
 *         Nov 5, 2012
 */
public class DBQueryTest
{
    @Test
    public void testQueryCriteria()
    {
        final DBQuery<TestUser> query = DBQuery.<TestUser>on(TestUser.class);
        final String username = "john";
        query.getModel().setUsername(username);

        final Map<String, Object> crit = query.getCriteria();

        assertEquals("The username isn't recorded properly by proxy class", username, crit.get("username"));
    }
}
