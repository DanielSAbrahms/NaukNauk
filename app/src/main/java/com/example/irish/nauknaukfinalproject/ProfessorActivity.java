package com.example.irish.nauknaukfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfessorActivity extends AppCompatActivity {
    private final String TAG = "PROFESSOR_ACTIVITY";
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    private Professor professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent()!=null){
            Intent intent = getIntent();
            String firstName = intent.getStringExtra(FIRSTNAME_KEY);
            String lastName = intent.getStringExtra(LASTNAME_KEY);
            String email = intent.getStringExtra(EMAIL_KEY);
            String department = intent.getStringExtra(DEPARTMENT_KEY);
            boolean isAvailable = intent.getBooleanExtra(AVAILABLE_KEY, false);
            professor = new Professor(firstName, lastName, email, "dummy", department);
            Log.d(TAG, professor.toString());
            TextView nameText = findViewById(R.id.nameText);
            TextView departmentText = findViewById(R.id.departmentText);
            TextView emailText = findViewById(R.id.emailText);
            nameText.setText(professor.getFirstName() + " " + professor.getLastName());
            departmentText.setText(professor.getDepartment());
            emailText.setText(professor.getEmail());
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto: " + professor.getEmail()).buildUpon().build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Choose how you'd like to send"));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater balloon = getMenuInflater();
        balloon.inflate(R.menu.professor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                ProfessorActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
