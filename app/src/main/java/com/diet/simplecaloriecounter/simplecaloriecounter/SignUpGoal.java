package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignUpGoal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_goal);

        hidingError();

        measurementUsed();

        Button buttonSignUpGoal = findViewById(R.id.buttonSignUpGoal);
        buttonSignUpGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpGoalSubmit();

            }
        });
    }





    public void signUpGoalSubmit(){

        DBAdapter db = new DBAdapter(this);
        db.open();

        ImageView imageViewError = findViewById(R.id.imageViewErrorGoal);
        TextView textViewErrorMessage = findViewById(R.id.textViewErrorMessageGoal);
        String errorMessage = "";


        EditText editTextTargetWeight = findViewById(R.id.editTextTargetWeight);
        String stringTargetWeight = editTextTargetWeight.getText().toString();
        double doubleTargetWeight = 0;
        try{
            doubleTargetWeight = Double.parseDouble(stringTargetWeight);
        }catch (NumberFormatException nfe){
            errorMessage = "Target Weight has to be a number";
        }

        Spinner spinneriWantTo = findViewById(R.id.spinneriWantTo);
        int intspinneriWantTo = spinneriWantTo.getSelectedItemPosition();

        Spinner spinnerWeeklyGoal = findViewById(R.id.spinnerWeeklyGoal);
        String StringspinnerWeeklyGoal = spinnerWeeklyGoal.getSelectedItem().toString();




        if(errorMessage.isEmpty()) {
            imageViewError.setVisibility(View.GONE);
            textViewErrorMessage.setVisibility(View.GONE);

            long rowId = 1;
            double doubleTargetWeightSQL = db.quoteSmart(doubleTargetWeight);
            db.update("goal", "goal_id", rowId, "goal_target_weight", doubleTargetWeightSQL);

            int intspinneriWantToSQL = db.quoteSmart(intspinneriWantTo);
            db.update("goal", "goal_id", rowId, "goal_i_want_to", intspinneriWantToSQL);

            String StringspinnerWeeklyGoalSQL = db.quoteSmart(StringspinnerWeeklyGoal);
            db.update("goal", "goal_id", rowId, "goal_weekly_goal", StringspinnerWeeklyGoalSQL);


            // Calculating energy
            String[] fields = new String[]{
                    "user_id",
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_activity_level"
            };

            Cursor c = db.selectPrimaryKey("users", "user_id", rowId, fields);

            String stringUserDob = c.getString(1);
            String stringUserGender = c.getString(2);
            String stringUserHeight = c.getString(3);
            String stringUserActivityLevel = c.getString(4);


            String[] items1 = stringUserDob.split("-");
            String stringUserAge;

            int year = Integer.parseInt(items1[0]);
            int month = Integer.parseInt(items1[1]);
            int date = Integer.parseInt(items1[2]);

            stringUserAge = getAge(year, month, date);

            double BMR = 0;
            if (stringUserGender.startsWith("m")) {
                BMR = 88.362 + (13.397 * doubleTargetWeight) + (4.799 * Integer.parseInt(stringUserHeight)) - (5.677 * Integer.parseInt(stringUserAge));//different

            } else {
                BMR = 447.593 + (9.247 * doubleTargetWeight) + (3.098 * Integer.parseInt(stringUserHeight)) - (4.330 * Integer.parseInt(stringUserAge));//different

            }
            BMR = Math.round(BMR);
            double energyBMRSQL = db.quoteSmart(BMR);
            db.update("goal", "goal_id", rowId, "goal_energy_BMR", energyBMRSQL);

            if(stringUserActivityLevel.equals("0")){
                BMR = (int)(BMR*1.2);
            }else if(stringUserActivityLevel.equals("1")){
                BMR = (int)(BMR*1.375);
            }else if(stringUserActivityLevel.equals("2")){
                BMR = (int)(BMR*1.55);
            }else if(stringUserActivityLevel.equals("3")){
                BMR = (int)(BMR*1.725);
            }else if(stringUserActivityLevel.equals("4")){
                BMR = (int)(BMR*1.9);
            }

            double energyWithActivitySQL = db.quoteSmart(BMR);
            db.update("goal", "goal_id", rowId, "goal_energy_with_activity", energyWithActivitySQL);


            double doubleWeeklyGoal = Double.parseDouble(StringspinnerWeeklyGoal);
            double kcal = 7700*doubleWeeklyGoal;
            double energyWithActivityAndDiet = 0;
            if(intspinneriWantTo == 0){
                energyWithActivityAndDiet = Math.round(BMR - (kcal/7));
            }else{
                energyWithActivityAndDiet = Math.round(BMR + (kcal/7));
            }

            double energyWithActivityAndDietSQL= db.quoteSmart(energyWithActivityAndDiet);
            db.update("goal", "goal_id", rowId, "goal_energy_with_activity_and_diet", energyWithActivityAndDietSQL);

            double proteins = Math.round(energyWithActivityAndDiet*0.3);
            double carbs = Math.round(energyWithActivityAndDiet*0.5);
            double fats = Math.round(energyWithActivityAndDiet*0.2);

            double proteinsSQL = db.quoteSmart(proteins);
            double carbsSQL = db.quoteSmart(carbs);
            double fatsSQL = db.quoteSmart(fats);

            db.update("goal", "goal_id", rowId, "goal_carbs", carbsSQL);
            db.update("goal", "goal_id", rowId, "goal_proteins", proteinsSQL);
            db.update("goal", "goal_id", rowId, "goal_fat", fatsSQL );

            abc(energyBMRSQL , energyWithActivitySQL, energyWithActivityAndDietSQL, carbsSQL, fatsSQL, proteinsSQL);
            db.close();

            Intent i = new Intent(SignUpGoal.this, MainActivity.class);
            startActivity(i);


        } else {
            textViewErrorMessage.setText(errorMessage);
            imageViewError.setVisibility(View.VISIBLE);
            textViewErrorMessage.setVisibility(View.VISIBLE);
        }

    }


    public void hidingError(){
        ImageView imageViewError = findViewById(R.id.imageViewErrorGoal);
        imageViewError.setVisibility(View.GONE);

        TextView textViewErrorMessage = findViewById(R.id.textViewErrorMessageGoal);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    public void measurementUsed(){
        DBAdapter db = new DBAdapter(this);
        db.open();

        long rowId = 1;
        String[] fields = new String[]{
                "user_id",
                "user_mesurment"
        };

        Cursor c = db.selectPrimaryKey("users", "user_id", rowId, fields);

        String measurement = c.getString(1);

        TextView textViewTargetMeasurement = findViewById(R.id.textViewTargetMesurment);
        TextView textViewTargetWeeklyGoal2 = findViewById(R.id.textViewWeeklyGoal2);

        if(measurement.startsWith("i")){
            textViewTargetMeasurement.setText("pound");
            textViewTargetWeeklyGoal2.setText("pound each week");
        }

        db.close();
    }
    private String getAge(int year, int month, int date){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            --age;
        }

        return "" + age;
    }
    public void abc(double a, double b, double c, double d, double e, double f){
        Toast.makeText(this,"Basal Metabolic Rate in Cal/day = " + a + "\nGoal with Activity in Cal/day = " + b + "\nGoal with Activity and Diet in Cal/day = " + c + "\nCarbohydrates required in cal/day = " + d + "\nFats required in cal/day = " + e + "\nProteins required in cal/day = " + f,Toast.LENGTH_LONG).show();

    }
    public void abc(String a){
        Toast.makeText(this,a,Toast.LENGTH_LONG).show();

    }

}
