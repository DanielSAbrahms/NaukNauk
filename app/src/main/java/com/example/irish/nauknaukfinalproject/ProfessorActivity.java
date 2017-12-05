package com.example.irish.nauknaukfinalproject;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfessorActivity extends AppCompatActivity {
    private final String TAG = "PROFESSOR_ACTIVITY";
    private final int[] IMAGES = {R.drawable.greencheck, R.drawable.redx};
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    private Professor professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

        Intent intent = getIntent();

        if(intent != null){

            // TODO: Add receiving intents for office number/ location - done
            String firstName = intent.getStringExtra(FIRSTNAME_KEY);
            String lastName = intent.getStringExtra(LASTNAME_KEY);
            String email = intent.getStringExtra(EMAIL_KEY);
            String department = intent.getStringExtra(DEPARTMENT_KEY);
            String officeLocation = intent.getStringExtra(OFFICE_LOCATION_KEY);
            String phoneNumber = intent.getStringExtra(PHONE_NUMBER_KEY);
            boolean isAvailable = intent.getBooleanExtra(AVAILABLE_KEY, false);

            professor = new Professor(firstName, lastName, email, "dummy", department, officeLocation, phoneNumber);
            Log.d(TAG, professor.toString());
            //ImageView img = (ImageView) findViewById(R.id.availableImage);
            //img.setImageResource(professor.isAvailable() ? IMAGES[0] : IMAGES[1]);
            TextView nameText = findViewById(R.id.ProfessorNameTextView);
            TextView departmentText = findViewById(R.id.DepartmentTextView);
            TextView emailText = findViewById(R.id.EmailTextView);
            TextView officeLocationText = findViewById(R.id.OfficeLocationTextView);
            TextView phoneNumberText = findViewById(R.id.PhoneNumberTextView);

            nameText.setText(professor.getFirstName() + " " + professor.getLastName());
            departmentText.setText(professor.getDepartment());
            emailText.setText(professor.getEmail());
            officeLocationText.setText((professor.getOfficeLocation()!=null) ? professor.getOfficeLocation() : "Office Location N/A");
            phoneNumberText.setText((professor.getPhoneNumber()!=null) ? professor.getPhoneNumber() : "Phone Number N/A");
        }


        ImageView emailButton = (ImageView) findViewById(R.id.EmailImageButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto: " + professor.getEmail()).buildUpon().build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Choose how you'd like to send"));
            }
        });

        ImageView phoneButton = (ImageView) findViewById(R.id.PhoneNumberImageButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + professor.getPhoneNumber()));
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
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
