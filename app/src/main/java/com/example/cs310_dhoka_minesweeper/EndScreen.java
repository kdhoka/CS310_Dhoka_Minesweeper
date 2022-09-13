package com.example.cs310_dhoka_minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        Intent intent = getIntent();
        String message = "Used " + intent.getIntExtra("time",60) + " seconds.\n";
        if(intent.getBooleanExtra("win",true)){
            message += "You won.\n";
            message += "Good Job!";
        } else {
            message += "You lost.\n";
            message += "Nice try!";
        }
        TextView messageView = (TextView) findViewById(R.id.endMessage);
        messageView.setText(message);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}