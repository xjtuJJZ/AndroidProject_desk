package com.example.desk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnrollActivity extends AppCompatActivity {
    String enroll_user_name;
    String enroll_pwd1;
    String enroll_pwd2;
    String enroll_userid;

    Button selectButton;
    Button enroll_ensure_Button;
    ImageView image;
    EditText name;
    EditText pwd1;
    EditText pwd2;
    EditText id;
    //存储相关信息
    String uploadFileName;
    byte[] fileBuf;
    public String base64buf;
    boolean ACCOUNT_EXIST ;


    String baseuploadurl ="http://114.55.66.253:3000/stu/upload";
    String ifexisturl = "http://114.55.66.253:3000/stu/ifexist";
    String baiduuploadurl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.3fbfc40ea8318451427a4e4d70333cfe.2592000.1576311254.282335-17718723";


    String accesstoken;
    String apikey = "HeF1uyYudHVreTNTvSE9Y1Xk";
    String secretkey = "BsiRUOyzeFDmZHr1w1YYCibmQkXp9WsV";

    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");


    //String imagepath;
    //Handler handler; //https://baike.baidu.com/item/handler/10404534?fr=aladdin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        name = findViewById(R.id.Enroll_et);
        pwd1 = findViewById(R.id.Enroll_et2);
        pwd2 = findViewById(R.id.Enroll_et3);
        id =findViewById(R.id.enroll_id);
        selectButton = findViewById(R.id.Enroll_btn_select);
        enroll_ensure_Button = findViewById(R.id.Enroll_btn_ensure);
        image = findViewById(R.id.Enroll_iv1);
        //###########################
        //与选择注册图片相关内容
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
                Log.i("aaa", "可以选择图片");

            }
        });
        //与注册相关内容
        //1.注册信息符合要求 2.信息上传服务器
        enroll_ensure_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("aaa", "可以点击确认注册");
                enroll_user_name = name.getText().toString();
                enroll_pwd1 = pwd1.getText().toString();
                enroll_pwd2 = pwd2.getText().toString();
                if ((enroll_user_name.length() != 0) && (enroll_pwd1.length() != 0) && (enroll_pwd1.equals(enroll_pwd2))) {
                    //输入的用户名 enroll_user_name 与服务器数据库已存在用户名对比 确认是否可以注册
                    try {
                        ifexist();
                        if (ACCOUNT_EXIST) {
                            Toast.makeText(getApplicationContext(), "该姓名已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            if (fileBuf == null){
                                Toast.makeText(getApplicationContext(), "未选择图片", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                base64buf = Base64(fileBuf);
                                uploadmongo();
                                uploadbaidu();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(EnrollActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EnrollActivity.this, "用户名或密码不符合要求", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void uploadbaidu() {
        new Thread() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("image_type", "BASE64");
                    jsonParam.put("group_id", "myface");
                    jsonParam.put("user_id",id.getText() );
                    jsonParam.put("user_info", "");
                    jsonParam.put("liveness_control", "LOW");
                    jsonParam.put("quality_control", "LOW");
                    jsonParam.put("image", base64buf);
                    String json = jsonParam.toString();
                    Log.i("aaa", json);
                    RequestBody requestBody = RequestBody.create(json, JSON);
                    Request request = new Request.Builder().url(baiduuploadurl).post(requestBody).build();
                    try {
                        Response response = client.newCall(request).execute();
                        Log.i("aaa", response.body().string());
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "人脸库上传成功！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }.start();
        }

    private void uploadmongo() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("stu_name",enroll_user_name );
                    jsonParam.put("stu_pwd",enroll_pwd1 );
                    jsonParam.put("stu_ava",base64buf);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonParam.toString();
                Log.i("aaa", json);
                RequestBody requestBody = RequestBody.create(json,JSON);
                Request request = new Request.Builder().url(baseuploadurl).post(requestBody).build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.i("aaa", response.body().string());
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void ifexist() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("stu_name", enroll_user_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonParam.toString();
                Log.i("aaa", json);
                RequestBody requestBody = RequestBody.create(json, JSON);
                Request request = new Request.Builder().url(ifexisturl).post(requestBody).build();
                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.i("aaa", result);
                    if (result.equals("null"))   ACCOUNT_EXIST = false;
                    else ACCOUNT_EXIST = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

//    //按钮点击事件
//    public void select(View view) {
//        String[] permissions = new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//        //进行sdcard的读写请求
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, permissions, 1);
//        } else {
//            openGallery(); //打开相册，进行选择
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openGallery();
//                } else {
//                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
//                }
//        }
//    }
//
//
//    //打开相册,进行照片的选择
//    private void openGallery() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        startActivityForResult(intent, 1);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 1:
//                if (Build.VERSION.SDK_INT >= 19) {
//                    handleImageOnKitkat(data);
//                } else {
//                    handleImageOnKitkat(data);
//                }
//        }
//    }
//
//
//    @TargetApi(19)
//    private void handleImageOnKitkat(Intent data) {
//        String imagePath = null;
//        try {
//            Uri uri = data.getData();
//            if (DocumentsContract.isDocumentUri(this, uri)) {
//                //如果是document类型的Uri，则通过document id处理
//                String docId = DocumentsContract.getDocumentId(uri);
//                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                    String id = docId.split(":")[1];     //解析出数字格式的id
//                    String selection = MediaStore.Images.Media._ID + "=" + id;
//                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                    imagePath = getImagePath(contentUri, null);
//                }
//            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//                //如果不是document类型的Uri，则使用普通方式处理
//                imagePath = getImagePath(uri, null);
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        imagepath = imagePath;
//        displayImage(imagePath);    //根据图片路径显示图片
//    }
//
//
//    /**
//     * private void handleImageOnKitkat(Intent data) {
//     * Uri uri = data.getData();
//     * String imagePath = getImagePath(uri,null);
//     * displayImage(imagePath);
//     * }
//     */
//
//    private String getImagePath(Uri uri, String selection) {
//        String path = null;
//        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
//    }
//
//    private void displayImage(String imagePath) {
//        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            image.setImageBitmap(bitmap);
//        } else {
//            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
//        }
//    }

    //按钮点击事件
    public void select(View view) {
        String[] permissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
        else{
            openGallery(); //打开相册，进行选择
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
                else{
                    Toast.makeText(this,"读相册的操作被拒绝",Toast.LENGTH_LONG).show();
                }
        }
    }

    //打开相册,进行照片的选择
    private void openGallery(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //               if (Build.VERSION.SDK_INT >= 19) {
                handleSelect(data);
                //              }
//                    handleImageOnKitkat19(data);
                //               }
//                else{
//                    handleImageOnKitkat(data);
                //               }
        }
    }

    private void handleSelect(Intent intent) {
        try {
            Cursor cursor = null;
            Uri uri = intent.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                uploadFileName = cursor.getString(columnIndex);
            }
            InputStream inputStream = getContentResolver().openInputStream(uri);

            byte[] buf = convertToBytes(inputStream);
            inputStream.close();
            Bitmap rawimage = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            Matrix matrix = new Matrix();
            matrix.setScale(0.2f, 0.2f);
            Bitmap bm = Bitmap.createBitmap(rawimage, 0, 0, rawimage.getWidth(), rawimage.getHeight(), matrix, true);
            //Bitmap bm = Bitmap.createScaledBitmap(rawimage, 150, 150, true);
            image.setImageBitmap(bm);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            fileBuf = baos.toByteArray();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }

    public static String Base64(byte[] buf) throws Exception{
        //编码
        return new String(Base64.encode(buf,0));
        //解码，并写入文件
        //Bitmap bitmap = BitmapFactory.decodeByteArray(filebuf,0,filebuf.length);
        //outputStream.write(buf1);
        //outputStream.close();
    }


}

