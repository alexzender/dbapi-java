package dbapi.plugins;

import dbapi.api.DBConfig;

/**
 * 
 * @author alex
 *
 */
public class DBPluginContext
{
    private final DBConfig config;

    public DBPluginContext(final DBConfig config)
    {
        this.config = config;
    }

    public DBConfig getConfig()
    {
        return config;
    }
}
