package com.example.irish.nauknaukfinalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public final String TAG = "SEARCH_ACTIVITY";
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    private final CharSequence[] DEPARTMENTS = {"All Departments", "Accounting", "Business Administration", "Biology", "Economics", "Finance",
            "Marketing", "Chemistry", "Communication", "Computer Science", "Criminal Justice", "Education", "Engineering", "English",
            "History", "Human Physiology", "Journalism", "Mathematics", "Music", "Nursing", "Philosophy", "Physics", "Political Science",
            "Psychology", "Public Relations", "Sociology", "Sport Management", "Sport and Physical Education", "Theatre Arts"};



    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private ListView professorListView;
    private TextView departmentTextView;
    private ArrayList<Professor> professorList;
    private FirestoreArrayAdapter professorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.db = FirebaseFirestore.getInstance();
        this.rootReference = db.collection(ROOT_KEY);
        this.professorCollectionRef=db.collection(PROFESSORS_KEY);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        professorListView = (ListView) findViewById(R.id.searchResultListView);
        professorList = new ArrayList<>();
        professorAdapter = new FirestoreArrayAdapter(this, professorList);
        professorListView.setAdapter(professorAdapter);

        final SearchView searchy = findViewById(R.id.NameSearchBar);
        searchy.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                departmentTextView = findViewById(R.id.searchByDepartmentTextView);
                if(s.compareTo("")==0) departmentTextView.setVisibility(View.VISIBLE);
                else {
                    departmentTextView.setVisibility(View.GONE);
                    getProfessorsGivenName(s);
                }
                return false;
            }
        });
        searchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchy.setIconified(false);
            }
        });



        departmentTextView = findViewById(R.id.searchByDepartmentTextView);
        departmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Department Clicked!");
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("Pick a Department!");
                builder.setItems(DEPARTMENTS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        getProfessorsGivenDepartment(DEPARTMENTS[which].toString());
                    }
                });
                builder.show();
            }
        });

        /**
         * When someone clicks on a Professor's name in a ListView, the Professor's fields are gathered
         * and passed into ProfessorActivity, where the user is shown all their information, and is given
         * the option to email them via implicit intent.
         * TODO: get the naming convention WAY better for professors ( pull from list,. don't assume last name )
         */
        professorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txt = view.findViewById(android.R.id.text1);
                String[] split = txt.getText().toString().split("\\s+");
                String email = split[1].toLowerCase() + "@gonzaga.edu";
                professorCollectionRef.document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent = new Intent(SearchActivity.this, ProfessorActivity.class);
                        Professor professor = documentSnapshot.toObject(Professor.class);
                        Log.d(TAG, professor.toString());
                        intent.putExtra(FIRSTNAME_KEY, professor.getFirstName());
                        Log.d(TAG, professor.getFirstName());
                        intent.putExtra(LASTNAME_KEY, professor.getLastName());
                        intent.putExtra(DEPARTMENT_KEY, professor.getDepartment());
                        intent.putExtra(EMAIL_KEY, professor.getEmail());
                        intent.putExtra(AVAILABLE_KEY, professor.isAvailable());
                        startActivityForResult(intent, 0);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<Professor> getProfessorsGivenName(String name){
        final ArrayList<Professor> professorList = new ArrayList<>();
        char[] nameArray = name.toCharArray();
        if(name.compareTo("")==0) return professorList;
        nameArray[name.length()-1] = ( (char) (((nameArray[name.length()-1] - 'a' + 1)%26) + 'a'));
        String upperLimitString = "";
        for(char c: nameArray){
            upperLimitString+=String.valueOf(c).toLowerCase();
        }
        // Ensuring that each name we Query begins with an uppercase letter and all others are lowercase
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        upperLimitString = upperLimitString.substring(0,1).toUpperCase() + upperLimitString.substring(1);

        Log.d(TAG, name + " TO " + upperLimitString);
        professorCollectionRef.whereGreaterThanOrEqualTo(LASTNAME_KEY, name)
                .whereLessThan(LASTNAME_KEY, upperLimitString)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "There was an issue searching by firstname");
                            return;
                        } else {
                            professorList.clear();
                            for (DocumentSnapshot doc : documentSnapshots) {
                                professorList.add(doc.toObject(Professor.class));
                            }
                            professorAdapter = new FirestoreArrayAdapter(SearchActivity.this, professorList);
                            professorListView.setAdapter(professorAdapter);
                            professorListView.invalidate();
                            professorAdapter.notifyDataSetChanged();
                        }
                    }
                });
        return professorList;
    }

    public ArrayList<Professor> getProfessorsGivenDepartment(String department){
        final ArrayList<Professor> professorList = new ArrayList<>();
        // If the user selects "All Departments", we call upon our getProfessorsAll() method to handle it.
        if(department==DEPARTMENTS[0].toString()){
            getProfessorsAll();
            return null;
        }
        professorCollectionRef.whereEqualTo(DEPARTMENT_KEY, department).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error loading professors. Check department String");
                    return;
                } else {
                    professorList.clear();
                    for(DocumentSnapshot dc: documentSnapshots){
                        professorList.add(dc.toObject(Professor.class));
                    }
                    professorAdapter = new FirestoreArrayAdapter(SearchActivity.this, professorList);
                    professorListView.setAdapter(professorAdapter);
                    professorListView.invalidate();
                    professorAdapter.notifyDataSetChanged();
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }

    public ArrayList<Professor> getProfessorsAll(){
        final ArrayList<Professor> professorList = new ArrayList<>();
        Log.d(TAG, "It's working!");
        professorCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error loading professors. Check department String");
                    return;
                } else {
                    professorList.clear();
                    Log.d(TAG, "Currently updating professors list...");
                    for(DocumentSnapshot dc: documentSnapshots){
                        if(dc.toObject(Professor.class).getFirstName().compareTo("Jason")!=0)
                            professorList.add(dc.toObject(Professor.class));
                    }
                    professorAdapter = new FirestoreArrayAdapter(SearchActivity.this, professorList);
                    professorListView.setAdapter(professorAdapter);
                    professorListView.invalidate();
                    professorAdapter.notifyDataSetChanged();
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }
}
