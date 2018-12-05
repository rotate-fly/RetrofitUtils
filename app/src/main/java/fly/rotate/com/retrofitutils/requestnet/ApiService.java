package fly.rotate.com.retrofitutils.requestnet;


import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author hzs
 * @date 2018/11/1
 * @description API接口!
 */

public interface ApiService {

    /**
     * 上传设备信息
     */
    @POST
    @FormUrlEncoded
    Observable<String> postAndroidDeviceMsg(@Url() String url, @FieldMap Map<String, Object> params);


}
