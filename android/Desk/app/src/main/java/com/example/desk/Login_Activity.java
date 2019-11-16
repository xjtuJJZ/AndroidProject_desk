package com.example.desk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity {

    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_FAILED = 1;
    private Button mBtn_Login;
    private Button mBtn_enroll;
    TextView name;
    TextView pwd;

    String loginurl = "http://114.55.66.253:3000/stu/getdata";

    public static String stu_name;
    public static String stu_pwd;
    public static String stu_ava;
    public static String comname;
    String localname;
    String localpwd;

    //boolean LOGIN_SUCCESS;

    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOGIN_SUCCESS) {
                Log.i("aaa", "handleMessage: get");
                Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_Activity.this, DeskActivity.class);
                    startActivity(intent);
            }
            if (msg.what == LOGIN_FAILED) {
                Log.i("aaa", "handleMessage: not get");
                Toast.makeText(getApplicationContext(), "登录失败，账号密码写对了嘛？", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        mBtn_Login=findViewById(R.id.btn_login);
        mBtn_enroll=findViewById(R.id.btn_enroll);
        name = findViewById(R.id.Et1);
        pwd = findViewById(R.id.Et2);
        //点击登录
        mBtn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sign_up();  //从服务器数据库获取用户名和密码 与用户输入用户名密码对比 确认能否登陆
                            //如果可以登录 页面跳转至DeskActivity
//                if (LOGIN_SUCCESS) {
//                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Login_Activity.this, DeskActivity.class);
//                    startActivity(intent);
//                }
//                else Toast.makeText(getApplicationContext(), "登录失败，账号密码写对了嘛？", Toast.LENGTH_SHORT).show();
            }
        });

        //点击注册，跳转至注册页面
        mBtn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this,EnrollActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sign_up() {
         localname = name.getText().toString();
         localpwd = pwd.getText().toString();
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("stu_name",localname );
                    jsonParam.put("stu_pwd",localpwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonParam.toString();
                Log.i("aaa", json);
                RequestBody requestBody = RequestBody.create(json,JSON);
                Request request = new Request.Builder().url(loginurl).post(requestBody).build();
                try {
                    Response response = client.newCall(request).execute();
                    String responsedata = response.body().string();


                    if (responsedata.equals("null")) {
                        Message msg = new Message();
                        msg.what = LOGIN_FAILED;
                        handler.sendMessage(msg);

                    }else {
                        JSONObject getdata = new JSONObject(responsedata);

                        stu_name = getdata.getString("stu_name");
                        stu_pwd = getdata.getString("stu_pwd");
                        stu_ava = getdata.getString("stu_ava");

                        comname = stu_name;

                        stu_name = new String(stu_name.getBytes("ISO-8859-1"), "UTF-8");
                        stu_pwd = new String(stu_pwd.getBytes("ISO-8859-1"), "UTF-8");
                        localname = new String(localname.getBytes("ISO-8859-1"), "UTF-8");
                        localpwd = new String(localpwd.getBytes("ISO-8859-1"), "UTF-8");

                        Log.i("aaa", (Arrays.toString(stu_name.getBytes())));
                        Log.i("aaa", (Arrays.toString(localname.getBytes())));
                        Log.i("aaa", (Arrays.toString(stu_pwd.getBytes())));
                        Log.i("aaa", (Arrays.toString(localpwd.getBytes())));

                        if (stu_name.equals(localname) && stu_pwd.equals(localpwd)) {
                            Message msg = new Message();
                            msg.what = LOGIN_SUCCESS;
                            handler.sendMessage(msg);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
