package dbapi.plugins.mongodb;

import com.mongodb.DB;

import dbapi.plugins.DBPluginCommand;
import dbapi.plugins.DBPluginContext;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public abstract class MongoCommand
implements DBPluginCommand
{
    private DBPluginContext ctx;
    private DB db;

    @Override
    public void init(final DBPluginContext ctx)
    {
        this.ctx = ctx;
    }

    DBPluginContext getCtx()
    {
        return ctx;
    }

    public DB getDB()
    {
        return db;
    }

    public void setDB(final DB db)
    {
        this.db = db;
    }



}
