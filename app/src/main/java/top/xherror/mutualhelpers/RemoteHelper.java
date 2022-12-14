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
    public void addImage(File file){
        //pass it like this
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
}
