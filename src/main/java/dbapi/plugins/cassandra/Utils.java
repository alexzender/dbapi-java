package dbapi.plugins.cassandra;

import java.nio.ByteBuffer;
/**
 * 
 * @author alex
 *
 */
public class Utils
{
    public static ByteBuffer longToByteBuffer(Long  value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(8).putLong(value);
        buffer.position(0);
        return buffer;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T bytesToTypedValue(Class<T> cls, byte[] bytes) throws InstantiationException, IllegalAccessException
    {
        T empty = null;
        
        if(cls.isAssignableFrom(String.class))
        {
            empty = (T) new String(bytes);
        }
        else if(cls.isAssignableFrom(Long.class))
        {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            empty = (T) Long.valueOf(buffer.getLong());
        }
        
        return empty;
    }
}
