package dbapi.plugins.cassandra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.common.base.Preconditions;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
import dbapi.plugins.DBPluginException;
import dbapi.plugins.PersistCommand;
/**
 * 
 * @author alex
 *
 */
public class CassandraPersist
    extends CassandraCommand
    implements PersistCommand
{
    private static final Logger log = Logger.getLogger(CassandraPersist.class);
    
    
    @Inject
    private ObjectIO objectIO;
    
    @Override
    public void execute(Object entity, AnnotatedEntity entityDef)
    {   
        Preconditions.checkArgument(null != entity, "Valid entity object is required");
        
        log.trace("Start saving new object into cassandra");
        
        Cassandra.Client client = getConnection().getClient();
        
        // define column parent
        //ColumnParent parent = new ColumnParent(annotated.getTable());

        Long id = System.nanoTime(); 
        
        if(log.isDebugEnabled())
            log.debug("New object id = " + id);
        
        entityDef.setId(entity, id);
        
        // define row id
        ByteBuffer rowid = ByteBuffer.allocate(8).putLong(id);
        rowid.position(0);
        List<Mutation> mutations = new ArrayList<Mutation>();
        long date = System.currentTimeMillis();
        for(AnnotatedField field : entityDef.getFields().values())
        {
            Column dataColumn = new Column();
            dataColumn.setName(field.getColumn().getBytes());
            dataColumn.setValue(field.getValue(entity));
            dataColumn.setTimestamp(date);
            
            ColumnOrSuperColumn col = new ColumnOrSuperColumn();
            col.setColumn(dataColumn);
            
            Mutation m = new Mutation();
            m.setColumn_or_supercolumn(col);

            mutations.add(m);
        }
        
        
        Map<String, List<Mutation>> columnFamilyMutations = new HashMap<String, List<Mutation>>();
        columnFamilyMutations.put(entityDef.getTable(), mutations);
        
        Map<ByteBuffer, Map<String, List<Mutation>>> mutationsMap = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
        
        mutationsMap.put(rowid, columnFamilyMutations);
        
        try
        {
            client.batch_mutate(mutationsMap, ConsistencyLevel.ONE);
            //getConnection().getTransport().flush();
            
           
            
            if(log.isDebugEnabled())
                log.debug("Saved successfully object by id = " + id);
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
