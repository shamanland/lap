package com.shamanland.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import static com.shamanland.permissions.PermissionsHelper.ACTION_APP_INFO;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionsHelperActivity extends Activity {
    private static final int RQ_REQUEST_PERMISSIONS = 1;
    private static final int RQ_APP_INFO = 2;

    private String[] permissions;
    private String[] rationale;
    private boolean[] grantState;
    private boolean appInfo;

    public static Intent createIntent(Context context, String[] permissions, String[] rationale, boolean[] grantState, int action) {
        boolean appInfo = (action & ACTION_APP_INFO) == ACTION_APP_INFO;

        Intent r = new Intent(context, PermissionsHelperActivity.class);
        r.putExtra("permissions", permissions);
        r.putExtra("rationale", rationale);
        r.putExtra("grantState", grantState);
        r.putExtra("appInfo", appInfo);
        return r;
    }

    private void resolveArgs() {
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra("permissions");
        rationale = intent.getStringArrayExtra("rationale");
        grantState = intent.getBooleanArrayExtra("grantState");
        appInfo = intent.getBooleanExtra("appInfo", false);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        resolveArgs();

        if (bundle == null) {
            showDialog();
        }
    }

    private void showDialog() {
        PermissionsHelperFragment fragment = new PermissionsHelperFragment();
        fragment.setArguments(PermissionsHelperFragment.createArgs(permissions, rationale, grantState, appInfo));
        fragment.show(getFragmentManager(), PermissionsHelperFragment.DIALOG_TAG);
    }

    public void onRequestPermissions(String[] permissions) {
        requestPermissions(permissions, RQ_REQUEST_PERMISSIONS);
    }

    public void onOpenSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, RQ_APP_INFO);
    }

    public void onCancelled() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RQ_APP_INFO) {
            finish();
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RQ_REQUEST_PERMISSIONS) {
            if (grantResults.length == grantState.length) {
                boolean allGranted = true;

                for (int i = 0, n = grantState.length; i < n; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantState[i] = true;
                    } else {
                        grantState[i] = false;
                        allGranted = false;
                    }
                }

                if (allGranted) {
                    setResult(RESULT_OK);
                }
            }

            finish();
        }
    }
}
