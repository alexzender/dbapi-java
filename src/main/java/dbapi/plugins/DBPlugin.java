package dbapi.plugins;
/**
 * 
 * @author alex
 *
 */
public interface DBPlugin
{
    void init(DBPluginContext ctx);
    
    void flush();
    void close();
    
    String getId();
    
    FindCommand getFindCommand();
    PersistCommand getPersistCommand();
    RemoveCommand getRemoveCommand();
    QueryCommand getQueryCommand();
    
    SchemaCreateCommand getSchemaCreateCommand();
    SchemaDeleteCommand getSchemaDeleteCommand();
}
