package com.example.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.constants.AgreementConsts;
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

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private Button touristModeButton;
    private Button toLoginButton;
    private Button toRegisterButton;
    private CheckBox agreeAgreementCheckBox;
    private TextView agreementTextView;
    private boolean isChecked;
    private Handler mSubHandler;
    private MMKV kv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化
        MMKV.initialize(this);
        // 获取全局实例
        kv = MMKV.defaultMMKV();
        // 如果用户曾游客登录则直接进入主页
        if (kv.getBoolean("isTourist", false)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }

        // 如果用户已登录，则判断用户登录是否已过期
        if (kv.getBoolean("isLogin", false)) {
            openThread();
            // 获取本地信息并反序列化 UserAccount 对象
            UserAccount account = kv.decodeParcelable("account", UserAccount.class);
            Message message = mSubHandler.obtainMessage();
            message.obj = account;
            message.sendToTarget();
        }

        setContentView(R.layout.activity_splash);
        SystemUIUtil.setAllPadding(findViewById(R.id.splashRootConstraintLayout));

        initView();
        clickListen(); // 绑定点击事件
        textViewPints(); // 绘制TextView文本使其可点击
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
                // 状态码为 0 代表验证成功
                if (response.getStatus() == 0) {
                    // 跳转主界面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                } else {
                    Toast.makeText(SplashActivity.this, "" + response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // 子线程业务逻辑
        class SubCallback implements Handler.Callback {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                UserAccount account = (UserAccount) msg.obj;
                // 将 UserAccount 对象转换成 HashMap
                Map<String, Object> params =  new HashMap<String, Object>() {{
                    if (account != null) {
                        put("userId", account.getUserId());
                    }
                    if (account != null) {
                        put("updateAt", account.getUpdatedAt());
                    }
                }};
                // 调用 okHttp 工具发送请求并获取响应
                String result = HttpClientUtil.doGet(UrlConsts.ADDRESS, "user/splash.do", params);
                // 将响应结果发送给 UI 线程
                Message message = mUIHandler.obtainMessage();
                Gson gson = new Gson();
                message.obj = gson.fromJson(result, new TypeToken<ServerResponse<Object>>() {
                }.getType());
                message.sendToTarget();
                return false;
            }
        }
        // 创建一个并启动子线程对象
        HandlerThread splashThread = new HandlerThread("SplashThread");
        splashThread.start();
        mSubHandler = new Handler(splashThread.getLooper(), new SubCallback());
    }

    private void touristModePageForward() {
        if (isChecked) {
            kv.putBoolean("isTourist", true);
            kv.putBoolean("isLogin", false);
            kv.remove("account");
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent); // 启动MainActivity
            SplashActivity.this.finish(); // 关闭当前Activity
        } else {
            Toast.makeText(this, "请先阅读并同意用户协议", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转登录注册验证
     *
     * @param cls: 要跳转的页面
     */
    private void pageForward(Class<?> cls) {
        if (isChecked) {
            Intent intent = new Intent(this, cls);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先阅读并同意用户协议", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 绘制 TextView 文本使其可点击
     */
    private void textViewPints() {
        // 创建一个 ClickableSpan 对象，当点击"《用户协议》"时会触发这个对象
        ClickableSpan userAgreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // 当点击"《用户协议》"时，跳转到相应的协议页面
                toAgreementPage(AgreementConsts.USER_AGREEMENT);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                // 设置点击的文本的样式
                super.updateDrawState(ds);
                drawState(ds);
            }
        };

        // 创建一个 ClickableSpan 对象，当点击"《隐私政策》"时会触发这个对象
        ClickableSpan privacyRegistrationClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // 当点击"《隐私政策》"时，跳转到相应的协议页面
                toAgreementPage(AgreementConsts.PRIVACY_REGISTRATION);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                // 设置点击的文本的样式
                super.updateDrawState(ds);
                drawState(ds);
            }
        };

        // 创建一个 ClickableSpan 对象，当点击"《版权声明》"时会触发这个对象
        ClickableSpan copyrightNoticeClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // 当点击"《版权声明》"时，跳转到相应的协议页面
                toAgreementPage(AgreementConsts.COPYRIGHT_NOTICE);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                // 设置点击的文本的样式
                super.updateDrawState(ds);
                drawState(ds);
            }
        };

        // 获取字符串资源
        String userAgreementString = getString(R.string.splash_user_agreement);
        // 创建 SpannableString 对象
        SpannableString spannableString = new SpannableString(userAgreementString);

        // 设置文本中的"《用户协议》"、"《隐私政策》"和"《版权声明》"为可点击的文本
        spannableString.setSpan(userAgreementClickableSpan, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyRegistrationClickableSpan, 13, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(copyrightNoticeClickableSpan, 19, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置 TextView 可以响应点击事件
        agreementTextView.setMovementMethod(LinkMovementMethod.getInstance());
        // 设置 TextView 的文本
        agreementTextView.setText(spannableString);
        // 设置点击文本时的高亮颜色为透明(取消文本高亮)
        agreementTextView.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * 定义一个方法，用于跳转到相应的协议页面
     *
     * @param i: 协议常量值
     */
    private void toAgreementPage(Integer i) {
        // 创建一个Intent对象，用于跳转到AgreementActivity
        Intent intent = new Intent(this, AgreementActivity.class);
        // 将协议的id作为额外数据放入Intent
        intent.putExtra("id", i);
        // 启动AgreementActivity
        startActivity(intent);
    }

    /**
     * 设置点击的文本的样式
     *
     * @param ds: TextPaint 对象
     */
    private void drawState(TextPaint ds) {
        // 设置文本的颜色
        ds.setColor(ContextCompat.getColor(this, R.color.link_text_normal));
        // 取消文本下的下划线
        ds.setUnderlineText(false);
    }

    /**
     * 绑定点击事件
     */
    private void clickListen() {
        touristModeButton.setOnClickListener(view -> touristModePageForward());
        toLoginButton.setOnClickListener(view -> pageForward(LoginActivity.class));
        toRegisterButton.setOnClickListener(view -> pageForward(RegisterActivity.class));
        agreeAgreementCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> this.isChecked = isChecked);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        touristModeButton = findViewById(R.id.splashTouristModeButton);
        toLoginButton = findViewById(R.id.splashToLoginButton);
        toRegisterButton = findViewById(R.id.splashToRegisterButton);
        agreeAgreementCheckBox = findViewById(R.id.splashAgreeAgreementCheckBox);
        agreementTextView = findViewById(R.id.splashAgreementTextView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubHandler.getLooper().quit();
    }
}