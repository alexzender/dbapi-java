package dbapi.api;
/**
 * 
 * @author alex
 *
 */
public class KernelException
    extends RuntimeException
{

    public KernelException()
    {
        super();
    }

    public KernelException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public KernelException(String message)
    {
        super(message);
    }

    public KernelException(Throwable cause)
    {
        super(cause);
    }

}
