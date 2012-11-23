package dbapi.test.model;

import java.util.ArrayList;
import java.util.List;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBEntity;
import dbapi.api.meta.DBId;
/**
 * 
 * @author alex
 *
 * Nov 7, 2012
 */
@DBEntity(table = "user")
public class TestUserWithEmbeddedCollection
{
    @DBId
    @DBColumn
    private String id;

    @DBColumn
    private String username;
    @DBColumn
    private String password;

    @DBColumn
    private List<TestUserFriend> friends = new ArrayList<TestUserFriend>();



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

    public List<TestUserFriend> getFriends()
    {
        return friends;
    }

    public void setFriends(final List<TestUserFriend> friend)
    {
        this.friends = friend;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (friends == null ? 0 : friends.hashCode());
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + (username == null ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final TestUserWithEmbeddedCollection other = (TestUserWithEmbeddedCollection) obj;
        if (friends == null)
        {
            if (other.friends != null)
            {
                return false;
            }
        }
        else if (!friends.equals(other.friends))
        {
            return false;
        }
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        if (password == null)
        {
            if (other.password != null)
            {
                return false;
            }
        }
        else if (!password.equals(other.password))
        {
            return false;
        }
        if (username == null)
        {
            if (other.username != null)
            {
                return false;
            }
        }
        else if (!username.equals(other.username))
        {
            return false;
        }
        return true;
    }




}
