package dbapi.kernel.query;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author alex
 *
 */
@SuppressWarnings("serial")
public class QueryCache
    extends LinkedHashMap<String, DBQuery>
{
    //TODO: expose this to outside world via property file configuration and Guice
    private static final int QUERY_CACHE_MAX_ENTRIES = 500;
    
    public QueryCache()
    {
        super(32, 0.75f, true);
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, DBQuery> pairEntry)
    {
        return this.size() > QUERY_CACHE_MAX_ENTRIES;
    }
}
