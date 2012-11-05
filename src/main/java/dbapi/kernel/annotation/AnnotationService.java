package dbapi.kernel.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import dbapi.api.meta.DBEntity;
import dbapi.kernel.Kernel;
import dbapi.kernel.KernelService;

/**
 * 
 * @author alex
 *
 */
public class AnnotationService
implements KernelService
{
    private static final Logger log = Logger.getLogger(AnnotationService.class);

    @Inject
    private AnnotatedEntityIndexer indexer;

    private final Map<String, AnnotatedEntity> managedEntities = new HashMap<String, AnnotatedEntity>();

    public Collection<AnnotatedEntity> getManagedEntities()
    {
        return managedEntities.values();
    }

    @Override
    public void init(final Kernel kernel)
    {
        final Set<Class<?>> entities = kernel.getContext().getEntities();

        Preconditions.checkState(null != entities && !entities.isEmpty(), "The list of entity classes is required.");

        for (final Class<?> cls : entities)
        {
            final DBEntity entityAnn = cls.getAnnotation(DBEntity.class);
            if (null != entityAnn)
            {
                log.debug("discovered persistent entity to manage: " + cls.getName());
                final AnnotatedEntity ann = create(cls);
                managedEntities.put(cls.getName(), ann);
            }

        }

    }

    public AnnotatedEntity create(final Class<?> cls)
    {
        return indexer.build(cls);
    }

    /**
     * Find the entity descriptor by a class name provided.
     * 
     * @param className path with package info included or the class name only
     * @return entity descriptor registered in the system or null otherwise
     */
    public AnnotatedEntity lookup(final String className)
    {
        AnnotatedEntity ann = managedEntities.get(className);

        if (null == ann)
        {
            final String classSuffix = "." + className;
            final String classImplSuffix = "." + className + "Impl";

            for (final String entityName : managedEntities.keySet())
            {
                if (entityName.endsWith(classSuffix) || entityName.endsWith(classImplSuffix))
                {
                    ann = managedEntities.get(entityName);
                    break;
                }
            }
        }
        return ann;
    }


}
