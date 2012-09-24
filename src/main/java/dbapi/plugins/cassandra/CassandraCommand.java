package dbapi.plugins.cassandra;

import dbapi.plugins.DBPluginCommand;
import dbapi.plugins.DBPluginContext;

/**
 * 
 * @author alex
 *
 */
public abstract class CassandraCommand
    implements DBPluginCommand
{
    private DBPluginContext ctx;
    private CassandraConnection connection;
    
    public void setConnection(CassandraConnection conn)
    {
        this.connection = conn;
    }
    
    @Override
    public void init(DBPluginContext ctx)
    {
        this.ctx = ctx;        
    }

    DBPluginContext getCtx()
    {
        return ctx;
    }

    public CassandraConnection getConnection()
    {
        return connection;
    }

    
}
