package com.example.irish.nauknaukfinalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private final String TAG = "FAVORITES_ACTIVITY";
    private String currentUser;
    private ListView favoritesListView;
    private ArrayAdapter favoritesAdapter;
    private ArrayList<Professor> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        if(getIntent()!=null){
            this.currentUser = getIntent().getStringExtra("CURRENT_USER");
        }
        favoritesList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            favoritesList.add(new Professor());
        }
        favoritesListView = (ListView) findViewById(R.id.favoritesListView);
        favoritesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritesList);
        Log.d(TAG, favoritesList.toString());
        favoritesListView.invalidate();
        favoritesAdapter.notifyDataSetChanged();
    }
}
