package com.example.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.R;
import com.example.constants.UrlConsts;
import com.example.utils.HttpClientUtil;
import com.example.utils.SharedPreferencesUtil;
import com.example.utils.SystemUIUtil;
import com.example.vo.ServerResponse;
import com.example.vo.UserAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button toRegisterButton;
    private Button toResetButton;
    private Button submitButton;
    private Handler mSubHandler; // 子线程的Handler对象，用于处理子线程的消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 设置控件的内边距为状态栏和导航栏的高度
        SystemUIUtil.setAllPadding(findViewById(R.id.loginRootConstraintlayout));

        initView();
        clickListen();
        openThread();
        }

    private void verifier() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isBlank()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isBlank()) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = mSubHandler.obtainMessage(); // 创建新的消息对象
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
        }};
        message.sendToTarget(); // 发送消息给子线程进行处理
    }

    private void openThread() {
        Handler mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = (String) msg.obj;
                Gson gson = new Gson();
                ServerResponse<UserAccount> response = gson.fromJson(result, new TypeToken<ServerResponse<UserAccount>>(){}.getType());

                int status = response.getStatus();
                if (status == 0) {  // 登录成功
                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(LoginActivity.this);
                    util.putBoolean("isLogin", true);
                    util.putBoolean("isTourist", false);
                    util.putString("account", response.getData().toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                String result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/login.do", params);
                Message message = mUIHandler.obtainMessage(); // 创建新的消息对象
                message.obj = result;
                message.sendToTarget(); // 发送消息给UI线程进行处理
                return false;
            }
        }

        HandlerThread loginThread = new HandlerThread("LoginThread"); // 创建一个HandlerThread对象
        loginThread.start(); // 启动子线程
        mSubHandler = new Handler(loginThread.getLooper(), new SubCallback()); // 创建子线程的Handler对象
    }


    private void clickListen() {
        // 监听用户点击返回按钮
        toolbar.setNavigationOnClickListener(view -> finish());
        // 监听用户点击去注册按钮
        toRegisterButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        // 监听用户点击重置密码按钮
        toResetButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ResetActivity.class);
            startActivity(intent);
        });
        // 监听用户点击提交登录按钮
        submitButton.setOnClickListener(view -> verifier());
    }

    private void initView() {
        toolbar = findViewById(R.id.loginToolbar);
        usernameEditText = findViewById(R.id.loginUsernameEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        toRegisterButton = findViewById(R.id.loginToRegisterButton);
        toResetButton = findViewById(R.id.loginToResetButton);
        submitButton = findViewById(R.id.loginSubmitButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubHandler.getLooper().quit();
    }

}