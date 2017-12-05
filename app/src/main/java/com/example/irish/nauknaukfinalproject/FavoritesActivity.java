package com.example.irish.nauknaukfinalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private final String TAG = "FAVORITES_ACTIVITY";
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    private String currentUser = "jconci@zagmail.gonzaga.edu";



    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private CollectionReference studentCollectionRef = null;
    private ListView favoritesListView;
    private TextView departmentTextView;
    private ArrayList<Professor> professorList;
    private ArrayAdapter<Professor> professorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        this.db = FirebaseFirestore.getInstance();
        this.rootReference = db.collection(ROOT_KEY);
        this.professorCollectionRef=db.collection(PROFESSORS_KEY);
        this.studentCollectionRef = db.collection(STUDENTS_KEY);
        professorList = new ArrayList<>();
        favoritesListView = (ListView) findViewById(R.id.favoritesListView);
        professorAdapter = new FirestoreArrayAdapter(this, getProfessorsGivenStudent("jconci@zagmail.gonzaga.edu"));
        Log.d(TAG, professorList.toString());
        favoritesListView.invalidate();
        professorAdapter.notifyDataSetChanged();
        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txt = view.findViewById(android.R.id.text1);
                String[] split = txt.getText().toString().split("\\s+");
                String email = split[1].toLowerCase() + "@gonzaga.edu";
                professorCollectionRef.document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent = new Intent(FavoritesActivity.this, ProfessorActivity.class);
                        Professor professor = documentSnapshot.toObject(Professor.class);
                        Log.d(TAG, professor.toString());
                        intent.putExtra(FIRSTNAME_KEY, professor.getFirstName());
                        Log.d(TAG, professor.getFirstName());
                        intent.putExtra(LASTNAME_KEY, professor.getLastName());
                        intent.putExtra(DEPARTMENT_KEY, professor.getDepartment());
                        intent.putExtra(EMAIL_KEY, professor.getEmail());
                        intent.putExtra(AVAILABLE_KEY, professor.isAvailable());
                        intent.putExtra(PHONE_NUMBER_KEY, professor.getPhoneNumber());
                        intent.putExtra(OFFICE_LOCATION_KEY, professor.getOfficeLocation());
                        startActivityForResult(intent, 0);
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater balloon = getMenuInflater();
        balloon.inflate(R.menu.refresh_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID){
            case R.id.refreshButton:
                getProfessorsGivenStudent(currentUser);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Returns arrayList of professors within a student's list of favorite professors,
     * given a valid student path.
     * TODO: This acts seriously goofy, get this to update quietly rather than needing to be forced
     * TODO: Figure out if there's a way to do this without a triple listener
     * @param emailTmp
     * @return
     */
    public ArrayList<Professor> getProfessorsGivenStudent(String emailTmp){
        final String email = emailTmp;
        Log.d(TAG, emailTmp);
        // in theory, any update to the professor collection should fire the repopulation of our
        // professor list
        professorCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "Got an update in the professors list based on student");
                if(e==null) {
                    professorList.clear();
                    studentCollectionRef.document(email).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Log.d(TAG, "Got the student in question");
                                    professorList.clear();
                                    if (task.isSuccessful()) {
                                        professorList.clear();
                                        Student student = task.getResult().toObject(Student.class);
                                        List<DocumentReference> docsList = student.getFavorites();
                                        professorList.clear();
                                        for (DocumentReference doc : docsList) {
                                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Log.d(TAG, "Getting the professors now");
                                                    Log.d(TAG, professorList.toString());
                                                    professorList.add(task.getResult().toObject(Professor.class));
                                                    professorAdapter = new FirestoreArrayAdapter(FavoritesActivity.this, professorList);
                                                    favoritesListView.setAdapter(professorAdapter);
                                                    favoritesListView.invalidate();
                                                    professorAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d(TAG, "Something went wrong in the ProfessorListGivenStudent");
                                    }
                                }
                            });

                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }
}
