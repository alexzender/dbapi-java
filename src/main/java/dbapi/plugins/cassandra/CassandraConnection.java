package dbapi.plugins.cassandra;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.transport.TTransport;

/**
 * 
 * @author alex
 *
 */
public class CassandraConnection
{
    private TTransport transport;
    private Cassandra.Client client;
    
    public CassandraConnection(TTransport transport, Client client)
    {        
        this.transport = transport;
        this.client = client;
    }
    public TTransport getTransport()
    {
        return transport;
    }
    public Cassandra.Client getClient()
    {
        return client;
    }        
}
