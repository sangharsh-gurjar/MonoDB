package com.example.mongodbtut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    Button SetData ;
    Button GetData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetData = findViewById(R.id.SetData);
        GetData =findViewById(R.id.GetData);
        SetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), SetData.class);
                startActivity(intent);
                
            }
        });
        GetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), GetData.class);
                startActivity(intent);

            }
        });




    }



}