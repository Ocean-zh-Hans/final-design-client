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
import com.example.utils.SystemUIUtil;
import com.example.vo.ServerResponse;
import com.example.vo.UserAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button toRegisterButton;
    private Button toResetButton;
    private Button submitButton;
    private Handler mSubHandler;

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

    /**
     * 开启一个子线程处理网络请求
     */
    private void openThread() {
        // UI 线程业务逻辑
        Handler mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ServerResponse<UserAccount> response = (ServerResponse<UserAccount>) msg.obj;

                // 响应码为 0 代表登录成功
                if (response.getStatus() == 0) {  // 登录成功
                    // 保持登录状态
                    MMKV.initialize(LoginActivity.this);
                    MMKV kv = MMKV.defaultMMKV();
                    kv.putBoolean("isTourist", false);
                    kv.putBoolean("isLogin", true);
                    kv.encode("account", response.getData());
                    // 进入主页
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // 子线程业务逻辑
        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                // 调用 okHttp 工具发送请求并获取响应
                String result = HttpClientUtil.doPost(UrlConsts.ADDRESS, "user/login.do", params);
                // 将响应结果发送给 UI 线程
                Message message = mUIHandler.obtainMessage();
                Gson gson = new Gson();
                message.obj = gson.fromJson(result, new TypeToken<ServerResponse<UserAccount>>() {
                }.getType());
                message.sendToTarget();
                return false;
            }
        }
        // 创建一个并启动子线程对象
        HandlerThread loginThread = new HandlerThread("LoginThread");
        loginThread.start();
        mSubHandler = new Handler(loginThread.getLooper(), new SubCallback());
    }

    /**
     * 表单本地验证
     */
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
        // 传输表单信息给子线程
        Message message = mSubHandler.obtainMessage();
        message.obj = new HashMap<String, Object>() {{
            put("username", username);
            put("password", password);
        }};
        message.sendToTarget();
    }

    /**
     * 监听点击事件
     */
    private void clickListen() {
        toolbar.setNavigationOnClickListener(view -> finish());
        toRegisterButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        toResetButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ResetActivity.class);
            startActivity(intent);
        });
        submitButton.setOnClickListener(view -> verifier());
    }

    /**
     * 初始化控件
     */
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