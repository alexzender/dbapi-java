package dbapi.plugins;

import java.util.Map;

/**
 * 
 * @author alex
 *
 */
public class DBPluginContext
{
    private Map<String, String> config;
    
    
    
    public DBPluginContext(Map<String, String> config)
    {     
        this.config = config;
    }



    public String getProperty(String name)
    {
        return config.get(name);
    }
}
