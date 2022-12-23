package top.xherror.mutualhelpers;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
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

    @Multipart
    @POST("avatars")
    Call<ResponseBody> addAvatar(@Part MultipartBody.Part image);

    @GET("update")
    Call<ResponseBody> updateCheck(@Query("timeStamp") Long timeStamp);

    @DELETE("delete")
    Call<ResponseBody> deleteItem(@Query("id") Integer id);

}

public class RemoteHelper {
    private static final String  tag= "RemoteHelper";
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
                Log.i(tag,"GET请求成功");
            }

            @Override
            public void onFailure(Call<List<EntityItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteItem(EntityItem entityitem){
        Call<ResponseBody> item= service.deleteItem(entityitem.getId());
        item.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(tag,"success:delete item id="+String.valueOf(entityitem.getId()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addItem(EntityItem entityitem){
        Call<EntityItem> item= service.addItem(entityitem);
        item.enqueue(new Callback<EntityItem>() {
            @Override
            public void onResponse(Call<EntityItem> call, Response<EntityItem> response) {
                EntityItem callbackEntityItem=response.body();
                Log.d(tag,"success:add item id="+String.valueOf(callbackEntityItem.getId()));
                DateBase.itemDao.insertItems(callbackEntityItem);
            }

            @Override
            public void onFailure(Call<EntityItem> call, Throwable t) {
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

    public void addAvatar(File file){
        //pass it like this
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        Call<ResponseBody> item=  service.addAvatar(body);
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

    public void updateCheck(Long timeStamp){
        Gson gson= new Gson();
        Call<ResponseBody> item= service.updateCheck(timeStamp);
        item.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Log.i("MainActivity","POST请求成功");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String update=jsonObject.getString("update");
                    Log.d(tag,update);
                    if (update.equals("yes")){
                        //TODO:
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
