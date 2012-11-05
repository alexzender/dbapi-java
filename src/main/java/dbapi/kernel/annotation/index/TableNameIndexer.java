package dbapi.kernel.annotation.index;

import com.google.common.base.Strings;

import dbapi.api.meta.DBTable;
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
        final DBTable tbl = cls.getAnnotation(DBTable.class);

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
