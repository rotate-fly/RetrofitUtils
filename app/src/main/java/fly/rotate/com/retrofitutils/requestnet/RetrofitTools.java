package fly.rotate.com.retrofitutils.requestnet;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * company: qianmi
 * Created by guanyue
 * date:   2018/11/5.
 * desc:
 * @author hzl
 */
public class RetrofitTools<T> {
    /**
     * 结果在主线程中
     *
     * @param context
     * @param observable 请求接口观察者实例
     */
    public void responseUI(Context context, Observable<T> observable, AbstractNetCallback<T> abstractNetCallback) {
        observable
                .subscribeOn(Schedulers.io())
                //在主线程显示数据
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<T>(context, abstractNetCallback));
    }

    /**
     * 结果在子线程中
     *
     * @param context
     * @param observable
     */
    public void responseIO(Context context, Observable<T> observable, AbstractNetCallback<T> abstractNetCallback) {
        observable.subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver<T>(context, abstractNetCallback));
    }
}
