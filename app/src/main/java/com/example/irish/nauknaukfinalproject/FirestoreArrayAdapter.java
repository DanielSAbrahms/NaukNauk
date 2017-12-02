package com.example.irish.nauknaukfinalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Created by Jason on 11/30/2017.
 */

public class FirestoreArrayAdapter extends ArrayAdapter<Professor>{
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String FAVORITES_KEY = "favorites";
    // Keys for accessing directories, again SQL-style
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    // Log tag
    public static String TAG = "FIRESTOREHELPER";

    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private CollectionReference studentCollectionRef = null;

    final int[] IMAGES = {R.drawable.greenlight, R.drawable.redlight};
    private ArrayAdapter<Professor> professorAdapter;
    private Context context;
    private String sortMode;

    public FirestoreArrayAdapter(Context context, ArrayList<Professor> professorList){
        super(context, android.R.layout.simple_list_item_1, professorList);
        this.notifyDataSetChanged();
        this.db = FirebaseFirestore.getInstance();
        rootReference = db.collection(ROOT_KEY);
        professorCollectionRef = db.collection(PROFESSORS_KEY);
        studentCollectionRef = db.collection(STUDENTS_KEY);
        this.context = context;
        Log.d(TAG, "List: "+ professorList.toString());
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Professor professor = getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext())
                    .inflate(android.R.layout.activity_list_item, parent, false);
        }
        convertView.setMinimumHeight(80);

        TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
        txt.setText(professor.firstName + " " + professor.lastName);
        txt.setTextSize(30);

        ImageView img = (ImageView) convertView.findViewById(android.R.id.icon);
        img.setImageResource(professor.isAvailable ? IMAGES[0] : IMAGES[1]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60,60);
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
