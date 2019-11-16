package com.example.desk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListViewActivity extends AppCompatActivity {

    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    String signdataurl = "http://114.55.66.253:3000/signinfo/getdata";

    int page = 0;
    int num = 1;
    private ListView mRvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mRvMain=findViewById(R.id.rv_main);
        mRvMain.setAdapter(new MylistAdapter(ListViewActivity.this));
    }

}
