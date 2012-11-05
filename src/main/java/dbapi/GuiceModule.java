package dbapi;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

import dbapi.api.DBSession;
import dbapi.api.DBType;
import dbapi.kernel.Kernel;
import dbapi.kernel.KernelService;
import dbapi.kernel.annotation.AnnotatedEntityIndexer;
import dbapi.kernel.annotation.AnnotationService;
import dbapi.kernel.query.QueryService;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.cassandra.CassandraConnector;
import dbapi.plugins.cassandra.CassandraDBPlugin;
import dbapi.plugins.cassandra.CassandraPersist;
import dbapi.plugins.cassandra.ObjectIO;
import dbapi.plugins.mongodb.MongoDBPlugin;
/**
 * 
 * @author alex
 *
 */
public class GuiceModule
extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(DBSession.class);

        bind(Kernel.class);
        bind(DBSession.class);

        final Multibinder<KernelService> services = Multibinder.newSetBinder(binder(), KernelService.class);
        services.addBinding().to(AnnotationService.class);
        services.addBinding().to(QueryService.class);

        bind(AnnotatedEntityIndexer.class);

        bind(CassandraDBPlugin.class);
        bind(CassandraConnector.class);
        bind(ObjectIO.class);
        bind(CassandraPersist.class);

        final MapBinder<DBType, DBPlugin> plugins = MapBinder.newMapBinder(binder(), DBType.class, DBPlugin.class);
        plugins.addBinding(DBType.CASSANDRA).to(CassandraDBPlugin.class);
        plugins.addBinding(DBType.MONGODB).to(MongoDBPlugin.class);

    }

}
