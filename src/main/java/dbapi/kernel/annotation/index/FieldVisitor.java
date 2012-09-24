package dbapi.kernel.annotation.index;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Lob;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotatedField;
/**
 * 
 * @author alex
 *
 */
public class FieldVisitor
   
{
    public void visitMethod(Method method, AnnotatedField field)
    {
        Preconditions.checkArgument(null != method, "Valid method is required");
        Preconditions.checkArgument(null != field, "Valid field is required");
        
        Column columnAnn = method.getAnnotation(Column.class);
        
        
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

        Lob lob = method.getAnnotation(Lob.class);
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
    }
    
    public void visitField(Field field, AnnotatedEntity ann)
    {
        Column columnAnn = field.getAnnotation(Column.class);
        Lob lobAnn = field.getAnnotation(Lob.class);
        
        if(null != columnAnn)
        {
            boolean newJpaField = false;
            AnnotatedField jpaField = ann.getFields().get(field.getName());
            
            if(jpaField == null)
            {
                jpaField = new AnnotatedField();
                newJpaField = true;
            }
            
            String column = columnAnn.name();
            String columnDefinition = columnAnn.columnDefinition();
            int length = columnAnn.length();
            
            if(!Strings.isNullOrEmpty(column))
            {
                jpaField.setColumn(column);
            }
            
            if(!Strings.isNullOrEmpty(columnDefinition))
            {
                jpaField.setColumnDefinition(columnDefinition);
            }
            
            //support default @Column length 
            if(length != 255)
            {
                jpaField.setLength(length);
            }
            
            if(lobAnn != null)
            {
                jpaField.setLob(true);
            }
            
            if(newJpaField)
            {
                jpaField.setClassField(field);
                ann.getFields().put(field.getName(), jpaField);
            }
        }
    }
}
