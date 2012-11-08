package dbapi.plugins.cassandra;

import javax.inject.Inject;

import org.apache.thrift.transport.TTransportException;

import dbapi.api.UnsupportedFeatureException;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.DBPluginContext;
import dbapi.plugins.DBPluginException;
import dbapi.plugins.DeleteCommand;
import dbapi.plugins.GetCommand;
import dbapi.plugins.SaveCommand;
import dbapi.plugins.QueryCommand;
import dbapi.plugins.SchemaCreateCommand;
import dbapi.plugins.SchemaDeleteCommand;

/**
 * 
 * @author alex
 *
 */
public class CassandraDBPlugin
    implements DBPlugin
{
    public static final String ID = "cassandra";
    
    private CassandraConnection connection;
    
    @Inject
    private CassandraPersist persist;
        
    @Inject
    private CassandraRemove remove;
    
    @Inject
    private CassandraFind find;
    
    @Inject
    private CassandraConnector connector;
        
    
    @Override
    public void init(DBPluginContext ctx)
    {
        connection = connector.connect(ctx);
        
        persist.init(ctx);
        persist.setConnection(connection);
        
        remove.init(ctx);
        remove.setConnection(connection);
        
        find.init(ctx);
        find.setConnection(connection);
    }

    @Override
    public String getId()
    {
        return CassandraDBPlugin.ID;
    }

    @Override
    public GetCommand getFindCommand()
    {
        return find;
    }

    @Override
    public SaveCommand getPersistCommand()
    {
        return persist;
    }

    @Override
    public DeleteCommand getRemoveCommand()
    {        
        return remove;
    }

    @Override
    public QueryCommand getQueryCommand()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SchemaCreateCommand getSchemaCreateCommand()
    {   
        throw new UnsupportedFeatureException("Use cassandra-cli to manage column families");
    }

    @Override
    public SchemaDeleteCommand getSchemaDeleteCommand()
    {
        throw new UnsupportedFeatureException("Use cassandra-cli to manage column families");
    }

    @Override
    public void flush()
    {
        try
        {
            connection.getTransport().flush();
        }
        catch (TTransportException e)
        {
            throw new DBPluginException(e);
        }
        
    }

    @Override
    public void close()
    {
        connection.getTransport().close();        
    }

}
