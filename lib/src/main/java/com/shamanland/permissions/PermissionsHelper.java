package com.shamanland.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Arrays;

public class PermissionsHelper {
    public static final int STATE_GRANTED = 0;
    public static final int STATE_NO_RATIONALE_NEEDED = 1;
    public static final int STATE_RATIONALE_NEEDED = 2;
    public static final int STATE_NEVER_ASK_AGAIN = 3;

    public static final int FLAG_SHOW_RATIONALE = 1;
    public static final int FLAG_OPEN_SETTINGS = 1 << 1;
    public static final int FLAG_SINGLE_REQUEST = 1 << 2;

    public static int[] checkPermissions(Activity activity, String[] permissions, int[] previousState) {
        return checkPermissions(activity, permissions, previousState, null);
    }

    public static int[] checkPermissions(Activity activity, String[] permissions, int[] previousState, int[] outFlags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }

        int[] result = null;

        for (int i = 0, n = permissions.length; i < n; ++i) {
            if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                if (result == null) {
                    result = new int[n];
                    Arrays.fill(result, STATE_GRANTED);

                    if (outFlags == null || outFlags.length < 1) {
                        outFlags = new int[1];
                    }

                    outFlags[0] |= FLAG_SINGLE_REQUEST;
                } else if ((outFlags[0] & FLAG_SINGLE_REQUEST) == FLAG_SINGLE_REQUEST) {
                    outFlags[0] &= ~FLAG_SINGLE_REQUEST;
                }

                if (activity.shouldShowRequestPermissionRationale(permissions[i])) {
                    result[i] = STATE_RATIONALE_NEEDED;
                    outFlags[0] |= FLAG_SHOW_RATIONALE;
                } else if (previousState != null && previousState[i] == STATE_RATIONALE_NEEDED) {
                    result[i] = STATE_NEVER_ASK_AGAIN;
                    outFlags[0] |= FLAG_SHOW_RATIONALE | FLAG_OPEN_SETTINGS;
                } else {
                    result[i] = STATE_NO_RATIONALE_NEEDED;
                }
            }
        }

        return result;
    }

    public static int[] ensurePermissions(Activity activity, String[] permissions, String[] rationale, int[] previousState) {
        return ensurePermissions(activity, 0, permissions, rationale, previousState);
    }

    public static int[] ensurePermissions(Activity activity, int requestCode, String[] permissions, String[] rationale, int[] previousState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }

        int[] flags = new int[1];
        int[] state = checkPermissions(activity, permissions, previousState, flags);
        if (state == null) {
            return null;
        }

        activity.startActivityForResult(PermissionsHelperActivity.createIntent(activity, permissions, rationale, state, flags), requestCode);

        return state;
    }
}
