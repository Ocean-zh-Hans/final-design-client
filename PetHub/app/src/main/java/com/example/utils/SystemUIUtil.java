package com.example.utils;

import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
public class SystemUIUtil {
    public static void setAllPadding(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    int statusBarHeight = insets.getSystemWindowInsetTop();
                    int navigationBarHeight = insets.getSystemWindowInsetBottom();

                    // 设置padding
                    v.setPadding(0, statusBarHeight, 0, navigationBarHeight);

                    return insets.consumeSystemWindowInsets();
                }
            });
        }
    }

    public static void setPaddingTop(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    int statusBarHeight = insets.getSystemWindowInsetTop();

                    // 设置padding
                    v.setPadding(0, statusBarHeight, 0, 0);

                    return insets.consumeSystemWindowInsets();
                }
            });
        }
    }

    public static void setPaddingBottom(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    int navigationBarHeight = insets.getSystemWindowInsetBottom();

                    // 设置padding
                    v.setPadding(0, 0, 0, navigationBarHeight);

                    return insets.consumeSystemWindowInsets();
                }
            });
        }
    }

}
