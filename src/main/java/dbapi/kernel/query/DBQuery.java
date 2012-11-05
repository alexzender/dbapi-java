package dbapi.kernel.query;

import java.util.List;

import dbapi.api.KernelException;
import dbapi.kernel.Kernel;

/**
 * 
 * @author alex
 *
 */
public class DBQuery
implements Cloneable
{

    private String alias;
    private String entity;
    private boolean selectCount;

    private List<QueryCondition> conditions;

    private String orderByColumn;
    private OrderByType orderByType;

    public DBQuery(final Kernel kernel)
    {
    }


    public enum OrderByType
    {
        ASC,
        DESC;
    }


    public boolean isSelectCount()
    {
        return selectCount;
    }

    public void setSelectCount(final boolean selectCount)
    {
        this.selectCount = selectCount;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(final String alias)
    {
        this.alias = alias;
    }

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(final String entity)
    {
        this.entity = entity;
    }

    public List<QueryCondition> getConditions()
    {
        return conditions;
    }

    public void setConditions(final List<QueryCondition> conditions)
    {
        this.conditions = conditions;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(final String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public OrderByType getOrderByType()
    {
        return orderByType;
    }

    public void setOrderByType(final OrderByType orderByType)
    {
        this.orderByType = orderByType;
    }


    public DBQuery clone(final Kernel kernel)
    {
        DBQuery res = null;
        try
        {
            res = (DBQuery) this.clone();
        }
        catch (final CloneNotSupportedException e)
        {
            throw new KernelException(e);
        }

        return res;
    }

}
