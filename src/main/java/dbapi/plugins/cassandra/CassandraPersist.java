package dbapi.plugins.cassandra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public void execute(final Object entity, final AnnotatedEntity entityDef)
    {
        Preconditions.checkArgument(null != entity, "Valid entity object is required");

        log.trace("Start saving new object into cassandra");

        final Cassandra.Client client = getConnection().getClient();

        // define column parent
        //ColumnParent parent = new ColumnParent(annotated.getTable());

        final String id = UUID.randomUUID().toString();

        if(log.isDebugEnabled())
        {
            log.debug("New object id = " + id);
        }

        entityDef.setId(entity, id);

        // define row id
        final ByteBuffer rowid = ByteBuffer.wrap(id.getBytes());
        rowid.position(0);
        final List<Mutation> mutations = new ArrayList<Mutation>();
        final long date = System.currentTimeMillis();
        for(final AnnotatedField field : entityDef.getFields().values())
        {
            final Column dataColumn = new Column();
            dataColumn.setName(field.getColumn().getBytes());
            dataColumn.setValue(field.getValueBytes(entity));
            dataColumn.setTimestamp(date);

            final ColumnOrSuperColumn col = new ColumnOrSuperColumn();
            col.setColumn(dataColumn);

            final Mutation m = new Mutation();
            m.setColumn_or_supercolumn(col);

            mutations.add(m);
        }


        final Map<String, List<Mutation>> columnFamilyMutations = new HashMap<String, List<Mutation>>();
        columnFamilyMutations.put(entityDef.getTable(), mutations);

        final Map<ByteBuffer, Map<String, List<Mutation>>> mutationsMap = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();

        mutationsMap.put(rowid, columnFamilyMutations);

        try
        {
            client.batch_mutate(mutationsMap, ConsistencyLevel.ONE);
            //getConnection().getTransport().flush();



            if(log.isDebugEnabled())
            {
                log.debug("Saved successfully object by id = " + id);
            }
        }
        catch (final InvalidRequestException e)
        {
            throw new DBPluginException(e);
        }
        catch (final UnavailableException e)
        {
            throw new DBPluginException(e);
        }
        catch (final TimedOutException e)
        {
            //TODO: retry
            throw new DBPluginException(e);
        }
        catch (final TException e)
        {
            throw new DBPluginException(e);
        }

    }

}
