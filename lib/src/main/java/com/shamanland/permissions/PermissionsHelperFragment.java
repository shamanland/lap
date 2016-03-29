package com.shamanland.permissions;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionsHelperFragment extends DialogFragment {
    public static final String DIALOG_TAG = PermissionsHelperFragment.class.getName();

    private String[] permissions;
    private String[] rationale;
    private boolean[] grantState;
    private boolean appInfo;

    public static Bundle createArgs(String[] permissions, String[] rationale, boolean[] grantState, boolean appInfo) {
        Bundle r = new Bundle();
        r.putStringArray("permissions", permissions);
        r.putStringArray("rationale", rationale);
        r.putBooleanArray("grantState", grantState);
        r.putBoolean("appInfo", appInfo);
        return r;
    }

    private void resolveArgs() {
        Bundle args = getArguments();
        permissions = args.getStringArray("permissions");
        rationale = args.getStringArray("rationale");
        grantState = args.getBooleanArray("grantState");
        appInfo = args.getBoolean("appInfo");
    }

    public PermissionsHelperFragment() {
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        resolveArgs();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.permissions_required);

        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = permissions.length; i < n; ++i) {
            if (!grantState[i]) {
                sb.append(rationale[i]);
                sb.append("\n\n");
            }
        }

        if (appInfo) {
            sb.append(getText(R.string.permissions_last_line_before_app_info));
        } else {
            sb.append(getText(R.string.permissions_last_line_before_continue));
        }

        builder.setMessage(sb);

        if (appInfo) {
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((PermissionsHelperActivity) getActivity()).onCancelled();
                }
            });

            builder.setPositiveButton(R.string.app_info, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((PermissionsHelperActivity) getActivity()).onOpenSettings();
                }
            });
        } else {
            builder.setPositiveButton(R.string.continue_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((PermissionsHelperActivity) getActivity()).onRequestPermissions(permissions);
                }
            });
        }

        return builder.create();
    }
}
