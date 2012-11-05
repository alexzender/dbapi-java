package dbapi.kernel;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;

import dbapi.api.DBConfig;
import dbapi.api.DBType;
import dbapi.kernel.annotation.AnnotationService;
import dbapi.kernel.query.QueryService;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.DBPluginContext;

/**
 * 
 * @author alex
 *
 */
public class Kernel
{
    private final static Logger log = Logger.getLogger(Kernel.class);

    private KernelContext context;

    @Inject
    private QueryService queryService;

    @Inject
    private AnnotationService annotationService;

    @Inject
    private Map<DBType, DBPlugin> plugins;

    @Inject
    private Injector injector;

    private DBPlugin db;

    public void init(final DBConfig config, final Set<Class<?>> entities)
    {
        Preconditions.checkArgument(null != config, "Valid configuration is required");
        Preconditions.checkArgument(null != entities && !entities.isEmpty(), "Valid list of entities to manage is required");

        context = new KernelContext(config, entities);
        context.validate();

        db = plugins.get(context.getDbType());

        Preconditions.checkArgument(null != db, "Failed to find a database plugin for " + context.getDbType());

        //TODO: filter out and pass only plug-in specific properties
        final DBPluginContext ctx = new DBPluginContext(config);

        db.init(ctx );


        log.debug("Initializing  annotation service...");
        annotationService.init(this);
        log.debug("Initialized annotations service.");

        log.debug("Initializing  query service...");
        queryService.init(this);
        log.debug("Initialized query service.");

    }

    public KernelContext getContext()
    {
        return context;
    }

    public DBPlugin getDb()
    {
        return db;
    }

    public QueryService getQueryService()
    {
        return queryService;
    }

    public AnnotationService getAnnotationService()
    {
        return annotationService;
    }

}
