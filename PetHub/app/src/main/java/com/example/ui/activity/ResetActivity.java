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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class ResetActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText usernameEditText;
    private EditText answerEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private Button getQuestionButton;
    private Button submitButton;
    private TextView questionTextView;
    private Handler mSubHandler; // 子线程的Handler对象，用于处理子线程的消息
    private String requestUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        SystemUIUtil.setAllPadding(findViewById(R.id.resetRootConstraintLayout));
        initView();
        clickListen();
        openThread();
    }


    private void inquire() {
        String username = usernameEditText.getText().toString();
        requestUsername = username;
        if (username.isBlank()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Message message = mSubHandler.obtainMessage(); // 创建新的消息对象
        message.what = 0;
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
        }};
        message.sendToTarget(); // 发送消息给子线程进行处理
    }

    private void verifier() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();
        String question = questionTextView.getText().toString();
        String answer = answerEditText.getText().toString();

        if (username.isBlank()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!username.equals(requestUsername)) {
            Toast.makeText(this, "用户名发生变化，请重新获取密保问题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (question.isBlank()) {
            Toast.makeText(this, "请先获取密保问题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answer.isBlank()) {
            Toast.makeText(this, "密保答案不能为空", Toast.LENGTH_SHORT).show();
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

        Message message = mSubHandler.obtainMessage(); // 创建新的消息对象
        message.what = 1;
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
            put("confirm", confirm);
            put("question", question);
            put("answer", answer);
        }};
        message.sendToTarget(); // 发送消息给子线程进行处理
    }

    private void openThread() {
        Handler mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = (String) msg.obj;
                Gson gson = new Gson();

                if (0 == msg.what) {
                    ServerResponse response = gson.fromJson(result, new TypeToken<ServerResponse>(){}.getType());
                    System.out.println(msg);
                    int status = response.getStatus();
                    if (status == 0) {  // 获取到密保
                        questionTextView.setText(response.getMsg());
                    } else {
                        Toast.makeText(ResetActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else if (1 == msg.what) {
                    ServerResponse response = gson.fromJson(result, new TypeToken<ServerResponse>() {
                    }.getType());
                    int status = response.getStatus();
                    if (status == 0) {  // 重置密码
                        Toast.makeText(ResetActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                        ResetActivity.this.startActivity(intent);
                        ResetActivity.this.finish();
                    } else {
                        Toast.makeText(ResetActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                Message message = mUIHandler.obtainMessage(); // 创建新的消息对象
                String result = "";
                if (0 == msg.what) {
                    result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/reset.prep", params);
                    message.what = 0;
                } else if (1 == msg.what) {
                    result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/reset.do", params);
                    message.what = 1;
                }
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
        toolbar.setNavigationOnClickListener(view -> finish());
        getQuestionButton.setOnClickListener(view -> inquire());
        submitButton.setOnClickListener(view -> verifier());
    }

    private void initView() {
        toolbar = findViewById(R.id.resetToolbar);
        usernameEditText = findViewById(R.id.resetUsernameEditText);
        answerEditText = findViewById(R.id.resetAnswerEditText);
        passwordEditText = findViewById(R.id.resetPasswordEditText);
        confirmEditText = findViewById(R.id.resetConfirmEditText);
        getQuestionButton = findViewById(R.id.resetGetQuestionButton);
        submitButton = findViewById(R.id.resetSubmitButton);
        questionTextView = findViewById(R.id.resetQuestionTextView);
    }

//


}