package dbapi.plugins;
/**
 * 
 * @author alex
 *
 */
public class DBPluginException
    extends RuntimeException
{

    public DBPluginException()
    {        
    }

    public DBPluginException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);        
    }

    public DBPluginException(String arg0)
    {
        super(arg0);
    }

    public DBPluginException(Throwable arg0)
    {
        super(arg0);
    }
    
}
