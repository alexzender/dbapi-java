package dbapi.plugins.mongodb;

import org.bson.types.ObjectId;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.plugins.RemoveCommand;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class MongoRemove extends MongoCommand implements RemoveCommand
{

    @Override
    public void execute(final Object entity, final AnnotatedEntity def)
    {
        final DBCollection coll = getDB().getCollection(def.getTable());

        final DBObject query = new BasicDBObject("_id", new ObjectId(String.class.cast(def.getId(entity))));

        final WriteResult res = coll.remove(query);

        final String error = res.getError();

        if (!Strings.isNullOrEmpty(error))
        {
            throw new KernelException("Failed to delete entity " + def.getTable() + ". Reason: " + error);
        }

    }

}
