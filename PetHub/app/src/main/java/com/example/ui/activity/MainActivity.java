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
import com.example.utils.SharedPreferencesUtil;

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
        initView();

        Fragment postsFragment = getFragment("fragment1", HomeFragment.class);
        Fragment moviesFragment = getFragment("fragment2", FuncFragment.class);
        Fragment meFragment = getFragment("fragment3", MeFragment.class);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainContainer, postsFragment, "fragment1");
        fragmentTransaction.commit();

        nav1Button.setOnClickListener(view -> {
            Fragment fragment = getFragment("fragment1", HomeFragment.class);
            replaceFragment(fragmentManager, fragment, "fragment1", nav1Button);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        });
        nav2Button.setOnClickListener(view -> {
            Fragment fragment = getFragment("fragment2", FuncFragment.class);
            replaceFragment(fragmentManager, fragment, "fragment2", nav2Button);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        });
        nav3Button.setOnClickListener(view -> {
            SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(this);
            if (util.readBoolean("isTourist")) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Fragment fragment = getFragment("fragment3", MeFragment.class);
                replaceFragment(fragmentManager, fragment, "fragment3", nav3Button);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        });
    }

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

    private void replaceFragment(FragmentManager fragmentManager, Fragment fragment, String tag, Button button) {
        nav1Button.setTextSize(16);
        nav1Button.setTextColor(getColor(R.color.nav_button_unselected));

        nav2Button.setTextSize(16);
        nav2Button.setTextColor(getColor(R.color.nav_button_unselected));

        nav3Button.setTextSize(16);
        nav3Button.setTextColor(getColor(R.color.nav_button_unselected));

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment, tag);
        fragmentTransaction.commit();

        button.setTextColor(getColor(R.color.nav_button_selected));
        button.setTextSize(18);
    }

    private void initView() {
        nav1Button = findViewById(R.id.mainNav1Button);
        nav2Button = findViewById(R.id.mainNav2Button);
        nav3Button = findViewById(R.id.mainNav3Button);

    }



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