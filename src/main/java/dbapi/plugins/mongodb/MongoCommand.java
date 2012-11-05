package dbapi.plugins.mongodb;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
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

    BasicDBObject object2doc(final Object entity, final AnnotatedEntity def)
    {
        final BasicDBObject doc = new BasicDBObject();

        for (final AnnotatedField field : def.getFields().values())
        {
            if (field.isId())// ID field is transient as it uses value from
                // mongo's "_id"
            {
                if (field.getValue(entity) == null)
                {
                    continue;
                }
                else
                {
                    doc.put("_id", new ObjectId((String) field.getValue(entity)));
                }
            }

            if (field.isComplexType())
            {
                final BasicDBObject kid = object2doc(field.getValue(entity), field.getComplexDef());
                doc.put(field.getColumn(), kid);
            }
            else if (field.isCollection())
            {

            }
            else
            {
                doc.put(field.getColumn(), field.getValue(entity));
            }
        }

        return doc;
    }


}
