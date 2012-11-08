package dbapi.test.system.model;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBId;
/**
 * 
 * @author alex
 *
 * Nov 7, 2012
 */
public class UserWithEmbeddedClass
{
    @DBId
    @DBColumn
    private String id;

    @DBColumn
    private String username;
    @DBColumn
    private String password;

    @DBColumn
    private UserFriend friend;




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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public UserFriend getFriend()
    {
        return friend;
    }

    public void setFriend(final UserFriend friend)
    {
        this.friend = friend;
    }


}
