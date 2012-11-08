package dbapi.plugins;

import java.util.List;

import dbapi.api.DBQuery;
import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public interface QueryCommand
extends DBPluginCommand
{
    <T> List<T> query(DBQuery<T> query, AnnotatedEntity def);
}
