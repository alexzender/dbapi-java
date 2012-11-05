package dbapi.plugins.mongodb;

import org.bson.types.ObjectId;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.plugins.PersistCommand;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class MongoPersist extends MongoCommand implements PersistCommand
{

    @Override
    public void execute(final Object entity, final AnnotatedEntity def)
    {
        final DBCollection coll = getDB().getCollection(def.getTable());

        final BasicDBObject doc = object2doc(entity, def);

        final WriteResult res = coll.insert(doc);

        final String error = res.getError();

        if (!Strings.isNullOrEmpty(error))
        {
            throw new KernelException("Failed to save entity " + def.getTable() + ". Reason: " + error);
        }

        final ObjectId id = ObjectId.class.cast(doc.get("_id"));

        def.setId(entity, id.toStringMongod());

    }



}
