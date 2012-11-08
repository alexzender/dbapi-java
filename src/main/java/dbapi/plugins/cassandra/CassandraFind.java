package dbapi.plugins.cassandra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
import dbapi.plugins.DBPluginException;
import dbapi.plugins.GetCommand;
/**
 * 
 * @author alex
 *
 */
public class CassandraFind
extends CassandraCommand
implements GetCommand
{

    @Override
    public <T> T find(final Class<T> cls, final AnnotatedEntity def, final String id)
    {
        final CassandraConnection client = getConnection();

        final ColumnParent columnParent = new ColumnParent(def.getTable());

        final SlicePredicate predicate = new SlicePredicate();
        predicate.setSlice_range(new SliceRange(ByteBuffer.wrap(new byte[0]), ByteBuffer.wrap(new byte[0]), false, 100));

        final ByteBuffer key = ByteBuffer.wrap(id.getBytes());

        T res = null;

        try
        {
            final List<ColumnOrSuperColumn> columns = client.getClient().get_slice(key, columnParent, predicate, ConsistencyLevel.ONE);

            if(null == columns || columns.isEmpty())
            {
                return res;
            }

            res = cls.newInstance();

            for(final ColumnOrSuperColumn column : columns)
            {
                final byte[] nameBytes = column.getColumn().getName();
                final String name = new String(nameBytes);

                final AnnotatedField field = def.getFields().get(name);

                final byte[] valueBytes = column.getColumn().getValue();


                final Method setter = field.getSetter();
                setter.invoke(res, Utils.bytesToTypedValue(field.getType(), valueBytes));
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
        catch (final InstantiationException e)
        {
            throw new DBPluginException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new DBPluginException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new DBPluginException(e);        }
        catch (final InvocationTargetException e)
        {
            throw new DBPluginException(e);
        }

        return res;
    }



}
