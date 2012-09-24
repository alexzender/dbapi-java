package dbapi.plugins.cassandra;

import java.nio.ByteBuffer;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.plugins.DBPluginException;
import dbapi.plugins.RemoveCommand;
/**
 * 
 * @author alex
 *
 */
public class CassandraRemove
    extends CassandraCommand
    implements RemoveCommand
{

    @Override
    public void execute(Object entity , AnnotatedEntity entityDef)
    {       
        Cassandra.Client client = getConnection().getClient();
        
        ByteBuffer id = entityDef.getIdBytes(entity);
        
        ColumnPath path = new ColumnPath(entityDef.getTable());
        
        try
        {   
            client.remove(id, path, System.currentTimeMillis(), ConsistencyLevel.ONE);
        }
        catch (InvalidRequestException e)
        {
           throw new DBPluginException(e);
        }
        catch (UnavailableException e)
        {
            throw new DBPluginException(e);
        }
        catch (TimedOutException e)
        {
            //TODO: retry
            throw new DBPluginException(e);
        }
        catch (TException e)
        {
            throw new DBPluginException(e);
        }        
        
    }


}
