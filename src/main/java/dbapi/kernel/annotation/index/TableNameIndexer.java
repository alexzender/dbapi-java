package dbapi.kernel.annotation.index;

import com.google.common.base.Strings;

import dbapi.api.meta.DBEntity;
import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public class TableNameIndexer
{
    public void visit(final Class<?> cls, final AnnotatedEntity entity)
    {
        String tableName = null;
        final DBEntity tbl = cls.getAnnotation(DBEntity.class);

        if(null != tbl)
        {
            tableName = tbl.table();

        }

        if(Strings.isNullOrEmpty(tableName))
        {
            tableName = cls.getSimpleName();
        }

        entity.setTable(tableName);
    }
}
