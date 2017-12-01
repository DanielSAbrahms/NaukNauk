package com.example.irish.nauknaukfinalproject;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Jason on 11/30/2017.
 */

public class Student extends GUAffiliate {
    // Note: for using FireStore's place-into-object call, we need a DVC, and from this Firestore
    // can use getters and setters to sort of construct our Object
    private List<DocumentReference> favorites;
    public Student() {
        super();
    }

    /**
     * TODO: add an initialization call for Students' favorite list
     */
    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.favorites = new List<DocumentReference>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<DocumentReference> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(DocumentReference documentReference) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends DocumentReference> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends DocumentReference> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public DocumentReference get(int i) {
                return null;
            }

            @Override
            public DocumentReference set(int i, DocumentReference documentReference) {
                return null;
            }

            @Override
            public void add(int i, DocumentReference documentReference) {

            }

            @Override
            public DocumentReference remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<DocumentReference> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<DocumentReference> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<DocumentReference> subList(int i, int i1) {
                return null;
            }
        };
    }

    public List<DocumentReference> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<DocumentReference>favorites) {
        this.favorites = favorites;
    }

    public Student(String firstName, String lastName, String email, String password, List<DocumentReference> favorites) {
        super(firstName, lastName, email, password);
        this.favorites = favorites;

    }

    public Student(Student student){
        super(student.getFirstName(), student.getLastName(), student.getEmail(), student.getPassword());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
