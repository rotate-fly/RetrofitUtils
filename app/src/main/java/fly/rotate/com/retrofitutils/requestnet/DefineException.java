package fly.rotate.com.retrofitutils.requestnet;

/**
 * Created by Administrator on 2017/5/27.
 * 自定义异常
 */
public class DefineException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DefineException() {
        super();
    }

    public DefineException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefineException(String message) {
        super(message);
    }

    public DefineException(Throwable cause) {
        super(cause);
    }

}
