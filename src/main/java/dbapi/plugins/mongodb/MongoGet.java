package dbapi.plugins.mongodb;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.plugins.GetCommand;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class MongoGet extends MongoCommand implements GetCommand
{
    private final Logger log = Logger.getLogger(this.getClass());

    @Inject
    private MongoSerializer serializer;

    @Override
    public <T> T find(final Class<T> cls, final AnnotatedEntity def, final String id)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Getting object by id = " + id);
        }
        final DBCollection coll = getDB().getCollection(def.getTable());

        final DBObject query = new BasicDBObject("_id", new ObjectId(id));

        final DBObject target = coll.findOne(query);

        if (null == target)
        {
            return null;
        }

        if (log.isTraceEnabled())
        {
            log.trace("Object found by id = " + id + ". Deserializing...");
        }

        final T res = serializer.doc2obj(cls, def, target);

        return res;
    }


}
