package dbapi.api;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class DBConfig
{
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;
    private DBType type;

    public String getHost()
    {
        return host;
    }

    public void setHost(final String host)
    {
        this.host = host;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public DBType getType()
    {
        return type;
    }

    public void setType(final DBType type)
    {
        this.type = type;
    }

    public void selectCassandra()
    {
        this.type = DBType.CASSANDRA;
    }

    public void selectMongoDB()
    {
        this.type = DBType.MONGODB;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(final String database)
    {
        this.database = database;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(final int port)
    {
        this.port = port;
    }

}
