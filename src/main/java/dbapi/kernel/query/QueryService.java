package dbapi.kernel.query;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

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

    @Inject
    private Injector guice;

    @Inject
    private AnnotationService annotations;


    public QueryService()
    {
    }



    @Override
    public void init(final Kernel kernel)
    {
        namedQueries = new HashMap<String, String>();

        final Set<Class<?>> classes =kernel.getContext().getEntities();
        for(final Class<?> aclass : classes)
        {
            final Annotation[] annotations = aclass.getAnnotations();
            for(final Annotation ann : annotations)
            {

            }
        }
    }






}
