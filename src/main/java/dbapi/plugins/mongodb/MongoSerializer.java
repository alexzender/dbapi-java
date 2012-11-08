package dbapi.plugins.mongodb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import dbapi.api.KernelException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;

/**
 * 
 * @author alex
 * 
 *         Nov 5, 2012
 */
@Singleton
public class MongoSerializer
{
    private final Logger log = Logger.getLogger(this.getClass());
    public <T> T doc2obj(final Class<T> cls, final AnnotatedEntity def, final DBObject target)
    {
        return (T) doc2rawObj(cls, def, target);
    }
    public Object doc2rawObj(final Class cls, final AnnotatedEntity def, final DBObject target)
    {
        Object res = null;

        try
        {
            res = cls.newInstance();
            if(log.isDebugEnabled())
            {
                log.debug("Deserializing class " + cls.getName());
            }
            for (final String key : target.keySet())
            {
                if(log.isDebugEnabled())
                {
                    log.debug("\tDeserializing field " + key);
                }

                if ("_id".equals(key))
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
                else if(field.isCollection())
                {
                    final Collection<?> list = Collection.class.cast(value);
                    final Iterator<?> iter = list.iterator();
                    final ArrayList listRes = new ArrayList(list.size());
                    while(iter.hasNext())
                    {
                        final DBObject obj =  (DBObject) iter.next();
                        final Object bizObj = doc2rawObj(field.getComplexDef().getType(), field.getComplexDef(), obj);
                        listRes.add(bizObj);
                    }
                    value = listRes;
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

    public BasicDBObject object2doc(final Object entity, final AnnotatedEntity def)
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
                final Collection list = Collection.class.cast(field.getValue(entity));
                final ArrayList<DBObject> resList = new ArrayList<DBObject>(list.size());
                final Iterator iter = list.iterator();
                while(iter.hasNext())
                {
                    final Object next = iter.next();
                    final BasicDBObject kid = object2doc(next, field.getComplexDef());
                    resList.add(kid);
                }
                doc.put(field.getColumn(), resList);
            }
            else
            {
                doc.put(field.getColumn(), field.getValue(entity));
            }
        }

        return doc;
    }

}
