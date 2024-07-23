package com.example.dbapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    MyTask task; //网络连接任务
    TextView tv; //显示新闻信息
    Button btn;
    News news; //新闻对象
    String url = "https://news.wust.edu.cn/info/1011/366242.htm";
    //注册权限请求（单个）
    private final ActivityResultLauncher permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        begin(); //准备爬取
                    }
                }
            }
    );

    //权限请求
    public void requestPermission() {
        permissionLauncher.launch(Manifest.permission.INTERNET);
    }

    public void begin() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new MyTask(); //新建异步任务
                task.execute(url); //启动任务
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.resultView);
        btn = (Button) findViewById(R.id.button);
        requestPermission();
    }

    // MyTask实现代码（将下页代码放到此处…）
    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuffer result = new StringBuffer(); //保存返回结果
            try {
                URL url = new URL(strings[0]); //创建URL对象
                //创建HttpURLConnection对象
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000); //设置连接超时为5秒
                conn.setRequestMethod("GET"); //设置请求方式为get(默认)
                conn.setRequestProperty("Charset", "UTF-8"); //设置字符集，避免乱码
                conn.connect(); //建立到连接
                int code = conn.getResponseCode(); //获得服务器状态码，判断一下
                if (code == HttpURLConnection.HTTP_OK) { //200表示成功连接
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
                }
                conn.disconnect(); //关闭连接
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv.setText(s); // 更新UI
            task.cancel(true); //执行完毕后销毁任务
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel(true); //取消任务
        }
    }
} //end MainActivity