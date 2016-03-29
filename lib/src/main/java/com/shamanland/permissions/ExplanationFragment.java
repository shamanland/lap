package com.shamanland.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExplanationFragment extends DialogFragment {
    public static final String DIALOG_TAG = ExplanationFragment.class.getName();

    public static final int STATE_GRANTED = 0;
    public static final int STATE_NO_EXPLANATION_NEEDED = 1;
    public static final int STATE_EXPLANATION_NEEDED = 2;
    public static final int STATE_DENIED_NOT_ASK_AGAIN = 3;

    private String[] all;
    private String[] explanations;
    private int[] state;
    private boolean showExplanation;
    private boolean updateSettings;
    private String onlyOne;

    private static Bundle buildArgs(String[] all, String[] explanations, int[] state, boolean showExplanation, boolean updateSettings, String onlyOne) {
        Bundle r = new Bundle();
        r.putStringArray("all", all);
        r.putStringArray("explanations", explanations);
        r.putIntArray("state", state);
        r.putBoolean("showExplanation", showExplanation);
        r.putBoolean("updateSettings", updateSettings);
        r.putString("onlyOne", onlyOne);
        return r;
    }

    private void resolveArgs() {
        Bundle args = getArguments();
        all = args.getStringArray("all");
        explanations = args.getStringArray("explanations");
        state = args.getIntArray("state");
        showExplanation = args.getBoolean("showExplanation");
        updateSettings = args.getBoolean("updateSettings");
        onlyOne = args.getString("onlyOne");
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return super.onCreateDialog(bundle);
        }

        resolveArgs();

        if (showExplanation) {
            final Activity activity = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle(activity.getString(R.string.permissions_required));

            if (onlyOne == null) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0, n = state.length; i < n; ++i) {
                    if (state[i] != STATE_GRANTED) {
                        sb.append(explanations[i]);
                        sb.append("\n\n");
                    }
                }

                builder.setMessage(sb);
            } else {
                builder.setMessage(onlyOne);
            }

            if (updateSettings) {
                builder.setPositiveButton(activity.getString(R.string.update_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        startActivity(intent);
                    }
                });
            } else {
                builder.setPositiveButton(activity.getString(R.string.continue_text), new DialogInterface.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new RequestFragment();
                        fragment.setArguments(RequestFragment.buildArgs(all, 0));

                        activity.getFragmentManager().beginTransaction()
                                .add(fragment, RequestFragment.DIALOG_TAG)
                                .commit();
                    }
                });
            }

            builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(activity, activity.getString(R.string.could_not_continue_without_permissions), Toast.LENGTH_SHORT).show();
                }
            });

            return builder.create();
        }

        return super.onCreateDialog(bundle);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

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
                    Arrays.fill(result, STATE_GRANTED);
                }

                if (activity.shouldShowRequestPermissionRationale(permissions[i])) {
                    result[i] = STATE_EXPLANATION_NEEDED;
                } else {
                    result[i] = STATE_NO_EXPLANATION_NEEDED;
                }
            }
        }

        return result;
    }

    /**
     * @return <b>false</b> means that method didn't perform any request;
     * <b>true</b> means that <code>Activity.requestPermissions()</code> was invoked.
     */
    public boolean requestPermissions(String... permissions) {
        return requestPermissions(0, permissions);
    }

    public boolean requestPermissions(int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        List<String> list = new ArrayList<>(permissions.length);

        Activity activity = getActivity();

        for (String p : permissions) {
            if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }

        if (list.size() > 0) {
            requestPermissions(list.toArray(new String[list.size()]), requestCode);
            return true;
        }

        return false;
    }

    // NOTE this is the simplest implementation, optimize it before use
    public static int[] resolveAll(Activity activity, String[] all, String[] explanations, int[] previousState) {
        int[] state = checkPermissions(activity, all);
        if (state == null) {
            return null;
        }

        int n = all.length;

        boolean showExplanation = false;
        boolean updateSettings = false;
        String onlyOne = null;

        if (previousState != null) {
            if (previousState.length != n) {
                throw new IllegalStateException("int[] must have length " + n + ", but have " + previousState.length);
            }

            for (int i = 0; i < n; ++i) {
                if (previousState[i] == STATE_EXPLANATION_NEEDED && state[i] == STATE_NO_EXPLANATION_NEEDED) {
                    state[i] = STATE_DENIED_NOT_ASK_AGAIN;
                    showExplanation = true;
                    updateSettings = true;
                }
            }
        } else {
            for (int i = 0; i < n; ++i) {
                if (state[i] == STATE_EXPLANATION_NEEDED) {
                    showExplanation = true;

                    if (onlyOne == null) {
                        onlyOne = explanations[i];
                    } else {
                        onlyOne = null;
                        break;
                    }
                }
            }
        }

        if (activity.getFragmentManager().findFragmentByTag(DIALOG_TAG) == null) {
            ExplanationFragment fragment = new ExplanationFragment();
            fragment.setArguments(buildArgs(all, explanations, state, showExplanation, updateSettings, onlyOne));
            fragment.show(activity.getFragmentManager(), DIALOG_TAG);
        }

        return state;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static class RequestFragment extends Fragment {
        public static final String DIALOG_TAG = RequestFragment.class.getName();

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
            resolveArgs();
            requestPermissions(permissions, requestCode);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Toast.makeText(getActivity(), "on perm result", Toast.LENGTH_SHORT).show();
        }
    }
}
