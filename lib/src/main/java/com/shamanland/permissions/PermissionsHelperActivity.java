package com.shamanland.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionsHelperActivity extends Activity {
    private static final int RQ_REQUEST_PERMISSIONS = 1;
    private static final int RQ_APP_INFO = 2;

    public static Intent createIntent(Context context, String[] permissions, String[] rationale, boolean[] grantState, boolean appInfo) {
        Intent r = new Intent(context, PermissionsHelperActivity.class);
        r.putExtras(PermissionsHelperFragment.createArgs(permissions, rationale, grantState, appInfo));
        return r;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle == null) {
            PermissionsHelperFragment fragment = new PermissionsHelperFragment();
            fragment.setArguments(getIntent().getExtras());
            fragment.show(getFragmentManager(), PermissionsHelperFragment.DIALOG_TAG);
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }
}
