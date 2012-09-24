package dbapi.kernel.query;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NamedQuery;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

import dbapi.kernel.Kernel;
import dbapi.kernel.KernelService;
import dbapi.kernel.annotation.AnnotationService;

/**
 * 
 * @author alex
 *
 */
public class QueryService
    implements KernelService
{
    private static final Logger log = Logger.getLogger(QueryService.class);
    
    private Map<String, String> namedQueries;
        
    @SuppressWarnings("serial")
    private static Map<String, DBQuery> queryCache = Collections.synchronizedMap(new QueryCache());
    
    private Kernel kernel;
    
    @Inject
    private Injector guice;
    
    @Inject
    private AnnotationService annotations;
    
    @Inject
    private QueryBuilder queryBuilder;
                                                    
    public QueryService() 
    {        
    }

    public String getNamedQuery(String queryname)
    {
        return namedQueries.get(queryname);
    }
    
    @Override
    public void init(Kernel kernel)
    {
        this.kernel = kernel;
        namedQueries = new HashMap<String, String>();
        
        Set<Class<?>> classes =kernel.getContext().getEntities();
        for(Class<?> aclass : classes)
        {           
            Annotation[] annotations = aclass.getAnnotations();
            for(Annotation ann : annotations)
            if(ann.annotationType() == NamedQuery.class)
            {
                NamedQuery nq = (NamedQuery)ann;
                namedQueries.put(nq.name(), nq.query());
            }
        }
    }
    
    
    public DBQuery parseQueryString(String query)
    {
        DBQuery res = null;
        if(!queryCache.containsKey(query))
        {
            res = queryBuilder.build(query, annotations);
            queryCache.put(query, res);
        }
       
        res = queryCache.get(query).clone(kernel);

        return res;
    }

            

}
