package dbapi.kernel;

import java.util.Set;

import com.google.common.base.Preconditions;

import dbapi.api.DBConfig;
import dbapi.api.DBType;

/**
 * 
 * @author alex
 *
 */
public class KernelContext
{
    private final DBConfig config;
    private final DBType dbType;
    private final Set<Class<?>> entities;

    public KernelContext(final DBConfig config, final Set<Class<?>> entities)
    {
        this.config = config;
        this.entities = entities;
        dbType = config.getType();
    }

    public DBConfig getConfig()
    {
        return config;
    }

    public Set<Class<?>> getEntities()
    {
        return entities;
    }

    public DBType getDbType()
    {
        return dbType;
    }

    public void validate()
    {
        Preconditions.checkArgument(null != dbType);

    }


}
