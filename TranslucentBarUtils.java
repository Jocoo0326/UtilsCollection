package com.jocoo.daggerdemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Display;
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

    public static void setStatusBarTransparent(Window window) {
        setStatusBarTransparent(window, true, false);
    }

    public static void setStatusBarTransparent(Window window, boolean isFitSystemWindows) {
        setStatusBarTransparent(window, isFitSystemWindows, false);
    }

    public static void setStatusBarTransparent(Window window, boolean isFitSystemWindows, boolean isLightStatusBar) {
        if (window == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        // 1. set flags
        if (isLightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (hasNavBar(window)) {
                    window.getDecorView().setPadding(0, 0, 0, getNavBarHeight(window.getContext()));
                }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        // 2. set fitSystemWindow
        if (isFitSystemWindows)
        {
            ViewGroup content = (ViewGroup) ((FrameLayout) window.findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
                content.setClipToPadding(true);
            }
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean hasNavBar(Window window) {
        Display d = window.getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
