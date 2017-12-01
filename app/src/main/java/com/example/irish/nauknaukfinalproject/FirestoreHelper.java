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
 * Created by Jason on 11/30/2017.
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

    public FirestoreHelper() {
        // initializing our FireStore database 'pointer' to the root of our database
        this.db = FirebaseFirestore.getInstance();
        rootReference = db.collection(ROOT_KEY);
        professorCollectionRef = db.collection(PROFESSORS_KEY);
        studentCollectionRef = db.collection(STUDENTS_KEY);
    }

    public void addUser(GUAffiliate gu, boolean isProfessor){
        Map<String, Object> user = new HashMap<>();
        user.put(FIRSTNAME_KEY, gu.getFirstName());
        user.put(LASTNAME_KEY, gu.getLastName());
        user.put(EMAIL_KEY, gu.getEmail());
        user.put(PASSWORD_KEY, gu.getPassword());
        if(!isProfessor){
            DocumentReference newStudentDocument = studentCollectionRef.document(gu.getEmail());
            newStudentDocument.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Student addition successful!");
                }
            });
        } else {
            Professor tmpProf = (Professor) gu;
            user.put(DEPARTMENT_KEY, tmpProf.getDepartment());
            user.put(AVAILABLE_KEY, tmpProf.isAvailable());
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

    /**
     * TODO: get this method actually working and not throwing a nullptr exception
     */
    public Student getStudent(String email){
        class onCompleteCustom implements OnCompleteListener<DocumentSnapshot> {
            Student student;
            int indicator = 0;
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                student = task.getResult().toObject(Student.class);
                Log.d(TAG, student.toString());
                indicator = 1;
            }
            public Student getStudent(){
                while (!(indicator==1)){
                    Log.d(TAG, "BUSY WAITING");
                }
                return student;
            }
        }
        final Student tmpStudent;
        DocumentReference studentRef = studentCollectionRef.document(email);
        onCompleteCustom listener = new onCompleteCustom();
        studentRef.get().addOnCompleteListener(listener);
        return null;
    }

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
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }

    public ArrayList<Professor> getProfessorsGivenName(String name){
        final ArrayList<Professor> professorList = new ArrayList<>();
        char[] nameArray = name.toCharArray();
        if(name.compareTo("")==0) return professorList;
        nameArray[name.length()-1] = ( (char) (((nameArray[name.length()-1] - 'a' + 1)%26) + 'a'));
        String newString = "";
        for(char c: nameArray){
            newString += String.valueOf(c);
        }
        Log.d(TAG, newString);
        final String newStringAccess = newString;
        final String nameAccess = name;
        professorCollectionRef.whereGreaterThanOrEqualTo(FIRSTNAME_KEY, name).whereLessThan(FIRSTNAME_KEY, newString)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.d(TAG, "There was an issue");
                            return;
                        } else {
                            professorList.clear();
                            for (DocumentSnapshot doc: documentSnapshots){
                                professorList.add(doc.toObject(Professor.class));
                            }
                        }
                        professorCollectionRef.whereEqualTo(LASTNAME_KEY, nameAccess).whereLessThan(LASTNAME_KEY, newStringAccess)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if(e!=null){
                                            Log.d(TAG, "There was an issue");
                                            return;
                                        }else {
                                            for(DocumentSnapshot doc: documentSnapshots){
                                                professorList.add(doc.toObject(Professor.class));
                                            }
                                        }
                                    }
                                });
                    }

                });

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
                    /*
                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                professorList.add(dc.getDocument().toObject(Professor.class));
                                break;
                            case MODIFIED:
                                professorList.remove(professorList.get(dc.getOldIndex()));
                                professorList.add(dc.getDocument().toObject(Professor.class));
                                break;
                            case REMOVED:
                                professorList.remove(dc.getOldIndex());
                                break;
                        }
                    }*/
                    for(DocumentSnapshot dc: documentSnapshots){
                        professorList.add(dc.toObject(Professor.class));
                    }
                }
            }
        });
        Log.d(TAG, professorList.toString());
        return professorList;
    }

    /**
     * This method provides an ArrayList of all professors withina a student's array of favorite professors.
     * This array is populated with the FireStore path to each professor's information. Within this method,
     * we listen to changes to the professorCollectionRef CollectionReference, and update our professorList whenever
     * we are notified that this CollectionReference has changed (ie, when a professor's availability changes.)
     *
     * @param emailTmp -> the student email (path) whose favorites we with to access
     * @return -> an ArrayList of professors specified by the Student's favorites.
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
                                    if (task.isSuccessful()) {
                                        Student student = task.getResult().toObject(Student.class);
                                        List<DocumentReference> docsList = student.getFavorites();
                                        for (DocumentReference doc : docsList) {
                                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Log.d(TAG, "Getting the professors now");
                                                    professorList.add(task.getResult().toObject(Professor.class));
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




    /**
     * In this block, we construct getters, but no setters. This is because any class that has access to
     * our FireStore should be able to access the data; however, they should not be able to change it.
     */
    public static String getEmailKey() {
        return EMAIL_KEY;
    }

    public static String getPasswordKey() {
        return PASSWORD_KEY;
    }

    public static String getUsersKey() {
        return USERS_KEY;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public CollectionReference getProfessorCollectionRef() {
        return professorCollectionRef;
    }

    public CollectionReference getStudentCollectionRef() {
        return studentCollectionRef;
    }
}
