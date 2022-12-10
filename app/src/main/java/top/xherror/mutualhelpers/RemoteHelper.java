package top.xherror.mutualhelpers;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface DatabaseService{
    @GET("items")
    Call<List<EntityItem>> getItems();

    @FormUrlEncoded
    @POST("user/edit")
    Call<EntityItem> updateUser(@Field("first_name") String first, @Field("last_name") String last);
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
                Log.i("MainActivity","请求成功");
            }

            @Override
            public void onFailure(Call<List<EntityItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
