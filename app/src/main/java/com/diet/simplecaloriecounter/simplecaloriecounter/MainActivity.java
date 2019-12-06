package com.diet.simplecaloriecounter.simplecaloriecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        DBAdapter db = new DBAdapter(this);
        db.open();

        int numberRows = db.count("food");

        if(numberRows < 1) {

            Toast.makeText(this, "Loading Setup...", Toast.LENGTH_LONG).show();
            DBSetupInsert setupInsert = new DBSetupInsert(this);
            setupInsert.insertAllCategories();
            setupInsert.insertAllFood();
            Toast.makeText(this, "Setup Completed!", Toast.LENGTH_LONG).show();

        }
        numberRows = db.count("users");
        if (numberRows < 1){

            Toast.makeText(this, "Just a few steps away from signing up", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, SignUp.class);
            startActivity(i);

        }

        db.close();

        //Toast.makeText(this, "Database Works, food created!", Toast.LENGTH_SHORT).show();
    }
}