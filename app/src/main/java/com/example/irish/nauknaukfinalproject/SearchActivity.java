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

/**
 * Search Activity Class. In this class, a user is prompted to search for a professor by either their
 * last name, or their department. Searches by name are shown in a ListView, displaying professor's
 * name and their availability, shown in green or red. Searches by department are performed by clicking
 * on the "Search by Department" image, which will bring up an AlertDialog, prompting the user to pick
 * a department which they wish to view. Similarly, all results are shown to the user in a ListView,
 * displaying a professor's name and availability, shown in green or red.
 *
 * Upon clicking an item in the ListView, ProfessorActivity is launched, and is populated with the
 * information of the professor whose name the user clicked.
 *
 * Sources:
 *      Get Realtime Updates with Cloud Firestore, Google Firestore (Department Queries)
 *          https://firebase.google.com/docs/firestore/query-data/listen
 *      Firestore query documents startsWith a string, StackOverflow (Updating as user searches by name)
 *          https://stackoverflow.com/questions/46573804/firestore-query-documents-startswith-a-string
 *
 * Version: 2.1
 * Authors: Jason Conci, Daniel Abrahms
 */

public class SearchActivity extends AppCompatActivity {
    public final String TAG = "SEARCH_ACTIVITY";
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";

    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    public static String OFFICE_HOURS_KEY = "officeHours";


    private final CharSequence[] DEPARTMENTS = {"All Departments", "Accounting", "Business Administration", "Biology", "Economics", "Finance",
            "Marketing", "Chemistry", "Communication", "Computer Science", "Criminal Justice", "Education", "Engineering", "English",
            "History", "Human Physiology", "Journalism", "Mathematics", "Music", "Nursing", "Philosophy", "Physics", "Political Science",
            "Psychology", "Public Relations", "Sociology", "Sport Management", "Sport and Physical Education", "Theatre Arts"};

    // Firebase fields, as well as GUI Components which will be populated with
    // Query results
    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private String currentUser = "jconci@zagmail.gonzaga.edu";
    private ListView professorListView;
    private TextView departmentTextView;
    private ArrayList<Professor> professorList;
    private FirestoreArrayAdapter professorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Gathering our Firestore collection references
        this.db = FirebaseFirestore.getInstance();
        this.rootReference = db.collection(ROOT_KEY);
        this.professorCollectionRef=db.collection(PROFESSORS_KEY);
        // Ensuring that keyboard usage doesn't squish our images
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Setting up our GUI components
        professorListView = (ListView) findViewById(R.id.searchResultListView);
        professorList = new ArrayList<>();
        professorAdapter = new FirestoreArrayAdapter(this, professorList);
        professorListView.setAdapter(professorAdapter);

        // Listening for text changes within out SearchView.
        // Upon text changed, our "Search by Department" image disappears, and the ListView
        // is populated with the results of querying via getProfessorGivenName(String name)
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



        // Once the "Search by Department" TextView is clicked, the user is prompted via AlertDialog
        // as to which department they'd like to see. Upon selection, the ListView is populated with
        // query results via getProfessorGivenDepartment(String department)
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
                        intent.putExtra(OFFICE_LOCATION_KEY, professor.getOfficeLocation());
                        intent.putExtra(OFFICE_HOURS_KEY, professor.getOfficeHours());
                        intent.putExtra(PHONE_NUMBER_KEY, professor.getPhoneNumber());
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

    /**
     * This method takes a String name as a parameter, and searches our database for Professors
     * whose last name begins with the parameter name. Method for this is seen below, and in sources
     * mentioned in code header.
     *
     * @param name -> The string which we are querying for (professor's lastName begins with name)
     */
    public ArrayList<Professor> getProfessorsGivenName(String name){
        // C-String manipulation; searching between field name and name with the last character shifted.
        // EX: name 'Jason' -> search between 'Jason' and 'Jasop'
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
                        // if e is not null, there's been an error. otherwise, we populate
                        if (e != null) {
                            Log.d(TAG, "There was an issue searching by firstname");
                            return;
                        } else {
                            professorList.clear();
                            for (DocumentSnapshot doc : documentSnapshots) {
                                // Casting our DocumentSnapshot into a Professor object and adding it
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

    /**
     * Method for searching our database given a department. If the user selected the first element of
     * DEPARTMENTS, they are shown all professors. Otherwise, we query our database given the department
     * field. Adding a snapshot listener to this query ensures that our ListView does, in fact, update
     * realtime
     *
     * @param department -> department which we are searching for
     */
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
                // If e is not null, there's been an error. Otherwise, we populate.
                if (e != null) {
                    Log.d(TAG, "Error loading professors. Check department String");
                    return;
                } else {
                    professorList.clear();
                    for(DocumentSnapshot dc: documentSnapshots){
                        // Casting DocumentSnapshot to Professor object and adding it
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

    /**
     * getProfessorsAll method. Queries our database for all professors, an ArrayList of which is then placed
     * into our ListView of professors. SnapshotListener ensures that any changes in the database will
     * update to the ListView in realtime
     */
    public ArrayList<Professor> getProfessorsAll(){
        final ArrayList<Professor> professorList = new ArrayList<>();
        Log.d(TAG, "It's working!");
        // Gathering all professor documents from our professorCollectionReference
        professorCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                // if e is not null, there's been an error. Otherwise, we populate.
                if (e != null) {
                    Log.d(TAG, "Error loading professors. Check department String");
                    return;
                } else {
                    professorList.clear();
                    Log.d(TAG, "Currently updating professors list...");
                    for(DocumentSnapshot dc: documentSnapshots){
                        // Casting our DocumentSnapshot into a Professor Object
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
