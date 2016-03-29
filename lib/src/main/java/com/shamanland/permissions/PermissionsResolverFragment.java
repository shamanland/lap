package com.shamanland.permissions;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class PermissionsResolverFragment extends DialogFragment {
    public static final String DIALOG_TAG = PermissionsResolverFragment.class.getName();

    private String[] permissions;
    private int requestCode;

    public static Bundle buildArgs(String[] permissions, int requestCode) {
        Bundle r = new Bundle();
        r.putStringArray("permissions", permissions);
        r.putInt("requestCode", requestCode);
        return r;
    }

    private void resolveArgs() {
        Bundle args = getArguments();
        permissions = args.getStringArray("permissions");
        requestCode = args.getInt("requestCode");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolveArgs();
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getActivity(), "on perm result", Toast.LENGTH_SHORT).show();
    }
}
