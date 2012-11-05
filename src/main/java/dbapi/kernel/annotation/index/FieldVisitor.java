package dbapi.kernel.annotation.index;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Inject;

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
    @Inject
    private AnnotatedEntityIndexer indexer;

    public void visitMethod(final Method method, final AnnotatedField field)
    {
        Preconditions.checkArgument(null != method, "Valid method is required");
        Preconditions.checkArgument(null != field, "Valid field is required");

        final DBColumn columnAnn = method.getAnnotation(DBColumn.class);


        int length = 0;
        String column = null;
        if (null != columnAnn)
        {
            column = columnAnn.name();
            length = columnAnn.length();
        }
        if (Strings.isNullOrEmpty(column))
        {
            column = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
        }

        final DBLob lob = method.getAnnotation(DBLob.class);
        if(null != lob)
        {
            field.setLob(true);
        }

        field.setColumn(column);

        //avoid default @Column length
        if(length != 255)
        {
            field.setLength(length);
        }

        if (field.isComplexType())
        {
            final AnnotatedEntity def = indexer.build(field.getType());
            field.setComplexDef(def);
        }
    }

    public void visitField(final Field field, final AnnotatedEntity ann)
    {
        final DBColumn columnAnn = field.getAnnotation(DBColumn.class);
        final DBLob lobAnn = field.getAnnotation(DBLob.class);

        if(null != columnAnn)
        {
            boolean newbie = false;
            AnnotatedField fieldIndex = ann.getFields().get(field.getName());

            if(fieldIndex == null)
            {
                fieldIndex = new AnnotatedField();
                newbie = true;
            }

            final String column = columnAnn.name();
            if(!Strings.isNullOrEmpty(column))
            {
                fieldIndex.setColumn(column);
            }

            final int length = columnAnn.length();
            fieldIndex.setLength(length);

            if(lobAnn != null)
            {
                fieldIndex.setLob(true);
            }

            if(newbie)
            {
                fieldIndex.setClassField(field);

                if (fieldIndex.isComplexType())
                {
                    final AnnotatedEntity def = indexer.build(fieldIndex.getType());
                    fieldIndex.setComplexDef(def);
                }

                ann.getFields().put(field.getName(), fieldIndex);
            }
        }
    }
}
