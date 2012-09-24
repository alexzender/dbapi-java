package dbapi.api;
/**
 * 
 * @author alex
 *
 */
@SuppressWarnings("serial")
public class UnsupportedFeatureException
    extends RuntimeException
{

    public UnsupportedFeatureException()
    {
        super();
    }

    public UnsupportedFeatureException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnsupportedFeatureException(String message)
    {
        super(message);
    }

    public UnsupportedFeatureException(Throwable cause)
    {
        super(cause);
    }
   

}
