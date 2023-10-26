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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private EditText questionEditText;
    private EditText answerEditText;
    private Button toLoginButton;
    private Button submitButton;
    private Handler mSubHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SystemUIUtil.setAllPadding(findViewById(R.id.registerRootConstraintLayout));
        initView();
        openThread();
        clickListen();
    }

    /**
     * 开启一个子线程处理网络请求
     */
    private void openThread() {
        Handler mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ServerResponse<Object> response = (ServerResponse<Object>) msg.obj;
                // 状态码为 0 代表注册成功
                if (response.getStatus() == 0) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    // 跳转登录界面
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent);
                    RegisterActivity.this.finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // 子线程业务逻辑
        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                // 调用 okHttp 工具发送请求并获取响应
                String result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/register.do", params);
                // 将响应结果发送给 UI 线程
                Message message = mUIHandler.obtainMessage();
                Gson gson = new Gson();
                message.obj = gson.fromJson(result, new TypeToken<ServerResponse<Object>>() {}.getType());;
                message.sendToTarget();
                return false;
            }
        }
        // 创建一个并启动子线程对象
        HandlerThread registerThread = new HandlerThread("RegisterThread");
        registerThread.start();
        mSubHandler = new Handler(registerThread.getLooper(), new SubCallback());
    }

    /**
     * 表单本地验证
     */
    private void verifier() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();
        String question = questionEditText.getText().toString();
        String answer = answerEditText.getText().toString();

        if (username.isBlank()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isBlank()) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirm.equals(password)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (question.isBlank()) {
            Toast.makeText(this, "密保问题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (answer.isBlank()) {
            Toast.makeText(this, "密保答案不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 传输表单信息给子线程
        Message message = mSubHandler.obtainMessage();
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
            put("confirm", confirm);
            put("question", question);
            put("answer", answer);
        }};
        message.sendToTarget();
    }

    /**
     * 监听点击事件
     */
    private void clickListen() {
        // 监听用户点击返回按钮
        toolbar.setNavigationOnClickListener(view -> finish());
        // 监听用户点击去登录按钮
        toLoginButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // 监听用户点击提交注册按钮
        submitButton.setOnClickListener(view -> verifier());
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar = findViewById(R.id.registerToolbar);
        usernameEditText = findViewById(R.id.registerUsernameEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmEditText = findViewById(R.id.registerConfirmEditText);
        questionEditText = findViewById(R.id.registerQuestionEditText);
        answerEditText = findViewById(R.id.registerAnswerEditText);
        toLoginButton = findViewById(R.id.registerToLoginButton);
        submitButton = findViewById(R.id.registerSubmitButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubHandler.getLooper().quit();
    }
}