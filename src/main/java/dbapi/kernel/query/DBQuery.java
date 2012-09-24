package dbapi.kernel.query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import dbapi.api.KernelException;
import dbapi.kernel.Kernel;

/**
 * 
 * @author alex
 *
 */
public class DBQuery
    implements Query, Cloneable
{

    private String querySource;
    private String alias;
    private String entity;
    private boolean selectCount;
    
    private List<QueryCondition> conditions;
    
    private String orderByColumn;
    private OrderByType orderByType;
    
    private Kernel kernel;
    
    public DBQuery(Kernel kernel)
    {   
        this.kernel = kernel;
    }


    public enum OrderByType
    {
        ASC,
        DESC;
    }
    
    @Override
    public List getResultList()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getSingleResult()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int executeUpdate()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Query setMaxResults(int maxResult)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setFirstResult(int startPosition)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setHint(String hintName, Object value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(String name, Object value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(String name, Date value, TemporalType temporalType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(String name, Calendar value, TemporalType temporalType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(int position, Object value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(int position, Date value, TemporalType temporalType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setParameter(int position, Calendar value, TemporalType temporalType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Query setFlushMode(FlushModeType flushMode)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getQuerySource()
    {
        return querySource;
    }

    public void setQuerySource(String querySource)
    {
        this.querySource = querySource;
    }

    public boolean isSelectCount()
    {
        return selectCount;
    }

    public void setSelectCount(boolean selectCount)
    {
        this.selectCount = selectCount;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public List<QueryCondition> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<QueryCondition> conditions)
    {
        this.conditions = conditions;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public OrderByType getOrderByType()
    {
        return orderByType;
    }

    public void setOrderByType(OrderByType orderByType)
    {
        this.orderByType = orderByType;
    }
    

    public DBQuery clone(Kernel kernel)
    {
        DBQuery res = null;
        try
        {
            res = (DBQuery) this.clone();
            res.kernel = kernel;
        }
        catch (CloneNotSupportedException e)
        {
            throw new KernelException(e);
        }
        
        return res;
    }

}
