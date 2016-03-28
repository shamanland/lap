package com.shamanland.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PermissionsHelper {
    /**
     * @return <b>null</b> means no action required at all, user granted all permissions;
     * otherwise method returns array of exact size as input array, where every item means:
     * 0 - no action required for item, user granted this permission,
     * 1 - no explanation needed, just invoke requestPermissions() synchronously,
     * 2 - explanation needed, you should display custom UI and then try to invoke requestPermissions()
     */
    public static int[] checkPermissions(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }

        int[] result = null;

        for (int i = 0, n = permissions.length; i < n; ++i) {
            if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                if (result == null) {
                    result = new int[n];
                }

                result[i] = activity.shouldShowRequestPermissionRationale(permissions[i]) ? 2 : 1;
            }
        }

        return result;
    }

    /**
     * @return <b>false</b> means that method didn't perform any request;
     * <b>true</b> means that <code>Activity.requestPermissions()</code> was invoked.
     */
    public static boolean requestPermissions(Activity activity, String... permissions) {
        return requestPermissions(activity, 0, permissions);
    }

    /**
     * This method allows to use custom <b>requestCode</b>
     *
     * @see PermissionsHelper#requestPermissions(Activity, String...)
     */
    public static boolean requestPermissions(Activity activity, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        List<String> list = new ArrayList<>(permissions.length);

        for (String p : permissions) {
            if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }

        if (list.size() > 0) {
            activity.requestPermissions(list.toArray(new String[list.size()]), requestCode);
            return true;
        }

        return false;
    }

    // NOTE this is the simplest implementation, optimize it before use
    public static void resolveAll(final Activity activity, final String[] all, String[] explanations) {
        int[] state = PermissionsHelper.checkPermissions(activity, all);
        if (state != null) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0, n = state.length; i < n; ++i) {
                if (state[i] == 2) {
                    sb.append(explanations[i]);
                    sb.append("\n");
                }
            }

            final Dialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("Grant these permissions to continue")
                    .setMessage(sb.toString())
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(activity, "Couldn't continue without permissions", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionsHelper.requestPermissions(activity, all);
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        } else {
            Toast.makeText(activity, "All permissions granted", Toast.LENGTH_SHORT).show();
        }
    }
}


/*

http://webcache.googleusercontent.com/search?q=cache:MEUJoXdX26IJ:stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev+&cd=3&hl=en&ct=clnk

Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
Uri uri = Uri.fromParts("package", getPackageName(), null);
intent.setData(uri);
startActivityForResult(intent, REQUEST_PERMISSION_SETTING);


 */
