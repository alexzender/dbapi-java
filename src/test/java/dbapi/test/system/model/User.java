package dbapi.test.system.model;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBEntity;
import dbapi.api.meta.DBId;
import dbapi.api.meta.DBTable;

/**
 * 
 * @author alex
 *
 */
@DBEntity
@DBTable(name = "user")
public class User
{
    @DBId
    @DBColumn
    private String id;

    @DBColumn
    private String username;
    @DBColumn
    private String password;
    @DBColumn
    private String displayName;
    @DBColumn
    private String bio;
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
    public String getDisplayName()
    {
        return displayName;
    }
    public void setDisplayName(final String displayName)
    {
        this.displayName = displayName;
    }
    public String getBio()
    {
        return bio;
    }
    public void setBio(final String bio)
    {
        this.bio = bio;
    }

    public UserFriend getFriend()
    {
        return friend;
    }

    public void setFriend(final UserFriend friend)
    {
        this.friend = friend;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
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
        final User other = (User) obj;
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
        return true;
    }

}
