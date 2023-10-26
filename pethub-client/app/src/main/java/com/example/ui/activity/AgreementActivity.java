package com.example.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.R;
import com.example.constants.AgreementConsts;
import com.example.utils.SystemUIUtil;

public class AgreementActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        SystemUIUtil.setAllPadding(findViewById(R.id.agreementRootConstraintLayout));
        initView();

        // 获取传递的Intent
        Intent intent = getIntent();
        // 通过键名"id"获取传递的值
        int id = intent.getIntExtra("id", 0); // 参数2为默认值，如果没有传递该值，则使用默认值
        String filename = null;
        switch (id) {
            case AgreementConsts.USER_AGREEMENT:
                filename = "user_agreement.html";
                break;
            case AgreementConsts.PRIVACY_REGISTRATION:
                filename = "privacy_registration.html";
                break;
            case AgreementConsts.COPYRIGHT_NOTICE:
                filename = "copyright_notice.html";
                break;
            default:
                finish();
                break;
        }
        
        
        webView.loadUrl("file:///android_asset/agreements/" + filename);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                toolbar.setTitle(view.getTitle());
            }
        });


        toolbar.setNavigationOnClickListener(view -> finish());


    }

    private void initView() {
        toolbar = findViewById(R.id.agreementToolbar);
        webView = findViewById(R.id.agreementContentWebView);
    }
}