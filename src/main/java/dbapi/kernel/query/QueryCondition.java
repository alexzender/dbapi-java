package dbapi.kernel.query;
/**
 * 
 * @author alex
 *
 */
public class QueryCondition
{
    private QueryConditionType type;
    private String column;
    private String alias;
    private String value;
    private String betweenFrom;
    private String betweenTo;
   
    
    public QueryCondition(String alias, String column, QueryConditionType type, String value)
    {
        this.type = type;
        this.column = column;
        this.alias = alias;
        this.value = value;
    }

    public QueryConditionType getType()
    {
        return type;
    }

    public void setType(QueryConditionType type)
    {
        this.type = type;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    
    
    
    public String getBetweenFrom()
    {
        return betweenFrom;
    }

    public void setBetweenFrom(String betweenFrom)
    {
        this.betweenFrom = betweenFrom;
    }

    public String getBetweenTo()
    {
        return betweenTo;
    }

    public void setBetweenTo(String betweenTo)
    {
        this.betweenTo = betweenTo;
    }

    @Override
    public String toString()
    {
        return alias + "." + column + ((type == QueryConditionType.EQUALS) ? '=' : " LIKE ") + value;
    }    
}
