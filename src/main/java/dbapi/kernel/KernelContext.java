package dbapi.kernel;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 
 * @author alex
 *
 */
public class KernelContext
{
    private static final String CONF_DB_TYPE = "db.type";
    private Map<String, String> config;
    private String dbType;
    private Set<Class<?>> entities;
    
    public KernelContext(Map<String, String> config, Set<Class<?>> entities)
    {        
        this.config = config;
        this.entities = entities;
        dbType = config.get(CONF_DB_TYPE);
    }
    
    public Map<String, String> getConfig()
    {
        return config;
    }
    
    public Set<Class<?>> getEntities()
    {
        return entities;
    }
    
    public String getDbType()
    {
        return dbType;
    }

    public void validate()
    {
       Preconditions.checkArgument(!Strings.isNullOrEmpty(dbType));
        
    }
    
    
}
