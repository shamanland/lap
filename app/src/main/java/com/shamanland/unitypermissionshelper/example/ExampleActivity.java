package com.shamanland.unitypermissionshelper.example;

import android.Manifest;
import android.app.Activity;

import com.shamanland.permissions.PermissionsHelper;

public class ExampleActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();

        String[] all = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        String[] explanations = {
                "Contacts permissions used for accessing leaderboard",
                "Location is used for target ads",
                "Storage is used for cache",
        };

        PermissionsHelper.resolveAll(this, all, explanations);
    }
}
