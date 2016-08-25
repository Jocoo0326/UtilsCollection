package com.jocoo.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * 运行时请求验证权限 for android 6.0 +
 * <p>
 * Created by Jocoo on 2016/8/8.
 */
public abstract class CheckPermissionBaseActivity extends BaseActivity {
    private static final int REQUEST_CODE = 317;
    private String[] mPermissions;
    private boolean isNeedCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissions = setPermissions();
        if (mPermissions == null || mPermissions.length == 0) {
            throw new IllegalArgumentException("未初始化需要的权限");
        }
    }

    protected abstract String[] setPermissions();

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        String[] deniedPermissions = getDeniedPermissions();
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE);
            return;
        }
        isNeedCheck = false;
    }

    private String[] getDeniedPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String perm : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm)
                != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    perm)) {
                    deniedPermissions.add(perm);
                }
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (!verifyPermissions(grantResults)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

        builder.setPositiveButton("设置",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startAppSettings();
                }
            });

        builder.setCancelable(false);

        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
