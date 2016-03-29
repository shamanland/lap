package com.shamanland.permissions.example;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import com.shamanland.permissions.PermissionsHelper;

public class ExampleActivity extends AppCompatActivity {
    private int[] permissionsState;

    @Override
    protected void onResume() {
        super.onResume();

        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        String[] rationale = {
                "Contacts permissions used for accessing leaderboard",
                "Location is used for target ads",
                "Storage is used for cache",
        };

        permissionsState = PermissionsHelper.ensurePermissions(this, permissions, rationale, permissionsState);
        if (permissionsState == null) {
            // all permissions granted
        } else {
            // new activity launched, wait for onResume() again
        }
    }
}
