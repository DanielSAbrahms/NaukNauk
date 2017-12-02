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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity {
    private String currentUser = "jconci@zagmail.gonzaga.edu";
    private final int REQUEST_CODE_FAVORITES = 1;
    private final int REQUEST_CODE_SEARCH = 2;
    private final String TAG = "MAINACTIVITY";
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
    // Android widget global variables
    private FirestoreHelper helper = null;
    private final ArrayList<Professor> professorsList = new ArrayList<>();
    private ListView professorsListView;
    private SwipeRefreshLayout swiper;
    private ArrayAdapter<Professor> professorAdapter;



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
        // BLOCK OF FIREBASE REFERENCING
        this.db = FirebaseFirestore.getInstance();
        this.rootReference = db.collection(ROOT_KEY);
        this.professorCollectionRef=db.collection(PROFESSORS_KEY);
        this.studentCollectionRef = db.collection(STUDENTS_KEY);
        Log.d(TAG, new Professor("Jason", "Conci", "conci@gonzaga.edu", "password", "Computer Science").toString());
        FirestoreHelper helper = new FirestoreHelper();
        /*
        helper.addUser(new Professor("Gina", "Sprint", "sprint@gonzaga.edu", "password", "Computer Science"), true);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        } else {
            setContentView(R.layout.activity_main);
        }

    }

    // HELPER METHODS //

    public void onFavoritesClicked(View view){
        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
        intent.putExtra("CURRENT_USER", currentUser);
        startActivity(intent);
    }

    /**
     * Method to update the ArrayList. Method is not 100% necessary anymore, but it doesn't hurt to have
     * in the code. Is needed since data will sometimes change without notifying the adapter,
     * so this is a sort of user-initiated update. Can plug this into a button, or a SwipeToRefresh
     */
    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                professorAdapter.notifyDataSetChanged();
                swiper.setRefreshing(false);
            }
        }, 10);
    }



    // BLOCK OF ARRAY PROVIDERS FOR PROFESSORS WITH LIVE UPDATING FEEDS TO A GLOBAL LISTVIEW //

    /**
     * Returns a list of all professors at GU, updates to screen very well
     * TODO: try to make update as needed, rather than all professors every update
     * @return
     */
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
                    professorAdapter = new FirestoreArrayAdapter(MainActivity.this, professorList);
                    professorsListView.setAdapter(professorAdapter);
                    professorsListView.invalidate();
                    professorAdapter.notifyDataSetChanged();
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
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
        final ArrayList<Professor> professorList = new ArrayList<>();
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
                                                }
                                            });

                                        }
                                    } else {
                                        Log.d(TAG, "Something went wrong in the ProfessorListGivenStudent");
                                    }
                                }
                            });
                    professorAdapter = new FirestoreArrayAdapter(MainActivity.this, professorList);
                    professorsListView.setAdapter(professorAdapter);
                    professorsListView.invalidate();
                    professorAdapter.notifyDataSetChanged();
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }

    // This one works pretty well. Has some goofy behavior within a ListView at times
    public ArrayList<Professor> getProfessorsGivenDepartment(String department){
        final ArrayList<Professor> professorList = new ArrayList<>();
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
                    professorAdapter = new FirestoreArrayAdapter(MainActivity.this, professorList);
                    professorsListView.setAdapter(professorAdapter);
                    professorsListView.invalidate();
                    professorAdapter.notifyDataSetChanged();
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }

    /**
     * provides list of professors given a first or last name.
     * NOTE: naming convention couldn't be less ugly
     * @param name -> name we're searching for
     * @return -> arrayList of professors matching
     */
    public ArrayList<Professor> getProfessorsGivenName(String name){
        final ArrayList<Professor> professorList = new ArrayList<>();
        char[] nameArray = name.toCharArray();
        if(name.compareTo("")==0) return professorList;
        nameArray[name.length()-1] = ( (char) (((nameArray[name.length()-1] - 'a' + 1)%26) + 'a'));
        String upperLimitString = "";
        for(char c: nameArray){
            upperLimitString += String.valueOf(c);
        }
        Log.d(TAG, upperLimitString);
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
                            professorAdapter = new FirestoreArrayAdapter(MainActivity.this, professorList);
                            professorsListView.setAdapter(professorAdapter);
                            professorsListView.invalidate();
                            professorAdapter.notifyDataSetChanged();
                        }
                    }
                });
        return professorList;
    }
}
