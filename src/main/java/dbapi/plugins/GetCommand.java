package dbapi.plugins;

import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public interface GetCommand extends DBPluginCommand
{
    <T> T find(Class<T> cls, AnnotatedEntity def, String id);
}
