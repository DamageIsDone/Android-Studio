package com.example.dbapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText et_phone;
    private EditText et_password;
    private Button btn;
    MyTask task; //异步任务

    private Button intentBtn;

    String loginUrl = "http://10.0.2.2:8080/login/";

    public void init() {
        et_phone = (EditText) findViewById(R.id.phone);
        et_password = (EditText) findViewById(R.id.password);
        btn = (Button) findViewById(R.id.login);
        btn.setOnClickListener(listener);

        intentBtn=findViewById(R.id.intent);
        intentBtn.setOnClickListener(listener1);
    }

    private View.OnClickListener listener1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login:
                    String phone = et_phone.getText().toString(); //输入的手机号
                    String password = et_password.getText().toString(); //输入的密码
                    task = new MyTask(); //新建异步任务
                    task.execute( loginUrl, phone, password ); //启动任务
                    break;
            }
        }
    };

    //定义任务类
    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuffer result = new StringBuffer(); //保存返回结果
            try {
                Log.d("flag","url:"+strings[0]);
                URL url = new URL( strings[0] ); //创建URL对象
                //创建HttpURLConnection对象
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000); //设置连接超时为5秒
                conn.setRequestMethod("POST"); //设置请求方式为post
                conn.setRequestProperty("Charset", "UTF-8"); //设置字符集，避免乱码
                conn.connect(); //建立到连接
                //传递给后台的数据
                String data = "phone=" + strings[1] + "&password=" + strings[2]; //参数之间用&连接
                //向服务器发送数据(输出流)
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.close(); os.close();
                //接收服务器信息
                int code = conn.getResponseCode(); //获得服务器状态码，判断一下
                if (code == HttpURLConnection.HTTP_OK) { //200表示成功连接
                    Log.d("flag","200表示成功连接");
                    InputStream is = conn.getInputStream(); //获得输入流
                    //输入字节流转为缓冲流
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    while ((line = br.readLine()) != null) { //一行行读取
                        result.append(line);
                    }
                    //关闭流
                    br.close();
                    is.close();
                }else{
                    Log.d("flag","404 not found");
                }
                conn.disconnect(); //关闭连接
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        } //end doInBackground

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //处理返回值（json串）
            try {
                JSONObject obj = new JSONObject(s); //将json串转为json对象
                Boolean flag = obj.getBoolean("flag"); //解析json对象，获取flag值
                if (flag) {
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    Log.d("flag","登陆成功");
                } else {
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    Log.d("flag","登陆失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            task.cancel(true); //执行完毕后销毁任务
        } //end onPostExecute
    } //end MyTask

    private final ActivityResultLauncher permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        Log.d("flag", "已获得权限");
                    }
                }
            }
    );

    //权限请求
    public void requestPermission() {
        permissionLauncher.launch(Manifest.permission.INTERNET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermission();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel(true); //取消任务
        }
    }
}