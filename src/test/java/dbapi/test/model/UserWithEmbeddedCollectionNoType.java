package dbapi.test.model;

import java.util.List;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBId;

/**
 * 
 * @author alex
 *
 * Nov 7, 2012
 */
public class UserWithEmbeddedCollectionNoType
{
    @DBId
    @DBColumn
    private String id;

    @DBColumn
    private String username;

    @DBColumn
    private List friends;



    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public List getFriends()
    {
        return friends;
    }

    public void setFriends(final List friends)
    {
        this.friends = friends;
    }



}
