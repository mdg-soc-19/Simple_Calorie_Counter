package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    private String [] arraySpinnerDOBDate = new String[31];
    private String [] arraySpinnerDOBYear = new String[thisYear - 1899];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Filling Spinner of Date//

        for (int x = 0; x < 31; x++){
            this.arraySpinnerDOBDate[x] = "" + (x + 1);
        }
        Spinner spinnerDOBDate = findViewById(R.id.spinnerDOBDate);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arraySpinnerDOBDate);
        spinnerDOBDate.setAdapter(adapterDate);

        // Filling Spinner of Year//
        for (int i = thisYear; i >= 1900; i--) {
            this.arraySpinnerDOBYear[thisYear - i] = "" + i;
        }
        Spinner spinnerDOBYear = findViewById(R.id.spinnerDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);

    }

}