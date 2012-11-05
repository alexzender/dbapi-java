package dbapi.plugins.mongodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
import dbapi.plugins.FindCommand;

/**
 * 
 * @author alex
 * 
 *         Nov 4, 2012
 */
public class MongoFind extends MongoCommand implements FindCommand
{
    private final Logger log = Logger.getLogger(this.getClass());
    @Override
    public <T> T find(final Class<T> cls, final AnnotatedEntity def, final String id)
    {
        final DBCollection coll = getDB().getCollection(def.getTable());

        final DBObject query = new BasicDBObject("_id", new ObjectId(id));

        final DBObject target = coll.findOne(query);

        if (null == target)
        {
            return null;
        }

        T res = null;

        try
        {
            res = cls.newInstance();

            for (final String key : target.keySet())
            {
                if("_id".equals(key))
                {
                    def.setId(res, ObjectId.class.cast(target.get(key)).toStringMongod());
                    continue;
                }

                final AnnotatedField field = def.getFields().get(key);

                if (null == field)
                {
                    log.warn("Failed to find a column " + key + " in class " + cls.getName() + " when fetching from Mongo");
                    continue;
                }

                if (field.isId())
                {
                    continue;
                }

                Object value = target.get(key);

                if (field.isComplexType())
                {
                    final Object complexObj = field.getType().newInstance();
                    final DBObject complexRet = (DBObject) value;
                    for (final String complexKey : complexRet.keySet())
                    {
                        final AnnotatedField complexField = field.getComplexDef().getFields().get(complexKey);

                        if (null == complexField)
                        {
                            log.warn("Failed to find a column " + key + " in class " + cls.getName() + " when fetching from Mongo");
                            continue;
                        }

                        if (complexField.isComplexType())
                        {
                            log.warn("Skipping complex field of 2nd level  -  column " + key + " in class " + cls.getName()
                                    + " when fetching from Mongo");
                            continue;
                        }

                        final Method setter = complexField.getSetter();
                        setter.invoke(complexObj, complexRet.get(complexKey));

                    }
                    value = complexObj;
                }


                final Method setter = field.getSetter();
                setter.invoke(res, value);

            }

        }
        catch (final InstantiationException e)
        {
            throw new KernelException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new KernelException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new KernelException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new KernelException(e);
        }

        return res;
    }

}
