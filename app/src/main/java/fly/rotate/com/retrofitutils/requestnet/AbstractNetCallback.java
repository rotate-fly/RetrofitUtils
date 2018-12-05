package fly.rotate.com.retrofitutils.requestnet;

/**
 *
 * @author hzs
 * @date 16/7/4
 *
 */

public abstract class AbstractNetCallback<T> {
    public void onStart() {
    }



    public void onComplete() {
    }



    public void onError() {
    }



    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    public abstract void onSuccees(T t);

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    protected abstract void onFailure(Throwable e, boolean isNetWorkError) throws Exception;

    /**
     * 返回成功了,但是code错误
     *
     * @param t
     * @throws Exception
     */
    protected void onCodeError(T t) throws Exception {
    }

    protected void onRequestStart() {
        showProgressDialog();
    }

    public void showProgressDialog() {

    }

    public void closeProgressDialog() {
    }

    protected void onRequestEnd() {
    }

}
