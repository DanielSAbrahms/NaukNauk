package com.example.irish.nauknaukfinalproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private int timeLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView test = (TextView) findViewById(R.id.SearchTextView);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                fancyStart(intent, 0);
            }
        });
    }




    private void fancyStart(Intent intent, int selectedItem) {
        final TextView selectedView;
        switch (selectedItem) {
            case 0:
                selectedView = (TextView) findViewById(R.id.SearchTextView);
                timeLeft = 1;
                while(timeLeft < 500) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /*
                            GridLayout.Spec colSpec = new GridLayout.Spec(0, 1, timeLeft)
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams(colSpec, rowSpec);
                            selectedView.
                            */
                        }
                    }, 15);
                    timeLeft++;
                }
                break;
        }
        startActivity(intent);
    }
}
