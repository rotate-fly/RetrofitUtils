package fly.rotate.com.retrofitutils.requestnet.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zs on 2016/7/7.
 * @author hzs
 */
public abstract class FileCallback implements Callback<ResponseBody> {
    /**
     * 订阅下载进度
     */
    private CompositeDisposable rxSubscriptions = new CompositeDisposable();
//    CompositeDisposable composite = new CompositeDisposable();

//composite.add(Flowable.range(1, 8).subscribeWith(subscriber));
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public FileCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        subscribeLoadProgress();// 订阅下载进度
    }

    /**
     * 成功后回调
     */
    public abstract void onSuccess(File file);

    /**
     * 下载过程回调
     */
    public abstract void onLoading(long progress, long total);

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            saveFile(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File saveFile(Response<ResponseBody> response) throws Exception {
        InputStream in = null;
        FileOutputStream out = null;
        byte[] buf = new byte[2048 * 10];
        int len;
        try {
            File dir = new File(destFileDir);
            if (!dir.exists()) {// 如果文件不存在新建一个
                dir.mkdirs();
            }
            in = response.body().byteStream();
            File file = new File(dir, destFileName);
            out = new FileOutputStream(file);
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            // 回调成功的接口
            onSuccess(file);
            unSubscribe();// 取消订阅
            return file;
        } finally {
            in.close();
            out.close();
        }
    }

    /**
     * 订阅文件下载进度
     */
    private void subscribeLoadProgress() {
        RxBus.getDefault().toObservable(FileLoadingBean.class).subscribe(new Observer<FileLoadingBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                rxSubscriptions.add(d);
            }

            @Override
            public void onNext(FileLoadingBean fileLoadingBean) {
                onLoading(fileLoadingBean.getProgress(), fileLoadingBean.getTotal());

            }

            @Override
            public void onError(Throwable e) {
                unSubscribe();
            }

            @Override
            public void onComplete() {
                unSubscribe();

            }
        });
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    private void unSubscribe() {
        if (!rxSubscriptions.isDisposed()) {
            rxSubscriptions.clear();
        }
    }
}
