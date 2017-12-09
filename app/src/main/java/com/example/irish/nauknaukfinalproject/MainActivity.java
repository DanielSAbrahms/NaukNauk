package com.example.irish.nauknaukfinalproject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * MainActivity class. In this class, Users are prompted as to whether they would like to Search for
 * a Professor, or whether they would like to access their Favorites. Selections are handled in Anonymous
 * listeners, and Favorites/Search Activities are launched thustly.
 *
 * Sources:
 *      Add Data to Cloud, Google Firestore
 *      https://firebase.google.com/docs/firestore/manage-data/add-data
 *
 * Version: 1.2
 * Authors: Jason Conci, Daniel Abrahms
 */
public class MainActivity extends AppCompatActivity {
    private String currentUser = "jconci@zagmail.gonzaga.edu";
    private final int REQUEST_CODE_FAVORITES = 1;
    private final int REQUEST_CODE_SEARCH = 2;
    private final String TAG = "MAINACTIVITY";
    // Keys for document field-level queries


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView test = findViewById(R.id.SearchTextView);

        // Upon SearchTextView click, we launch SearchActivity
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        // Upon FavoritesTextView click, we launch FavoritesActivity
        TextView favorites = (TextView) findViewById(R.id.FavoritesTextView);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        // Block of test code, for adding users to our database (Useful for uncommenting and checking
        // our connection to the database, as this overwrites all professors within the database

        /*
        helper.addUser(new Professor("Gina", "Sprint", "sprint@gonzaga.edu", "password",
                "Computer Science", "HERAK 309B", "(509) 313 3535"), true);
        helper.addUser(new Professor("Shawn", "Bowers", "bowers@gonzaga.edu", "password", "Computer Science"), true);
        helper.addUser(new Professor("Melody", "Alsaker", "alsaker@gonzaga.edu", "password", "Mathematics"), true);
        helper.addUser(new Professor("Scott", "Starbuck", "starbuck@gonzaga.edu", "password", "Religious Studies"), true);
        helper.addUser(new Professor("David", "Schroeder", "schroeder@gonzaga.edu", "password", "Computer Science"), true);
        helper.addUser(new Professor("Robert", "Ray", "ray@gonzaga.edu", "password", "Mathematics"), true);
        helper.addUser(new Professor("Rick", "Stoody", "stoody@gonzaga.edu", "password", "Philosophy"), true);
        helper.addUser(new Professor("Brent", "Diebel", "diebel@gonzaga.edu", "password", "Philosophy"), true);
        helper.addUser(new Professor("Bonni", "Dichone", "dichone@gonzaga.edu", "password", "Mathematics"), true);
        */
    }

    /**
     * Method overridden to ensure that our app doesn't look terrible when orientation changes.
     * @param newConfig -> the new orientation/configuration of the device.
     */
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
