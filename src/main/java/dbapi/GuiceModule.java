package dbapi;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

import dbapi.kernel.EntityManagerFactoryImpl;
import dbapi.kernel.EntityManagerImpl;
import dbapi.kernel.Kernel;
import dbapi.kernel.KernelService;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotationService;
import dbapi.kernel.annotation.AnnotatedEntityIndexer;
import dbapi.kernel.query.QueryBuilder;
import dbapi.kernel.query.QueryConditionFinder;
import dbapi.kernel.query.QueryService;
import dbapi.plugins.DBPlugin;
import dbapi.plugins.cassandra.CassandraConnector;
import dbapi.plugins.cassandra.CassandraDBPlugin;
import dbapi.plugins.cassandra.CassandraPersist;
import dbapi.plugins.cassandra.ObjectIO;
import dbapi.plugins.cassandra.Utils;
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
       bind(EntityManager.class).to(EntityManagerImpl.class);
       
       bind(EntityManagerFactoryImpl.class);
       bind(Kernel.class);
       bind(EntityManagerImpl.class);
       
       Multibinder<KernelService> services = Multibinder.newSetBinder(binder(), KernelService.class);
       services.addBinding().to(AnnotationService.class);
       services.addBinding().to(QueryService.class);
              
       bind(AnnotatedEntityIndexer.class);
       bind(QueryBuilder.class);
       bind(QueryConditionFinder.class);
       
       bind(CassandraDBPlugin.class);
       bind(CassandraConnector.class);
       bind(ObjectIO.class);
       bind(CassandraPersist.class);
       
       MapBinder<String, DBPlugin> plugins = MapBinder.newMapBinder(binder(), String.class, DBPlugin.class);
       plugins.addBinding(CassandraDBPlugin.ID).to(CassandraDBPlugin.class);
       
    }
    
}
