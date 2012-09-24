package dbapi.kernel.query;

import java.util.List;

import dbapi.api.UnsupportedFeatureException;
import dbapi.kernel.query.jpql.JPQLTreeConstants;
import dbapi.kernel.query.jpql.SimpleNode;

/**
 * 
 * @author alex
 *
 */
public class QueryConditionFinder
{
    /**
     * Recursively walks through a parsed query tree and discovers filter criterias based on the list of supported operators.
     * 
     *  
     * 
     * @param node
     * @param conditions
     */
    public void walk(SimpleNode node,List<QueryCondition> conditions)
    {
        if(node.getId() == JPQLTreeConstants.JJTWHERE)
        {
            walk(node.getChild(0),conditions);
            return;
        }
       
        //FIXME: This big if-else branch tree to be re-factored and replaced by a map with IDs of nodes mapping to node parsers
        
        // 1) parse atomic nodes
        if(node.isEQUALS())
        {            
            SimpleNode valueNode = node.getChild(1);
            String value = valueNode.getText(); // reply
            
            if (value.indexOf("'") == 0 && value.lastIndexOf("'") == (value.length() - 1))
            {
                value = value.substring(1, value.length() - 1);
            }
            else if (valueNode.getChildByID(JPQLTreeConstants.JJTNEGATIVE, false) != null)
            {
                value = '-' + value;
            }
            
            QueryCondition cond = new QueryCondition(node.getAlias(), node.getColumn(), QueryConditionType.EQUALS, value);
            conditions.add(cond);
            
            
        }
        else if(node.isLIKE())
        {
            String value = node.getChild(1).getChild(0).getText(); 
            
            if(value.indexOf("'")==0 && value.lastIndexOf("'")==(value.length()-1) )
            {
                value=value.substring(1, value.length()-1);
            }
            
            QueryCondition like=new QueryCondition(node.getAlias(), node.getColumn(), QueryConditionType.LIKE, value);
            conditions.add(like);            
            
        }
        else if(node.isIN())
        {
            // process in
            
            for(int i = 1; i < node.getChildCount(); i++)
            {
                String value = node.getChild(i).getText();
            
                if(value.indexOf("'")==0 && value.lastIndexOf("'")==(value.length()-1) )
                {
                    value=value.substring(1, value.length()-1);
                }
                
                QueryCondition in = new QueryCondition(node.getAlias(), node.getColumn(), QueryConditionType.EQUALS, value);
                
                conditions.add(in);
            }
            
        }        
        else if(node.isBETWEEN())
        {
            String from = node.getChild(1).getText();
            String to = node.getChild(2).getText();
            
            //FIXME: no character value processig for now            
            QueryCondition range = new QueryCondition(node.getAlias(), node.getColumn(), QueryConditionType.BETWEEN, from + ":" + to);
            range.setBetweenFrom(from);
            range.setBetweenTo(to);
            conditions.add(range);
            
            return;
        }        
        // 2) parse AND & OR (recursively go through the hierarchy)
        // AND & OR translated to the same statements in resulting query, but we should throw exceptions in different cases
        else if(node.getId()==JPQLTreeConstants.JJTOR)
        {
            walk(node.getChild(0), conditions);
            walk(node.getChild(1), conditions);
            return;
        }
        if(node.getId()==JPQLTreeConstants.JJTAND)
        {
            walk(node.getChild(0), conditions);
            walk(node.getChild(1), conditions);
            return;
        }
        
        // 3) throw exception if construction is not supported
        throw new UnsupportedFeatureException("Unsupported operator found in WHERE clause. Node ID: "+node.getId()+" (from JPQLTreeConstants class)"); 
    }
    
}
