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
    private Handler mSubHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        SystemUIUtil.setAllPadding(findViewById(R.id.resetRootConstraintLayout));
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
                ServerResponse<Object> response = (ServerResponse<Object>) msg.obj;
                System.out.println(response.toString());
                if (1 == msg.what) {
                    //获取到密保
                    if (response.getStatus() == 0) {
                        questionTextView.setText(response.getMsg());
                    } else {
                        Toast.makeText(ResetActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else if (2 == msg.what) {
                    // 密码重置成功
                    if (response.getStatus() == 0) {
                        Toast.makeText(ResetActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        ResetActivity.this.finish();
                    } else {
                        Toast.makeText(ResetActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        // 子线程业务逻辑
        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                Message message = mUIHandler.obtainMessage();
                String result = "";
                if (1 == msg.what) {
                    // msg.what = 1 获取密保问题请求
                    result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/reset.prep", params);
                    message.what = 1;
                } else if (2 == msg.what) {
                    // msg.what = 2 获取重置密码请求
                    result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/reset.do", params);
                    message.what = 2;
                }
                // 将响应结果发送给 UI 线程
                Gson gson = new Gson();
                message.obj = gson.fromJson(result, new TypeToken<ServerResponse<Object>>() {
                }.getType());;
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
     * 获取问题
     */
    private void inquire() {
        // 获取用户名
        String username = usernameEditText.getText().toString();
        submitButton.setEnabled(true);
        // 非空验证
        if (username.isBlank()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 创建一个并启动子线程对象
        Message message = mSubHandler.obtainMessage();
        message.what = 1;
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
        }};
        message.sendToTarget();
    }

    /**
     * 表单本地验证
     */
    private void verifier() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();
        String question = questionTextView.getText().toString();
        String answer = answerEditText.getText().toString();

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
        // 创建一个并启动子线程对象
        Message message = mSubHandler.obtainMessage();
        message.what = 2;
        message.obj = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
            put("confirm", confirm);
            put("question", question);
            put("answer", answer);
        }};
        message.sendToTarget();
    }

    private void clickListen() {
        toolbar.setNavigationOnClickListener(view -> finish());
        getQuestionButton.setOnClickListener(view -> inquire());
        submitButton.setOnClickListener(view -> verifier());
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                submitButton.setEnabled(false);
            }
        });
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