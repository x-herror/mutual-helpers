package top.xherror.mutualhelpers;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

interface DatabaseService{
    @GET("items")
    Call<List<EntityItem>> getItems();

    //https://blog.csdn.net/qq_36342492/article/details/90691559

    @Headers("Content-Type: application/json")
    @POST("items")
    Call<EntityItem> addItem(@Body EntityItem entityItem);

    @Headers("Content-Type: application/json")
    @POST("items")
    Call<TestItem> addTestItem(@Body TestItem testItem);

    @Multipart
    @POST("images")
    Call<ResponseBody> updateProfile(@Part("full_name") RequestBody fullName, @Part MultipartBody.Part image);

    @Streaming
    @GET("images/{imageName}")
    Call<ResponseBody>  download(@Path("imageName") String imageName);
}

public class RemoteHelper {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.184:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DatabaseService service = retrofit.create(DatabaseService.class);

    public void getItems() {
        Call<List<EntityItem>> items = service.getItems();
        items.enqueue(new Callback<List<EntityItem>>() {
            @Override
            public void onResponse(Call<List<EntityItem>> call, Response<List<EntityItem>> response) {
                Log.i("MainActivity","GET请求成功");
            }

            @Override
            public void onFailure(Call<List<EntityItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addItem(EntityItem entityitem){
        Call<EntityItem> item= service.addItem(entityitem);
        item.enqueue(new Callback<EntityItem>() {
            @Override
            public void onResponse(Call<EntityItem> call, Response<EntityItem> response) {
                Log.i("MainActivity","POST请求成功");
            }

            @Override
            public void onFailure(Call<EntityItem> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addTestItem(TestItem testItem){
        Call<TestItem> item= service.addTestItem(testItem);
        item.enqueue(new Callback<TestItem>() {
            @Override
            public void onResponse(Call<TestItem> call, Response<TestItem> response) {
                Log.i("MainActivity","POST请求成功");
            }

            @Override
            public void onFailure(Call<TestItem> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //https://stackoverflow.com/questions/39953457/how-to-upload-an-image-file-in-retrofit-2
    public void addImage(String pathName){
        //pass it like this
        File file = new File(pathName);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // add another part within the multipart request
        RequestBody fullName =
                RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name");

        Call<ResponseBody> item=  service.updateProfile(fullName, body);
        item.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("MainActivity","POST请求成功");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    //https://cloud.tencent.com/developer/article/1742281
    public  void download(String url, final String path, final DownloadListener downloadListener) {

        Call<ResponseBody>  call = service.download(url);
        call.enqueue(new Callback<ResponseBody> () {
            @Override
            public void onResponse(Call<ResponseBody>  call,final Response<ResponseBody>  response) {
                //将Response写入到从磁盘中，详见下面分析
                //注意，这个方法是运行在子线程中的
                writeResponseToDisk(path, response, downloadListener);
            }

            @Override
            public void onFailure( Call<ResponseBody>  call, Throwable throwable) {
                downloadListener.onFail("网络错误～");
            }
        });
    }

    private static void writeResponseToDisk(String path, Response<ResponseBody  response, DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
    }

    private static int sBufferSize = 8192;

    //将输入流写入文件
    private static void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        //开始下载
        downloadListener.onStart();

        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
