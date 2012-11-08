package dbapi.kernel.annotation.index;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import dbapi.api.meta.DBColumn;
import dbapi.api.meta.DBLob;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedEntityIndexer;
import dbapi.kernel.annotation.AnnotatedField;
/**
 * 
 * @author alex
 *
 */
public class FieldVisitor

{
    private final Logger log = Logger.getLogger(this.getClass());
    @Inject
    private AnnotatedEntityIndexer indexer;

    public AnnotatedField visitMethod(final Method method, final AnnotatedEntity ann)
    {
        Preconditions.checkArgument(null != method, "Valid method is required");
        Preconditions.checkArgument(null != ann, "Valid field is required");

        final DBColumn columnAnn = method.getAnnotation(DBColumn.class);
        final String name = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

        final AnnotatedField index = visitDeclaration(ann, method.getReturnType(),
                method.getGenericReturnType(),
                name,
                columnAnn,
                method.getAnnotation(DBLob.class));

        if(null != index)
        {
            index.setGetter(method);
        }

        return index;
    }

    public AnnotatedField visitDeclaration(final AnnotatedEntity ann,
            final Class<?> member,
            final Type genericInfo,
            final String name,
            final DBColumn columnAnn,
            final DBLob lobAnn)
    {
        if(null == columnAnn)
        {
            return null;
        }

        AnnotatedField fieldIndex = ann.getFields().get(name);
        // already processed?
        if (null != fieldIndex)
        {
            return null;
        }

        fieldIndex = new AnnotatedField();
        fieldIndex.setLength(columnAnn.length());
        fieldIndex.setType(member);

        final String column = columnAnn.name();
        if(!Strings.isNullOrEmpty(column))
        {
            fieldIndex.setColumn(column);
        }
        else
        {
            fieldIndex.setColumn(name);
        }

        if(lobAnn != null)
        {
            fieldIndex.setLob(true);
        }

        if (fieldIndex.isCollection())
        {
            if(null != genericInfo && genericInfo instanceof ParameterizedType)
            {
                final ParameterizedType paramType = (ParameterizedType) genericInfo;
                final Class<?> genCls = (Class<?>) paramType.getActualTypeArguments()[0];
                final AnnotatedEntity def = indexer.build(genCls);
                fieldIndex.setComplexDef(def);
            }
            else
            {
                log.warn("Failed to process generic parameter declaration. Skipping field " + name);
            }
        }
        if (fieldIndex.isComplexType())
        {
            final AnnotatedEntity def = indexer.build(fieldIndex.getType());
            fieldIndex.setComplexDef(def);
        }

        ann.getFields().put(name, fieldIndex);


        return fieldIndex;

    }

    public AnnotatedField visitField(final Field field, final AnnotatedEntity ann)
    {
        final DBColumn columnAnn = field.getAnnotation(DBColumn.class);
        final DBLob lobAnn = field.getAnnotation(DBLob.class);

        final AnnotatedField fieldIndex = visitDeclaration(ann, field.getType(), field.getGenericType(),
                field.getName(), columnAnn, lobAnn);
        if(null != fieldIndex)
        {
            fieldIndex.setClassField(field);
        }

        return fieldIndex;

    }
}
