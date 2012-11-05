package dbapi.test.system.model;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBEntity;

@DBEntity
public class UserFriend
{
    @DBColumn
    private String id;

    @DBColumn
    private String username;

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

}
