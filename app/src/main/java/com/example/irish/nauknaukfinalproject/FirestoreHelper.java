package com.example.irish.nauknaukfinalproject;

import android.support.annotation.NonNull;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FirebaseHelper class. This class is a bit like SQLiteOpenHelper, in a previous project, in that
 * it contains and handles all the hard work of difficult additions and queries, while other classes
 * are not filled with messy code.
 *
 * NOTE: in its current state, this class doesn't do much and is not used.
 *
 * Sources:
 *      Add Data to Cloud Firestore, Google Firestore
 *          https://firebase.google.com/docs/firestore/manage-data/add-data
 *
 * Version: 1.3
 * Author: Jason Conci
 */

public class FirestoreHelper {
    // Implementing these keys SQL-style, so as to avoid bad queries
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String FAVORITES_KEY = "favorites";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_HOURS_KEY = "officeHours";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    // Keys for accessing directories, again SQL-style
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    // Log tag
    public static String TAG = "FIRESTOREHELPER";


    // we leave these fields as null until runtime
    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference professorCollectionRef = null;
    private CollectionReference studentCollectionRef = null;

    // Again, this class is not actually user in this project; however, it will be important for
    // further implementation of our application
    public FirestoreHelper() {
        // initializing our FireStore database 'pointer' to the root of our database
        this.db = FirebaseFirestore.getInstance();
        rootReference = db.collection(ROOT_KEY);
        professorCollectionRef = db.collection(PROFESSORS_KEY);
        studentCollectionRef = db.collection(STUDENTS_KEY);
    }

    /**
     * Method for adding a user to our Firestore database. Not currently user in this project; however,
     * will be important in further app implementation. Takes GUAffiliate as a parameter, and a boolean
     * as to whether or not this user is a professor. Once this is done, the object's fields are added
     * to a HashMap, using appropriate String keys, and new user is placed into our database into the correct
     * collection, either Student or Professor.
     *
     * @param gu -> the new user object which we wish to place in our databae
     * @param isProfessor -> a boolean, telling us whether this user is a professor or student
     */
    public void addUser(GUAffiliate gu, boolean isProfessor) {
        Map<String, Object> user = new HashMap<>();
        user.put(FIRSTNAME_KEY, gu.getFirstName());
        user.put(LASTNAME_KEY, gu.getLastName());
        user.put(EMAIL_KEY, gu.getEmail());
        user.put(PASSWORD_KEY, gu.getPassword());
        // If the user is a Student,
        if (!isProfessor) {
            DocumentReference newStudentDocument = studentCollectionRef.document(gu.getEmail());
            newStudentDocument.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Student addition successful!");
                }
            });
        }
        // Else, the user is a professor.
        else {
            Professor tmpProf = (Professor) gu;
            user.put(DEPARTMENT_KEY, tmpProf.getDepartment());
            user.put(AVAILABLE_KEY, tmpProf.isAvailable());
            user.put(OFFICE_LOCATION_KEY, tmpProf.getOfficeLocation());
            user.put(OFFICE_HOURS_KEY, tmpProf.getOfficeHours());
            user.put(PHONE_NUMBER_KEY, tmpProf.getPhoneNumber());
            DocumentReference newProfessorDocument = professorCollectionRef.document(gu.getEmail());
            newProfessorDocument.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Professor addition successful!");
                }
            });
        }
        Log.d(TAG, "Finished with addition");
    }
}
