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
import dbapi.plugins.DeleteCommand;
import dbapi.plugins.GetCommand;
import dbapi.plugins.QueryCommand;
import dbapi.plugins.SaveCommand;
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
    private MongoSave save;

    @Inject
    private MongoGet get;

    @Inject
    private MongoDelete delete;

    @Inject
    private MongoQuery query;

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

        query.init(ctx);
        query.setDB(db);
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
    public GetCommand getFindCommand()
    {
        return get;
    }

    @Override
    public SaveCommand getPersistCommand()
    {
        return save;
    }

    @Override
    public DeleteCommand getRemoveCommand()
    {
        return delete;
    }

    @Override
    public QueryCommand getQueryCommand()
    {
        return query;
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
