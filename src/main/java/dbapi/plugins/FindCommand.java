package dbapi.plugins;

import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public interface FindCommand
    extends DBPluginCommand
{
    <T> T find(Class<T> cls, AnnotatedEntity def, Object id);
}
