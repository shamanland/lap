package com.shamanland.permissions;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionsHelper {
    public static final int ACTION_NO = 0x0;
    public static final int ACTION_REQUEST_PERMISSIONS = 0x1;
    public static final int ACTION_APP_INFO = 0x2;

    public static int checkPermissions(Activity activity, String[] permissions, boolean[] grantState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return ACTION_NO;
        }

        int result = ACTION_NO;

        for (int i = 0, n = permissions.length; i < n; i++) {
            if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                grantState[i] = false;

                if (activity.shouldShowRequestPermissionRationale(permissions[i])) {
                    result |= ACTION_REQUEST_PERMISSIONS;
                } else {
                    result |= ACTION_APP_INFO;
                }
            } else {
                grantState[i] = true;
            }
        }

        return result;
    }

    public static boolean[] ensurePermissions(Activity activity, String[] permissions, String[] rationale) {
        return ensurePermissions(activity, 0, permissions, rationale);
    }

    public static boolean[] ensurePermissions(Activity activity, int requestCode, String[] permissions, String[] rationale) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }

        boolean[] grantState = new boolean[permissions.length];
        int action = checkPermissions(activity, permissions, grantState);
        if (action == ACTION_NO) {
            return null;
        }

        boolean appInfo = (action & ACTION_APP_INFO) == ACTION_APP_INFO;
        Intent intent = PermissionsHelperActivity.createIntent(activity, permissions, rationale, grantState, appInfo);
        activity.startActivityForResult(intent, requestCode);

        return grantState;
    }
}
