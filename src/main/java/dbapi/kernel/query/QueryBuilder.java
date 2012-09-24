package dbapi.kernel.query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

import dbapi.api.KernelException;
import dbapi.api.UnsupportedFeatureException;
import dbapi.kernel.annotation.AnnotatedEntity;
import dbapi.kernel.annotation.AnnotationService;
import dbapi.kernel.query.jpql.JPQL;
import dbapi.kernel.query.jpql.JPQLTreeConstants;
import dbapi.kernel.query.jpql.ParseException;
import dbapi.kernel.query.jpql.SimpleNode;
import dbapi.kernel.query.jpql.TokenMgrError;

/**
 * 
 * @author alex
 *
 */
public class QueryBuilder
{
    private static final Logger log = Logger.getLogger(QueryBuilder.class);
    
    @Inject
    private Injector guice;
    
    @Inject
    private QueryConditionFinder conditionWalker;
    
    public DBQuery build(String query, AnnotationService annotations)
    {
        DBQuery res = guice.getInstance(DBQuery.class);
        
        SimpleNode root = buildGrammarTree(query);

        res.setQuerySource(query);
        
        processSelectClause(res, root);
        
        
        // from clause contains 'FROM item ' and optionally list of joins
        SimpleNode fromClause = root.getChildByID(JPQLTreeConstants.JJTFROM, false);
        if(null == fromClause)
        {
            throw new IllegalArgumentException("Invalid query: FROM expression in query is missing");
        }
        
        SimpleNode fromItem = fromClause.getChildByID(JPQLTreeConstants.JJTFROMITEM, false);
        if(null == fromItem)
        {
            throw new IllegalArgumentException("Invalid query: FROM  clause is missing");
        }
        
        SimpleNode schemaNode = fromItem.getChildByID(JPQLTreeConstants.JJTABSTRACTSCHEMANAME, false);
        if(null == schemaNode)
        {
            throw new IllegalArgumentException("Invalid query: entity name in FROM clause is missing");
        }
        
        SimpleNode entityNode = schemaNode.getChildByID(JPQLTreeConstants.JJTIDENTIFICATIONVARIABLE, false);
        String entityName = entityNode.getText();
        res.setEntity(entityName);
        
        validateAliasNode(res.getAlias(), fromItem, entityName);
        
        AnnotatedEntity mainEntityAnn = annotations.lookup(entityName);
        if(null == mainEntityAnn)
        {
            throw new IllegalArgumentException("Unknown " + entityName + " entity. It hasn't been registered in this runtime context yet.");
        }
        
        
        // 3rd step. PROCESSING WHERE node        
        SimpleNode whereClause = root.getChildByID(JPQLTreeConstants.JJTWHERE, false);

        
        if(null != whereClause)
        {
            List<QueryCondition> conditions = new ArrayList<QueryCondition>();
            
            conditionWalker.walk(whereClause,conditions);
            
            res.setConditions(conditions);
        }

        //4th step. ORDER BY processing
        processOrderBy(res, root);
        
        return res;
    }

    private void validateAliasNode(String aliasInSelect, SimpleNode fromItem, String entityName)
    {
        SimpleNode entityAliasNode = fromItem.getChildByID(JPQLTreeConstants.JJTIDENTIFIER, false);
        
        if(entityAliasNode == null)
        {
            // ensure that query in the form of 'SELECT Entity FROM Entity ...'
            if(!aliasInSelect.equals(entityName))
            {
                throw new IllegalArgumentException("Invalid query: Either alias is missing or " 
                        + aliasInSelect + " must equal to " + entityName);
            }
        }
        else
        {
            // ensure that it's in form of 'SELECT alias FROM Entity alias...'
            if(!aliasInSelect.equals(entityAliasNode.getText()))
            {
                throw new IllegalArgumentException("Invalid query: aliases in the query don't match:  '" 
                        + aliasInSelect + "' and '" + entityAliasNode.getText() + "'");
            }
        }
    }

    private SimpleNode processSelectClause(DBQuery res, SimpleNode root)
    {
        SimpleNode selectClause = root.getChildByID(JPQLTreeConstants.JJTSELECTCLAUSE, false);
        if(null == selectClause)
        {
            throw new IllegalArgumentException("Invalid query: SELECT clause is missing");
        }
        
        SimpleNode selectCount = selectClause.getChildByID(JPQLTreeConstants.JJTCOUNT, true);
        if(null != selectCount)
        {
            res.setSelectCount(true);
        }
        
        //TODO: add multiple entities selection support
        SimpleNode aliasNode= selectClause.getChildByID(JPQLTreeConstants.JJTIDENTIFIER, true);
        if(null == aliasNode)
        {
            throw new IllegalArgumentException("Invalid query: entity name/alias is missing in the SELECT expression");
        }
        String aliasInSelect = aliasNode.getText();
        res.setAlias(aliasInSelect);
        
        return selectClause;
    }

    private SimpleNode buildGrammarTree(String query)
    {
        SimpleNode root = null;
        
        try
        {
            root = (SimpleNode) new JPQL(query).parseQuery();
        }
        catch(ParseException ex)
        {
            throw new KernelException(ex);
        }
        catch(TokenMgrError err)
        {
            throw new KernelException(err);
        }
       
        if(root.getId() != JPQLTreeConstants.JJTSELECT)
        {
            throw new UnsupportedFeatureException("Only SELECT query is supported at the moment");
        }
        
        return root;
    }
    
    private void processOrderBy(DBQuery res, SimpleNode root)
    {
        if (!res.isSelectCount())
        {
            SimpleNode orderByNode = root.getChildByID(JPQLTreeConstants.JJTORDERBY, true);
            if(null != orderByNode)
            {
                SimpleNode pathNode = orderByNode.getChildByID(JPQLTreeConstants.JJTPATH, true);
                if(null != pathNode)
                {
                    SimpleNode idNode = pathNode.getChildByID(JPQLTreeConstants.JJTIDENTIFICATIONVARIABLE, false);
                    if(null != idNode)
                    {
                        res.setOrderByColumn(idNode.getText());
                    }
                    else
                    {
                        log.trace("No ORDER BY identification node detected");
                    }
                }
                else
                {
                    log.trace("No ORDER BY path node detected");
                }
                
                SimpleNode desc = orderByNode.getChildByID(JPQLTreeConstants.JJTDESCENDING, true);
                if(null != desc)
                {
                    res.setOrderByType(DBQuery.OrderByType.DESC);            
                }
                else
                {
                    SimpleNode asc = orderByNode.getChildByID(JPQLTreeConstants.JJTASCENDING, true);
                    if(null != asc)
                    {
                        res.setOrderByType(DBQuery.OrderByType.ASC);            
                    }
                }
            }
            else 
            {
                log.trace("no ORDER BY clause detected");
            }
        }
    }
}
