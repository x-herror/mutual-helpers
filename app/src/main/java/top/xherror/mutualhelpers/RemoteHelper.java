package top.xherror.mutualhelpers;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import retrofit2.http.POST;
import retrofit2.http.Path;

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

}
