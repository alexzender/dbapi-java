package dbapi.plugins.cassandra;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import dbapi.api.KernelException;
import dbapi.plugins.DBPluginContext;

/**
 * 
 * @author alex
 *
 */
public class CassandraConnector
{
    private static final Logger log = Logger.getLogger(CassandraConnector.class);

    public static final String CFG_KEYSPACE = "plugins.cassandra.keyspace";
    public static final String CFG_HOST = "plugins.cassandra.host";
    public static final String CFG_PORT = "plugins.cassandra.port";

    public String getKeyspace(final DBPluginContext ctx)
    {
        return ctx.getConfig().getDatabase();
    }

    public String getHost(final DBPluginContext ctx)
    {
        return ctx.getConfig().getHost();
    }

    public int getPort(final DBPluginContext ctx)
    {
        return ctx.getConfig().getPort();
    }

    public CassandraConnection connect(final DBPluginContext ctx)
    {
        validate(ctx);

        final TTransport transport = new TFramedTransport(new TSocket(getHost(ctx), getPort(ctx)));
        final TProtocol protocol = new TBinaryProtocol(transport);
        final Cassandra.Client client = new Cassandra.Client(protocol);


        try
        {
            transport.open();
            client.set_keyspace(getKeyspace(ctx));
        }
        catch (final InvalidRequestException e)
        {
            log.error("Invalid request to Cassandra", e);
            throw new KernelException("Invalid request to Cassandra", e);
        }
        catch (final TException e)
        {
            log.error("Failed to connect to Cassandra", e);
            throw new KernelException("Failed to connect to Cassandra", e);
        }

        final CassandraConnection res = new CassandraConnection(transport, client);

        return res;
    }

    private void validate(final DBPluginContext ctx)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(getKeyspace(ctx)), "Cassandra keyspace is missed in the configuration");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(getHost(ctx)), "Cassandra host is missed in the configuration");
        getPort(ctx);
    }
}
