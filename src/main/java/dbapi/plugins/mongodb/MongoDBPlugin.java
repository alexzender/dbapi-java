package dbapi.plugins.mongodb;

import java.net.UnknownHostException;

import javax.inject.Inject;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import dbapi.api.KernelException;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.DBPluginContext;
import dbapi.plugins.FindCommand;
import dbapi.plugins.PersistCommand;
import dbapi.plugins.QueryCommand;
import dbapi.plugins.RemoveCommand;
import dbapi.plugins.SchemaCreateCommand;
import dbapi.plugins.SchemaDeleteCommand;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class MongoDBPlugin implements DBPlugin
{
    private Mongo mongo;
    private DB db;

    @Inject
    private MongoPersist save;

    @Inject
    private MongoFind get;

    @Inject
    private MongoRemove delete;

    @Override
    public void init(final DBPluginContext ctx)
    {
        try
        {
            mongo = new Mongo(ctx.getConfig().getHost());
            mongo.setWriteConcern(WriteConcern.SAFE);
            db = mongo.getDB(ctx.getConfig().getDatabase());
        }
        catch (final UnknownHostException e)
        {
            throw new KernelException(e);
        }
        catch (final MongoException e)
        {
            throw new KernelException(e);
        }

        save.init(ctx);
        save.setDB(db);

        get.init(ctx);
        get.setDB(db);

        delete.init(ctx);
        delete.setDB(db);
    }

    @Override
    public void flush()
    {
        mongo.fsync(false);
    }

    @Override
    public void close()
    {
        mongo.close();
    }

    @Override
    public String getId()
    {
        return "mongodb";
    }

    @Override
    public FindCommand getFindCommand()
    {
        return get;
    }

    @Override
    public PersistCommand getPersistCommand()
    {
        return save;
    }

    @Override
    public RemoveCommand getRemoveCommand()
    {
        return delete;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SchemaDeleteCommand getSchemaDeleteCommand()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
