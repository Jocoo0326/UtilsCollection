package com.jocoo.translucentbardemo;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class TranslucentBarUtils {

    public static void setStatusBarColor(Window window, int color) {
        if (window == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        } else {
            // 1. add translucent flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 2. add a view with the same height as status bar
            View view = createStatusView(window, color);
            ((FrameLayout) window.getDecorView()).addView(view, 0);

            // 3. set fitSystemWindow
            ViewGroup content = (ViewGroup) ((FrameLayout) window.findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
                content.setClipToPadding(true);
            }
        }
    }

    public static void setStatusBarTranslucent(Window window) {
        if (window == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        // 1. add translucent flag
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 2. set fitSystemWindow
        ViewGroup content = (ViewGroup) ((FrameLayout) window.findViewById(android.R.id.content)).getChildAt(0);
        if (content != null) {
            content.setFitsSystemWindows(true);
            content.setClipToPadding(true);
        }
    }

    private static View createStatusView(Window window, int color) {
        int status_bar_height = 0;
        int resource = window.getContext()
                .getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resource > 0) {
            status_bar_height = window.getContext()
                    .getResources().getDimensionPixelSize(resource);
        }

        View statusView = new View(window.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, status_bar_height);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }
}
