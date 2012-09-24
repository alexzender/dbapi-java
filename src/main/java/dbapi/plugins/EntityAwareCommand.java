package dbapi.plugins;

import dbapi.kernel.annotation.AnnotatedEntity;

/**
 * 
 * @author alex
 *
 */
public interface EntityAwareCommand
    extends DBPluginCommand
{
    void execute(Object entity, AnnotatedEntity definition);
}
