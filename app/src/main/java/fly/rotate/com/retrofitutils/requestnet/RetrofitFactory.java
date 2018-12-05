package fly.rotate.com.retrofitutils.requestnet;


import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static RetrofitFactory mRetrofitFactory;
    private static ApiService sApiService;

    public static int HTTP_TIME = 8;

    private RetrofitFactory(Context context) {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(HTTP_TIME, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(InterceptorUtil.tokenInterceptor())
                //添加日志拦截器
                .addInterceptor(InterceptorUtil.LogInterceptor())
                .addNetworkInterceptor(InterceptorUtil.paramsInterceptor(context))
                .build();
        //baseUrl不能为空
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http:")
                //添加gson转换器
                .addConverterFactory(GsonConverterFactory.create())
                //添加rxjava转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
        sApiService = mRetrofit.create(ApiService.class);

    }

    public static RetrofitFactory getInstence(Context context) {
        if (mRetrofitFactory == null) {
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null) {
                    mRetrofitFactory = new RetrofitFactory(context);
                }
            }

        }
        return mRetrofitFactory;
    }

    public ApiService API() {
        return sApiService;
    }
}
