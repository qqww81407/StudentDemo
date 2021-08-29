package tw.com.studentdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String url = "http://tbike-data.tainan.gov.tw/Service/StationStatus/Json";

    private ListView listview;

    private OkHttpClient client;

    private ArrayList<BikeItem> allData = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.bikelist);
        client = new OkHttpClient();
        getJson();
    }

    private void getJson(){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //連線失敗

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //連線成功
                final String res = response.body().string();
                //傳入成功後 解析json
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray array = new JSONArray(res);
                            for(int i = 0 ; i < array.length() ; i++){
                                JSONObject data = array.getJSONObject(i);
                                BikeItem item = new BikeItem();
                                item.setStation(data.getString("StationName"));
                                item.setRent(""+data.getInt("AvaliableBikeCount"));
                                item.setSpace(""+data.getInt("AvaliableSpaceCount"));
                                item.setLat(data.getDouble("Latitude"));
                                item.setLng(data.getDouble("Longitude"));
                                allData.add(item);
                            }

                            listview.setAdapter(new CustomAdapter(getApplicationContext(),allData));
                            bikelistener();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });


    }

    private void bikelistener(){
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                BikeItem item = allData.get(position);
                bundle.putString("station",item.getStation());
                bundle.putDouble("Lat",item.getLat());
                bundle.putDouble("Lng",item.getLng());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }

}