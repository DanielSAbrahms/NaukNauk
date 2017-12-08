package com.example.irish.nauknaukfinalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Login Activity class. This Activity prompts the user for a username and password for their account.
 * While this does nothing at the moment, in the future, this is where we will implement user authentication,
 * as well as registration for new users to our application.
 *
 * Sources:
 *
 * Version: 1.0
 * Authors: Jason Conci, Daniel Abrahms
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_top);
            }
        });
    }
}
