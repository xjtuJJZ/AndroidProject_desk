package com.example.desk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MylistAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    int count;
    JSONArray jsonresult;

    String getcounturl = "http://114.55.66.253:3000/signinfo/count";
    String getsigndataturl = "http://114.55.66.253:3000/signinfo/getdata";

    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");


    public MylistAdapter(Context context){
        this.mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
        getnum.start();
        getsigndata.start();
    }
    @Override
    public int getCount() {
        try  {
            getnum.join();
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        public TextView tv1,tv2,tv3,tv4;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.layout_list_item,null);
            holder= new ViewHolder();
            holder.tv1=convertView.findViewById(R.id.list_tv1);
            holder.tv2=convertView.findViewById(R.id.list_tv2);
            holder.tv3=convertView.findViewById(R.id.list_tv3);
            holder.tv4=convertView.findViewById(R.id.list_tv4);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        try  {
            getsigndata.join() ;
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //给控件赋值
            JSONObject eachinfo = jsonresult.getJSONObject(position);
            holder.tv1.setText("学生姓名： "+eachinfo.getString("stu_name"));
            holder.tv2.setText("签到情况： "+eachinfo.getString("is_sign"));
            holder.tv3.setText("人脸相似度： "+eachinfo.getDouble("score"));
            holder.tv4.setText("日期： "+eachinfo.getString("sign_date"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    Thread getnum = new Thread() {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("stu_name",Login_Activity.comname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonParam.toString();
                Log.i("aaa", json);
                RequestBody requestBody = RequestBody.create(json,JSON);
                Request request = new Request.Builder().url(getcounturl).post(requestBody).build();
                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.i("aaa", result);
                    JSONObject jsonresult = new JSONObject(result);
                    count = jsonresult.getInt("num");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


    Thread getsigndata = new Thread() {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("stu_name",Login_Activity.comname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = jsonParam.toString();
            Log.i("aaa", json);
            RequestBody requestBody = RequestBody.create(json,JSON);
            Request request = new Request.Builder().url(getsigndataturl).post(requestBody).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.i("aaa", result);
                jsonresult = new JSONArray(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };




}
