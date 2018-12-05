package fly.rotate.com.retrofitutils.requestnet.download;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private static volatile RxBus mInstance;

    private final Subject<Object> mBus;

    private RxBus() {
        this.mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = Holder.BUS;
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送一个事件
     *
     * @param obj
     */
    public void post(Object obj) {
        mBus.onNext(obj);
    }

    /**
     * 暴露出RxBus的Observable供我们订阅事件
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }
}
