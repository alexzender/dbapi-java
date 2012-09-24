package dbapi.kernel.annotation.index;

import javax.persistence.Table;

import com.google.common.base.Strings;

import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public class TableNameIndexer
{
    public void visit(Class<?> cls, AnnotatedEntity entity)
    {
        String tableName = null;
        Table tbl = (Table) cls.getAnnotation(Table.class);
        
        if(null != tbl)
        {
            tableName = tbl.name();
           
        }
        
        if(Strings.isNullOrEmpty(tableName))
        {
            tableName = cls.getCanonicalName();
        }
        
        entity.setTable(tableName);
    }
}
