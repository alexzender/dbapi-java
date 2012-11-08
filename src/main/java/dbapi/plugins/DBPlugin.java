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
    
    GetCommand getFindCommand();
    SaveCommand getPersistCommand();
    DeleteCommand getRemoveCommand();
    QueryCommand getQueryCommand();
    
    SchemaCreateCommand getSchemaCreateCommand();
    SchemaDeleteCommand getSchemaDeleteCommand();
}
