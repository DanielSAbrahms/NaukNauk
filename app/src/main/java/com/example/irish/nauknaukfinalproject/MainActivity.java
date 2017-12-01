package com.example.irish.nauknaukfinalproject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MAINACTIVITY";
    // Keys for document field-level queries
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String FAVORITES_KEY = "favorites";
    // Keys for collection access
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    // String array of
    private final String[] DEPARTMENTS = {"All Departments", "Accounting", "Business Administration", "Biology", "Economics", "Finance",
            "Marketing", "Chemistry", "Communication", "Computer Science", "Criminal Justice", "Education", "Engineering", "English",
            "History", "Human Physiology", "Journalism", "Mathematics", "Music", "Nursing", "Philosophy", "Physics", "Political Science",
            "Psychology", "Public Relations", "Sociology", "Sport Management", "Sport and Physical Education", "Theatre Arts"};
    // FireStore global access variables (Collection Levels)
    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private CollectionReference studentCollectionRef = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView test = findViewById(R.id.SearchTextView);
        test.setOnClickListener(new View.OnClickListener() {
            //comment to push
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        } else {
            setContentView(R.layout.activity_main);
        }

    }


}
