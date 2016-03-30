package com.shamanland.permissions.example;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.shamanland.permissions.PermissionsHelper;

public class ExampleActivity extends AppCompatActivity {
    String[] permissions = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    String[] rationale = {
            "\u2022 Contacts permissions used for accessing leaderboard",
            "\u2022 Location is used for target ads",
            "\u2022 Storage is used for cache",
    };

    boolean intention;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle != null) {
            intention = bundle.getBoolean("intention");
        }

        setContentView(R.layout.a_example);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putBoolean("intention", intention);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (intention) {
                doSomething();
            }
        }
    }

    protected void doSomething() {
        Intent intent = PermissionsHelper.ensurePermissions(this, permissions, rationale);
        if (intent == null) {
            Toast.makeText(this, "You did something", Toast.LENGTH_SHORT).show();
            intention = false;
        } else if (intention) {
            Toast.makeText(this, "Can't do something without permissions", Toast.LENGTH_SHORT).show();
            intention = false;
        } else {
            startActivityForResult(intent, 1);
            intention = true;
        }
    }
}
