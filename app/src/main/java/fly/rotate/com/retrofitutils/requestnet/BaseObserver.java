package fly.rotate.com.retrofitutils.requestnet;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;


import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BaseObserver<T> implements Observer<T> {
    protected Context mContext;
    //    private final int REQUESTCODE = 1234;
//    private Boolean isShowLoading = true;
    private AbstractNetCallback<T> mAbstractNetCallback;

    public BaseObserver(Context cxt, AbstractNetCallback<T> abstractNetCallback) {
        this.mContext = cxt;
        this.mAbstractNetCallback = abstractNetCallback;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mAbstractNetCallback.onRequestStart();
    }

    @Override
    public void onNext(T tBaseEntity) {
        try {
            mAbstractNetCallback.onSuccees(tBaseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAbstractNetCallback.onRequestEnd();
    }

    /**
     * 按照status去跳转
     * 2 跳转登录
     * 3支付密码
     * 4手机号
     * 5邮箱
     * 6身份认证
     * 7余额不足，去充值；
     * 8店铺名设置。
     * 9设置银行卡信息
     * 10新设备登录 跳转到 安全验证,不需要再次点发送验证码,后台已发送.
     */
    private void otherStatusCode(String status) {
        switch (status) {
            case "5":
                break;
            case "6":
                break;
            case "7":
                break;
            case "8":
                break;
            case "9":
                break;
            case "10":
                break;
            default:
                break;
        }
    }


    //    java.net.SocketTimeoutException: failed to connect to /192.168.3.31 (port 80) after 30000000ms: isConnected failed: ETIMEDOUT (Connection timed out)
    //retrofit2.adapter.rxjava2.HttpException: HTTP 404 Not Found
    @Override
    public void onError(Throwable e) {
        Log.e("retrofit", "onError: " + e.getMessage());
        mAbstractNetCallback.onRequestEnd();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                mAbstractNetCallback.onFailure(e, true);
            } else {
                mAbstractNetCallback.onFailure(e, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        mAbstractNetCallback.onComplete();
    }
}
