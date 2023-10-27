package com.example.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.R;
import com.example.ui.fragment.MeFragment;
import com.example.ui.fragment.FuncFragment;
import com.example.ui.fragment.HomeFragment;
import com.tencent.mmkv.MMKV;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private Button nav1Button;
    private Button nav2Button;
    private Button nav3Button;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();// 获取 HomeFragment 实例
        Fragment homeFragment = getFragment("fragment1", HomeFragment.class);
        // 获取 Fragment 管理器
        fragmentManager = getSupportFragmentManager();
// 开始 Fragment 事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // 添加 HomeFragment 到容器中
        fragmentTransaction.add(R.id.mainContainer, homeFragment, "fragment1");
        fragmentTransaction.commit(); // 提交事务

        // 底部导航栏按钮1的点击事件处理
        nav1Button.setOnClickListener(view -> {
            Fragment fragment = getFragment("fragment1", HomeFragment.class); // 获取 HomeFragment 实例
            replaceFragment(fragmentManager, fragment, "fragment1", nav1Button); // 切换 Fragment
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏样式
        });

        // 底部导航栏按钮2的点击事件处理
        nav2Button.setOnClickListener(view -> {
            Fragment fragment = getFragment("fragment2", FuncFragment.class); // 获取 FuncFragment 实例
            replaceFragment(fragmentManager, fragment, "fragment2", nav2Button); // 切换 Fragment
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏样式
        });

        // 底部导航栏按钮3的点击事件处理
        nav3Button.setOnClickListener(view -> {
            MMKV.initialize(MainActivity.this);
            MMKV kv = MMKV.defaultMMKV();
            if (kv.getBoolean("isTourist", false)) { // 如果是游客模式，则跳转到登录界面
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else { // 否则获取 MeFragment 实例并切换 Fragment
                Fragment fragment = getFragment("fragment3", MeFragment.class);
                replaceFragment(fragmentManager, fragment, "fragment3", nav3Button);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 设置状态栏样式
            }
        });
    }

    /**
     * 根据 tag 获取 Fragment 实例
     *
     * @param tag
     * @param clazz
     * @return
     */
    private Fragment getFragment(String tag, Class<?> clazz) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = (Fragment) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    /**
     * 切换 Fragment
     *
     * @param fragmentManager
     * @param fragment
     * @param tag
     * @param button
     */
    private void replaceFragment(FragmentManager fragmentManager, Fragment fragment, String tag, Button button) {
        nav1Button.setTextSize(16); // 设置底部导航栏按钮1的字体大小
        nav1Button.setTextColor(getColor(R.color.nav_button_unselected)); // 设置底部导航栏按钮1的字体颜色

        nav2Button.setTextSize(16); // 设置底部导航栏按钮2的字体大小
        nav2Button.setTextColor(getColor(R.color.nav_button_unselected)); // 设置底部导航栏按钮2的字体颜色

        nav3Button.setTextSize(16); // 设置底部导航栏按钮3的字体大小
        nav3Button.setTextColor(getColor(R.color.nav_button_unselected)); // 设置底部导航栏按钮3的字体颜色

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // 开始 Fragment 事务
        fragmentTransaction.replace(R.id.mainContainer, fragment, tag); // 替换当前 Fragment
        fragmentTransaction.commit(); // 提交事务

        button.setTextColor(getColor(R.color.nav_button_selected)); // 设置当前按钮的字体颜色
        button.setTextSize(18); // 设置当前按钮的字体大小
    }

    /**
     * 初始化控件
     */
    private void initView() {
        nav1Button = findViewById(R.id.mainNav1Button); // 获取底部导航栏按钮1
        nav2Button = findViewById(R.id.mainNav2Button); // 获取底部导航栏按钮2
        nav3Button = findViewById(R.id.mainNav3Button); // 获取底部导航栏按钮3
    }

    /**
     * 双击返回按钮退出应用
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainActivity.this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}