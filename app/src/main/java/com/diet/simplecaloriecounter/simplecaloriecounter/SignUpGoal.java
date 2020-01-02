package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
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
            db.update("goal", "_id", rowId, "goal_target_weight", doubleTargetWeightSQL);

            int intspinneriWantToSQL = db.quoteSmart(intspinneriWantTo);
            db.update("goal", "_id", rowId, "goal_i_want_to", intspinneriWantToSQL);

            String StringspinnerWeeklyGoalSQL = db.quoteSmart(StringspinnerWeeklyGoal);
            db.update("goal", "_id", rowId, "goal_weekly_goal", StringspinnerWeeklyGoalSQL);


            // Calculating energy
            String[] fields = new String[]{
                    "_id",
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_activity_level"
            };

            Cursor c = db.selectPrimaryKey("users", "_id", rowId, fields);

            String stringUserDob = c.getString(1);
            String stringUserGender = c.getString(2);
            String stringUserHeight = c.getString(3);
            String stringUserActivityLevel = c.getString(4);

            String[] fieldsGoal = new String[]{
                    "_id",
                    "goal_current_weight"
            };
            Cursor cGoal = db.selectPrimaryKey("goal","_id", rowId, fieldsGoal);
            String stringUserCurrentWeight = cGoal.getString(1);

            double doubleUserCurrentWeight = 0;
            try{
                doubleUserCurrentWeight = Double.parseDouble(stringUserCurrentWeight);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            String[] items1 = stringUserDob.split("-");
            String stringUserAge;

            int year = Integer.parseInt(items1[0]);
            int month = Integer.parseInt(items1[1]);
            int date = Integer.parseInt(items1[2]);

            stringUserAge = getAge(year, month, date);

            double BMR = 0;
            if (stringUserGender.startsWith("m")) {
                BMR = 88.362 + (13.397 *doubleUserCurrentWeight ) + (4.799 * Integer.parseInt(stringUserHeight)) - (5.677 * Integer.parseInt(stringUserAge));//different

            } else {
                BMR = 447.593 + (9.247 *doubleUserCurrentWeight ) + (3.098 * Integer.parseInt(stringUserHeight)) - (4.330 * Integer.parseInt(stringUserAge));//different

            }
            BMR = Math.round(BMR);
            ///////////////////////////////////////////// Energy BMR
            double energyBMRSQL = db.quoteSmart(BMR);
            db.update("goal", "_id", rowId, "goal_energy_BMR", energyBMRSQL);

            double proteins1 = Math.round(energyBMRSQL*0.3);
            double carbs1 = Math.round(energyBMRSQL*0.5);
            double fats1 = Math.round(energyBMRSQL*0.2);

            double proteinsSQL1 = db.quoteSmart(proteins1);
            double carbsSQL1 = db.quoteSmart(carbs1);
            double fatsSQL1 = db.quoteSmart(fats1);

            db.update("goal", "_id", rowId, "goal_carbs_BMR", carbsSQL1);
            db.update("goal", "_id", rowId, "goal_proteins_BMR", proteinsSQL1);
            db.update("goal", "_id", rowId, "goal_fat_BMR", fatsSQL1);

            ///////////////////////////////////////////// Energy with Diet

            double doubleWeeklyGoal = Double.parseDouble(StringspinnerWeeklyGoal);
            double KcalDividedBy7 = 1100*doubleWeeklyGoal;

            double energyWithDiet = 0;
            if(intspinneriWantTo == 0){
                energyWithDiet = Math.round((BMR - KcalDividedBy7) * 1.2);
            }else{
                energyWithDiet = Math.round((BMR + KcalDividedBy7) * 1.2);
            }
            double energyWithDietSQL = db.quoteSmart(energyWithDiet);
            db.update("goal", "_id", rowId, "goal_energy_with_diet", energyWithDietSQL);
            double proteins2 = Math.round(energyWithDiet*0.3);
            double carbs2 = Math.round(energyWithDiet*0.5);
            double fats2 = Math.round(energyWithDiet*0.2);

            double proteinsSQL2 = db.quoteSmart(proteins2);
            double carbsSQL2 = db.quoteSmart(carbs2);
            double fatsSQL2 = db.quoteSmart(fats2);

            db.update("goal", "_id", rowId, "goal_carbs_with_diet", carbsSQL2);
            db.update("goal", "_id", rowId, "goal_proteins_with_diet", proteinsSQL2);
            db.update("goal", "_id", rowId, "goal_fat_with_diet", fatsSQL2);

            ///////////////////////////////////////////// Energy with Activity
            double energyWithActivity = 0;
            if(stringUserActivityLevel.equals("0")){
                energyWithActivity = BMR*1.2;
            }else if(stringUserActivityLevel.equals("1")){
                energyWithActivity = BMR*1.375;
            }else if(stringUserActivityLevel.equals("2")){
                energyWithActivity = BMR*1.55;
            }else if(stringUserActivityLevel.equals("3")){
                energyWithActivity = BMR*1.725;
            }else if(stringUserActivityLevel.equals("4")){
                energyWithActivity = BMR*1.9;
            }
            energyWithActivity = Math.round(energyWithActivity);
            double energyWithActivitySQL = db.quoteSmart(energyWithActivity);
            db.update("goal", "_id", rowId, "goal_energy_with_activity", energyWithActivitySQL);

            double proteins3 = Math.round(energyWithActivitySQL*0.3);
            double carbs3 = Math.round(energyWithActivitySQL*0.5);
            double fats3 = Math.round(energyWithActivitySQL*0.2);

            double proteinsSQL3 = db.quoteSmart(proteins3);
            double carbsSQL3 = db.quoteSmart(carbs3);
            double fatsSQL3 = db.quoteSmart(fats3);

            db.update("goal", "_id", rowId, "goal_carbs_with_activity", carbsSQL3);
            db.update("goal", "_id", rowId, "goal_proteins_with_activity", proteinsSQL3);
            db.update("goal", "_id", rowId, "goal_fat_with_activity", fatsSQL3);

            ///////////////////////////////////////////// Energy with Activity and Diet
            double energyWithActivityAndDiet = 0;
            if(intspinneriWantTo == 0){
                energyWithActivityAndDiet = Math.round(BMR - KcalDividedBy7);
            }else{
                energyWithActivityAndDiet = Math.round(BMR + KcalDividedBy7);
            }
            if(stringUserActivityLevel.equals("0")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet* 1.2;
            }
            else if(stringUserActivityLevel.equals("1")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet* 1.375;
            }
            else if(stringUserActivityLevel.equals("2")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet*1.55;
            }
            else if(stringUserActivityLevel.equals("3")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet*1.725;
            }
            else if(stringUserActivityLevel.equals("4")) {
                energyWithActivityAndDiet = energyWithActivityAndDiet* 1.9;
            }
            energyWithActivityAndDiet = Math.round(energyWithActivityAndDiet);


            double energyWithActivityAndDietSQL = db.quoteSmart(energyWithActivityAndDiet);
            db.update("goal", "_id", rowId, "goal_energy_with_activity_and_diet", energyWithActivityAndDietSQL);

            double proteins4 = Math.round(energyWithActivityAndDietSQL*0.3);
            double carbs4 = Math.round(energyWithActivityAndDietSQL*0.5);
            double fats4 = Math.round(energyWithActivityAndDietSQL*0.2);

            double proteinsSQL4 = db.quoteSmart(proteins4);
            double carbsSQL4 = db.quoteSmart(carbs4);
            double fatsSQL4 = db.quoteSmart(fats4);

            db.update("goal", "_id", rowId, "goal_carbs_with_activity_and_diet", carbsSQL4);
            db.update("goal", "_id", rowId, "goal_proteins_with_activity_and_diet", proteinsSQL4);
            db.update("goal", "_id", rowId, "goal_fat_with_activity_and_diet", fatsSQL4 );

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
                "_id",
                "user_mesurment"
        };

        Cursor c = db.selectPrimaryKey("users", "_id", rowId, fields);

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


}
