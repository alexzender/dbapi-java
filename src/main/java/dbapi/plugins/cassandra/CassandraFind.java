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
import dbapi.plugins.FindCommand;
/**
 * 
 * @author alex
 *
 */
public class CassandraFind
    extends CassandraCommand
    implements FindCommand
{

    @Override
    public <T> T find(Class<T> cls, AnnotatedEntity def, Object id)
    {
        CassandraConnection client = getConnection();
        
        ColumnParent columnParent = new ColumnParent(def.getTable());
        
        SlicePredicate predicate = new SlicePredicate();
        predicate.setSlice_range(new SliceRange(ByteBuffer.wrap(new byte[0]), ByteBuffer.wrap(new byte[0]), false, 100));

        ByteBuffer key = null;
        
        //FIXME: this is not good
        if(id instanceof Long)
        {
            key = Utils.longToByteBuffer((Long) id);
        }
        
        T res = null;
         
        try
        {
            List<ColumnOrSuperColumn> columns = client.getClient().get_slice(key, columnParent, predicate, ConsistencyLevel.ONE);
            
            if(null == columns || columns.isEmpty())
            {
                return res;
            }
            
            res = cls.newInstance();
            
            for(ColumnOrSuperColumn column : columns)
            {
                byte[] nameBytes = column.getColumn().getName();
                String name = new String(nameBytes);
                
                AnnotatedField field = def.getFields().get(name);
                
                byte[] valueBytes = column.getColumn().getValue();
                
                
                Method setter = field.getSetter();
                setter.invoke(res, Utils.bytesToTypedValue(field.getType(), valueBytes));
            }
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
        catch (InstantiationException e)
        {
            throw new DBPluginException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new DBPluginException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new DBPluginException(e);        }
        catch (InvocationTargetException e)
        {
            throw new DBPluginException(e);
        }       
        
        return res;
    }



}
