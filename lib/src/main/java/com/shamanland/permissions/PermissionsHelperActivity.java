package com.shamanland.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import static com.shamanland.permissions.PermissionsHelper.FLAG_SHOW_RATIONALE;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionsHelperActivity extends Activity {
    private String[] permissions;
    private String[] rationale;
    private int[] state;
    private int[] flags;

    public static Intent createIntent(Context context, String[] permissions, String[] rationale, int[] state, int[] flags) {
        Intent r = new Intent(context, PermissionsHelperActivity.class);
        r.putExtra("permissions", permissions);
        r.putExtra("rationale", rationale);
        r.putExtra("state", state);
        r.putExtra("flags", flags);
        return r;
    }

    private void resolveArgs() {
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra("permissions");
        rationale = intent.getStringArrayExtra("rationale");
        state = intent.getIntArrayExtra("state");
        flags = intent.getIntArrayExtra("flags");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        resolveArgs();

        if ((flags[0] & FLAG_SHOW_RATIONALE) == FLAG_SHOW_RATIONALE) {
            // TODO show custom ui
        } else {
            // TODO request permissions
        }

        requestPermissions(permissions, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toast.makeText(this, "permissions result", Toast.LENGTH_SHORT).show();
    }
}
