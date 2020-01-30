package com.diet.simplecaloriecounter.simplecaloriecounter.ui.goal;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.simplecaloriecounter.simplecaloriecounter.DBAdapter;
import com.diet.simplecaloriecounter.simplecaloriecounter.IOnBackPressed;
import com.diet.simplecaloriecounter.simplecaloriecounter.MainActivity;
import com.diet.simplecaloriecounter.simplecaloriecounter.R;

import java.util.Calendar;


public class GoalFragment extends Fragment implements IOnBackPressed {

    private GoalViewModel mViewModel;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private View mainView;

    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;


    private String currentId;
    private String currentName;




    private OnFragmentInteractionListener mListener;

    public GoalFragment() {
        // Required empty public constructor
    }

    public static GoalFragment newInstance() {
        return new GoalFragment();
    }

    public static GoalFragment newInstance(String param1, String param2) {
        GoalFragment fragment = new GoalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(GoalViewModel.class);

        /* Set title */
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Goal");

        // getDataFromDbAndDisplay
        initializeGetDataFromDbAndDisplay();

        // Create menu
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_goal, menu);

        menuItemEdit = menu.findItem(R.id.menu_action_goal_edit);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.menu_action_goal_edit) {
            goalEdit();
        }
        return super.onOptionsItemSelected(menuItem);
    }

   public void initializeGetDataFromDbAndDisplay(){

       DBAdapter db = new DBAdapter(getActivity());
       db.open();



       long rowID = 1;
       String[] fields = new String[] {
               "_id",
               "user_mesurment"
       };
       Cursor c = db.selectPrimaryKey("users", "_id", rowID, fields);
       String mesurment;
       mesurment = c.getString(1);


       String[] fieldsGoal = new String[] {
               "_id",
               "goal_current_weight",
               "goal_target_weight",
               "goal_i_want_to",
               "goal_weekly_goal",
               "goal_activity_level",
               "goal_date",
       };
       Cursor goalCursor = db.select("goal", fieldsGoal, "", "", "_id", "DESC");


       // Ready as variables
       String goalID = goalCursor.getString(0);
       String goalCurrentWeight = goalCursor.getString(1);
       String goalTargetWeight = goalCursor.getString(2);
       String goalIWantTo = goalCursor.getString(3);
       String goalWeeklyGoal = goalCursor.getString(4);
       String goalActivityLevel = goalCursor.getString(5);
       String goalDate = goalCursor.getString(6);


       TextView textViewGoalCurrentWeightNumber = getActivity().findViewById(R.id.textViewGoalCurrentWeightNumber);
       if(mesurment.startsWith("m")) {

           textViewGoalCurrentWeightNumber.setText(goalCurrentWeight + " kg (" + goalDate + ")");
       }
       else{

           double currentWeightNumber = 0;

           try {
               currentWeightNumber = Double.parseDouble(goalCurrentWeight);
           }
           catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
           }
           // kg to pounds
           double currentWeightNumberPounds =  Math.round(currentWeightNumber / 0.45359237);


           textViewGoalCurrentWeightNumber.setText(currentWeightNumberPounds + " pounds (" + goalDate + ")");
       }

       //  Target
       TextView textViewGoalCurrentTargetNumber = getActivity().findViewById(R.id.textViewGoalCurrentTargetNumber);
       if(mesurment.startsWith("m")) {
           // Metric
           textViewGoalCurrentTargetNumber.setText(goalTargetWeight + " kg");
       }
       else{

           double targetWeightNumber = 0;

           try {
               targetWeightNumber = Double.parseDouble(goalTargetWeight);
           }
           catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
           }
           // kg to pounds
           double targetWeightNumberPounds =  Math.round(targetWeightNumber / 0.45359237);


           textViewGoalCurrentTargetNumber.setText(targetWeightNumberPounds + " pounds");
       }


       // Method
       TextView textViewGoalMethodText = getActivity().findViewById(R.id.textViewGoalMethodText);

       String method;
       if(goalIWantTo.equals("0")){
           method = "Lose "  + goalWeeklyGoal;
       }
       else{
           method = "Gain "  + goalWeeklyGoal;
       }
       if(mesurment.startsWith("m")) {
           method = method + " kg/week";
       }
       else{
           method = method + " pounds/week";
       }
       textViewGoalMethodText.setText(method);



       TextView textViewActivityLevel = getActivity().findViewById(R.id.textViewActivityLevel);
       if(goalActivityLevel.equals("0")){
           textViewActivityLevel.setText("Little to no exercise");
       }
       else if(goalActivityLevel.equals("1")){
           textViewActivityLevel.setText("Light exercise (1–3 days per week)");
       }
       else if(goalActivityLevel.equals("2")){
           textViewActivityLevel.setText("Moderate exercise (3–5 days per week)");
       }
       else if(goalActivityLevel.equals("3")){
           textViewActivityLevel.setText("Heavy exercise (6–7 days per week)");
       }
       else if(goalActivityLevel.equals("4")){
           textViewActivityLevel.setText("Very heavy exercise (twice per day, extra heavy workouts)");
       }

       updateNumberTable();

       toggleNumbersViewGoal(false);

       CheckBox checkBoxAdvanced = getActivity().findViewById(R.id.checkBoxGoalToggle);
       checkBoxAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               toggleNumbersViewGoal(isChecked);
           }
       });
       db.close();
   }

   public void toggleNumbersViewGoal(boolean isChecked){

       TableRow textViewGoalMethodRowA = getActivity().findViewById(R.id.textViewGoalMethodRowA);

       TableRow textViewGoalMethodRowB =getActivity().findViewById(R.id.textViewGoalMethodRowB);


       TextView textViewGoalHeadcellEnergy = getActivity().findViewById(R.id.textViewGoalHeadcellEnergy );
       TextView textViewGoalHeadcellProteins = getActivity().findViewById(R.id.textViewGoalHeadcellProteins );
       TextView textViewGoalHeadcellCarbs = getActivity().findViewById(R.id.textViewGoalHeadcellCarbs );
       TextView textViewGoalHeadcellFat = getActivity().findViewById(R.id.textViewGoalHeadcellFat );

       TextView textViewGoalProteinsBMR = getActivity().findViewById(R.id.textViewGoalProteinsBMR );
       TextView textViewGoalCarbsBMR = getActivity().findViewById(R.id.textViewGoalCarbsBMR);
       TextView textViewGoalFatBMR = getActivity().findViewById(R.id.textViewGoalFatBMR);

       TextView textViewGoalProteinsDiet = getActivity().findViewById(R.id.textViewGoalProteinsDiet);
       TextView textViewGoalCarbsDiet = getActivity().findViewById(R.id.textViewGoalCarbsDiet);
       TextView textViewGoalFatDiet = getActivity().findViewById(R.id.textViewGoalFatDiet);

       TextView textViewGoalProteinsWithActivity = getActivity().findViewById(R.id.textViewGoalProteinsWithActivity);
       TextView textViewGoalCarbsWithActivity = getActivity().findViewById(R.id.textViewGoalCarbsWithActivity);
       TextView textViewGoalFatWithActivity = getActivity().findViewById(R.id.textViewGoalFatWithActivity);

       TextView textViewGoalProteinsWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalProteinsWithActivityAndDiet);
       TextView textViewGoalCarbsWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalCarbsWithActivityAndDiet);
       TextView textViewGoalFatWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalFatWithActivityAndDiet);

       if(!isChecked){
           textViewGoalMethodRowA.setVisibility(View.GONE);
           textViewGoalMethodRowB.setVisibility(View.GONE);
           textViewGoalHeadcellEnergy.setVisibility(View.GONE);
           textViewGoalHeadcellProteins.setVisibility(View.GONE);
           textViewGoalHeadcellCarbs.setVisibility(View.GONE);
           textViewGoalHeadcellFat.setVisibility(View.GONE);
           textViewGoalProteinsBMR.setVisibility(View.GONE);
           textViewGoalCarbsBMR.setVisibility(View.GONE);
           textViewGoalFatBMR.setVisibility(View.GONE);
           textViewGoalProteinsDiet.setVisibility(View.GONE);
           textViewGoalCarbsDiet.setVisibility(View.GONE);
           textViewGoalFatDiet.setVisibility(View.GONE);
           textViewGoalProteinsWithActivity.setVisibility(View.GONE);
           textViewGoalCarbsWithActivity.setVisibility(View.GONE);
           textViewGoalFatWithActivity.setVisibility(View.GONE);
           textViewGoalProteinsWithActivityAndDiet.setVisibility(View.GONE);
           textViewGoalCarbsWithActivityAndDiet.setVisibility(View.GONE);
           textViewGoalFatWithActivityAndDiet.setVisibility(View.GONE);
       }
       else {
           textViewGoalMethodRowA.setVisibility(View.VISIBLE);
           textViewGoalMethodRowB.setVisibility(View.VISIBLE);
           textViewGoalHeadcellEnergy.setVisibility(View.VISIBLE);
           textViewGoalHeadcellProteins.setVisibility(View.VISIBLE);
           textViewGoalHeadcellCarbs.setVisibility(View.VISIBLE);
           textViewGoalHeadcellFat.setVisibility(View.VISIBLE);
           textViewGoalProteinsBMR.setVisibility(View.VISIBLE);
           textViewGoalCarbsBMR.setVisibility(View.VISIBLE);
           textViewGoalFatBMR.setVisibility(View.VISIBLE);
           textViewGoalProteinsDiet.setVisibility(View.VISIBLE);
           textViewGoalCarbsDiet.setVisibility(View.VISIBLE);
           textViewGoalFatDiet.setVisibility(View.VISIBLE);
           textViewGoalProteinsWithActivity.setVisibility(View.VISIBLE);
           textViewGoalCarbsWithActivity.setVisibility(View.VISIBLE);
           textViewGoalFatWithActivity.setVisibility(View.VISIBLE);
           textViewGoalProteinsWithActivityAndDiet.setVisibility(View.VISIBLE);
           textViewGoalCarbsWithActivityAndDiet.setVisibility(View.VISIBLE);
           textViewGoalFatWithActivityAndDiet.setVisibility(View.VISIBLE);

       }
   }

   public void goalEdit(){
       int id = R.layout.fragment_goal_edit;
       setMainView(id);


       DBAdapter db = new DBAdapter(getActivity());
       db.open();


       long rowID = 1;
       String[] fields= new String[] {
               "_id",
               "user_mesurment"
       };
       Cursor c = db.selectPrimaryKey("users", "_id", rowID, fields);
       String mesurment;
       mesurment = c.getString(1);

       String[] fieldsGoal = new String[] {
               "_id",
               "goal_current_weight",
               "goal_target_weight",
               "goal_i_want_to",
               "goal_weekly_goal",
               "goal_activity_level"
       };
       Cursor goalCursor = db.select("goal", fieldsGoal, "", "", "_id", "DESC");


       String goalID = goalCursor.getString(0);
       String goalCurrentWeight = goalCursor.getString(1);
       String goalTargetWeight = goalCursor.getString(2);
       String goalIWantTo = goalCursor.getString(3);
       String goalWeeklyGoal = goalCursor.getString(4);
       String goalActivityLevel = goalCursor.getString(5);


       EditText editTextGoalCurrentWeight =  getActivity().findViewById(R.id.editTextGoalCurrentWeight);
       if(mesurment.startsWith("m")) {

           editTextGoalCurrentWeight.setText(goalCurrentWeight);
       }
       else{

           double currentWeightNumber = 0;

           try {
               currentWeightNumber = Double.parseDouble(goalCurrentWeight);
           }
           catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
           }
           double currentWeightNumberPounds =  Math.round(currentWeightNumber / 0.45359237);


           editTextGoalCurrentWeight.setText(currentWeightNumberPounds+"");

           TextView textViewGoalCurrentWeightType = getActivity().findViewById(R.id.textViewGoalCurrentWeightType);
           textViewGoalCurrentWeightType.setText("pounds");
       }


       //  Target
       TextView editTextGoalTargetWeight = getActivity().findViewById(R.id.editTextGoalTargetWeight);
       if(mesurment.startsWith("m")) {

           editTextGoalTargetWeight.setText(goalTargetWeight);
       }
       else{

           double targetWeightNumber = 0;

           try {
               targetWeightNumber = Double.parseDouble(goalTargetWeight);
           }
           catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
           }

           double targetWeightNumberPounds =  Math.round(targetWeightNumber / 0.45359237);


           editTextGoalTargetWeight.setText(targetWeightNumberPounds + "");



           TextView textViewTargetWeightType = getActivity().findViewById(R.id.textViewTargetWeightType);
           textViewTargetWeightType.setText("pounds/week");
       }


       Spinner spinnerIWantTo = getActivity().findViewById(R.id.spinnerIWantTo);
       if(goalIWantTo.equals("0")){
           spinnerIWantTo.setSelection(0);
       }
       else{
           spinnerIWantTo.setSelection(1);
       }


       Spinner spinnerWeeklyGoal = getActivity().findViewById(R.id.spinnerWeeklyGoal);
       if(goalWeeklyGoal.equals("0.5")){
           spinnerWeeklyGoal.setSelection(0);
       }
       else if(goalWeeklyGoal.equals("1")){
           spinnerWeeklyGoal.setSelection(1);
       }
       else if(goalWeeklyGoal.equals("1.5")){
           spinnerWeeklyGoal.setSelection(2);
       }


       Spinner spinnerActivityLevel = getActivity().findViewById(R.id.spinnerActivityLevel);
       int intActivityLevel = 0;
       try{
           intActivityLevel = Integer.parseInt(goalActivityLevel);
       }
       catch (NumberFormatException e){

       }
       spinnerActivityLevel.setSelection(intActivityLevel);


       updateNumberTable();


       Button buttonGoalSubmit = getActivity().findViewById(R.id.buttonGoalSubmit);
       buttonGoalSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               editGoalSubmitOnClick();
           }
       });


       db.close();


   }

   public void editGoalSubmitOnClick(){


        int error = 0;


       DBAdapter db = new DBAdapter(getActivity());
       db.open();

       long rowID = 1;
       String[] fields = new String[] {
               "_id",
               "user_dob",
               "user_gender",
               "user_height",
               "user_mesurment"
       };
       Cursor c = db.selectPrimaryKey("users", "_id", rowID,fields);
       String stringUserDob = c.getString(1);
       String stringUserGender  = c.getString(2);
       String stringUserHeight = c.getString(3);
       String mesurment = c.getString(4);



       String[] items1 = stringUserDob.split("-");
       String stringYear = items1[0];
       String stringMonth = items1[1];
       String stringDay = items1[2];

       int intYear = 0;
       try {
           intYear = Integer.parseInt(stringYear);
       }
       catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }
       int intMonth = 0;
       try {
           intMonth = Integer.parseInt(stringMonth);
       }
       catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }
       int intDay = 0;
       try {
           intDay = Integer.parseInt(stringDay);
       }
       catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }
       String stringUserAge = getAge(intYear, intMonth, intDay);

       int intUserAge = 0;
       try {
           intUserAge = Integer.parseInt(stringUserAge);
       }
       catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }


       double doubleUserHeight = 0;

       try {
           doubleUserHeight = Double.parseDouble(stringUserHeight);
       }
       catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }


       EditText editTextGoalCurrentWeight = getActivity().findViewById(R.id.editTextGoalCurrentWeight);
       String stringCurrentWeight = editTextGoalCurrentWeight.getText().toString();
       double doubleCurrentWeight = 0;
       if(stringCurrentWeight.isEmpty()){
           Toast.makeText(getActivity(), "Please enter current weight", Toast.LENGTH_LONG).show();
           error = 1;
       }
       else{
           try {
               doubleCurrentWeight = Double.parseDouble(stringCurrentWeight);
           }
           catch(NumberFormatException nfe) {
               Toast.makeText(getActivity(), "Current weight has to be a number.", Toast.LENGTH_LONG).show();
               error = 1;
           }
       }
       String stringCurrentWeightSQL = db.quoteSmart(stringCurrentWeight);

       // Target weight
       EditText editTextGoalTargetWeight =  getActivity().findViewById(R.id.editTextGoalTargetWeight);
       String stringTargetWeight = editTextGoalTargetWeight.getText().toString();
       double doubleTargetWeight = 0;
       if(stringTargetWeight.isEmpty()){
           Toast.makeText(getActivity(), "Please enter target weight", Toast.LENGTH_LONG).show();
           error = 1;
       }
       else{
           try {
               doubleTargetWeight = Double.parseDouble(stringTargetWeight);
           }
           catch(NumberFormatException nfe) {
               Toast.makeText(getActivity(), "Target weight has to be a number.", Toast.LENGTH_LONG).show();
               error = 1;
           }
       }
       String stringTargetWeightSQL = db.quoteSmart(stringTargetWeight);


       Spinner spinnerIWantTo = getActivity().findViewById(R.id.spinnerIWantTo);
       int intIWantTo = spinnerIWantTo.getSelectedItemPosition();
       String stringIWantTo = "" + intIWantTo;
       String stringIWantToSQL = db.quoteSmart(stringIWantTo);


       Spinner spinnerWeeklyGoal = getActivity().findViewById(R.id.spinnerWeeklyGoal);
       String stringWeeklyGoal = spinnerWeeklyGoal.getSelectedItem().toString();
       String stringWeeklyGoalSQL = db.quoteSmart(stringWeeklyGoal);


       Spinner spinnerActivityLevel = getActivity().findViewById(R.id.spinnerActivityLevel);

       int intActivityLevel = spinnerActivityLevel.getSelectedItemPosition();
       String stringActivityLevel = ""+intActivityLevel;
       String stringActivityLevelSQL = db.quoteSmart(stringActivityLevel);



       if(error == 0) {

           int year = Calendar.getInstance().get(Calendar.YEAR);
           int month = Calendar.getInstance().get(Calendar.MONTH);
           ++month;
           int date = Calendar.getInstance().get(Calendar.DATE);

           String goalDate = year + "-" + month + "-" + date;
           String goalDateSQL = db.quoteSmart(goalDate);



           /* 1. BMR: Energy */
           double goalEnergyBMR = 0;

           if(stringUserGender.startsWith("m")){

               goalEnergyBMR = 88.362 + (13.397*doubleCurrentWeight) + (4.799*doubleUserHeight)- (5.677*intUserAge);

           }
           else{

               goalEnergyBMR = 447.593+(9.247*doubleCurrentWeight)+(3.098*doubleUserHeight)-(4.330*intUserAge);

           }
           goalEnergyBMR = Math.round(goalEnergyBMR);
           String goalEnergyBMRSQL = db.quoteSmart(""+goalEnergyBMR);


           double proteinsBMR = Math.round(goalEnergyBMR*0.3);
           double carbsBMR = Math.round(goalEnergyBMR*0.5);
           double fatBMR = Math.round(goalEnergyBMR*0.2);

           double proteinsBMRSQL = db.quoteSmart(proteinsBMR);
           double carbsBMRSQL = db.quoteSmart(carbsBMR);
           double fatBMRSQL = db.quoteSmart(fatBMR);



           double doubleWeeklyGoal = 0;
           try {
               doubleWeeklyGoal = Double.parseDouble(stringWeeklyGoal);
           }
           catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
           }


           double energyDiet = 0;
           double KcalDividedBy7 = 1100*doubleWeeklyGoal;
           if(intIWantTo == 0){
               // Loose weight
               energyDiet = Math.round((goalEnergyBMR - KcalDividedBy7) * 1.2);

           }
           else{
               // Gain weight
               energyDiet = Math.round((goalEnergyBMR + KcalDividedBy7) * 1.2);
           }


           double energyDietSQL = db.quoteSmart(energyDiet);
           double proteinsDiet = Math.round(energyDiet*0.3);
           double carbsDiet = Math.round(energyDiet*0.5);
           double fatDiet = Math.round(energyDiet*0.2);

           double proteinsDietSQL = db.quoteSmart(proteinsDiet);
           double carbsDietSQL = db.quoteSmart(carbsDiet);
           double fatDietSQL = db.quoteSmart(fatDiet);





           double energyWithActivity = 0;
           if(stringActivityLevel.equals("0")) {
               energyWithActivity = goalEnergyBMR * 1.2;
           }
           else if(stringActivityLevel.equals("1")) {
               energyWithActivity = goalEnergyBMR * 1.375;
           }
           else if(stringActivityLevel.equals("2")) {
               energyWithActivity = goalEnergyBMR*1.55;
           }
           else if(stringActivityLevel.equals("3")) {
               energyWithActivity = goalEnergyBMR*1.725;
           }
           else if(stringActivityLevel.equals("4")) {
               energyWithActivity = goalEnergyBMR * 1.9;
           }
           energyWithActivity = Math.round(energyWithActivity);
           double energyWithActivitySQL = db.quoteSmart(energyWithActivity);

           double proteinsWithActivity = Math.round(energyWithActivity*0.3);
           double carbsWithActivity = Math.round(energyWithActivity*0.5);
           double fatWithActivity = Math.round(energyWithActivity*0.2);

           double proteinsWithActivitySQL = db.quoteSmart(proteinsWithActivity);
           double carbsWithActivitySQL = db.quoteSmart(carbsWithActivity);
           double fatWithActivitySQL = db.quoteSmart(fatWithActivity);


           double energyWithActivityAndDiet;
           if(intIWantTo == 0){

               energyWithActivityAndDiet = goalEnergyBMR - KcalDividedBy7;
           }
           else{

               energyWithActivityAndDiet = goalEnergyBMR + KcalDividedBy7;
           }

           if(stringActivityLevel.equals("0")) {
               energyWithActivityAndDiet= energyWithActivityAndDiet* 1.2;
           }
           else if(stringActivityLevel.equals("1")) {
               energyWithActivityAndDiet= energyWithActivityAndDiet* 1.375;
           }
           else if(stringActivityLevel.equals("2")) {
               energyWithActivityAndDiet= energyWithActivityAndDiet*1.55;
           }
           else if(stringActivityLevel.equals("3")) {
               energyWithActivityAndDiet= energyWithActivityAndDiet*1.725;
           }
           else if(stringActivityLevel.equals("4")) {
               energyWithActivityAndDiet = energyWithActivityAndDiet* 1.9;
           }
           energyWithActivityAndDiet = Math.round(energyWithActivityAndDiet);


           double energyWithActivityAndDietSQL = db.quoteSmart(energyWithActivityAndDiet);

           double proteinsWithActivityAndDiet = Math.round(energyWithActivityAndDiet*0.3);
           double carbsWithActivityAndDiet = Math.round(energyWithActivityAndDiet*0.5);
           double fatWithActivityAndDiet = Math.round(energyWithActivityAndDiet*0.2);

           double proteinsWithActivityAndDietSQL = db.quoteSmart(proteinsWithActivityAndDiet);
           double carbsWithActivityAndDietSQL = db.quoteSmart(carbsWithActivityAndDiet);
           double fatWithActivityAndDietSQL = db.quoteSmart(fatWithActivityAndDiet);



           String inpFields = "'_id', " +
                   "'goal_current_weight', " +
                   "'goal_target_weight', " +
                   "'goal_i_want_to', " +
                   "'goal_weekly_goal', " +
                   "'goal_date'," +
                   "'goal_activity_level'," +
                   "'goal_energy_BMR'," +
                   "'goal_proteins_BMR'," +
                   "'goal_carbs_BMR'," +
                   "'goal_fat_BMR'," +
                   "'goal_energy_with_diet'," +
                   "'goal_proteins_with_diet'," +
                   "'goal_carbs_with_diet'," +
                   "'goal_fat_with_diet'," +
                   "'goal_energy_with_activity'," +
                   "'goal_proteins_with_activity'," +
                   "'goal_carbs_with_activity'," +
                   "'goal_fat_with_activity'," +
                   "'goal_energy_with_activity_and_diet'," +
                   "'goal_proteins_with_activity_and_diet'," +
                   "'goal_carbs_with_activity_and_diet'," +
                   "'goal_fat_with_activity_and_diet'";


           String inpValues = "NULL, " +
                   stringCurrentWeightSQL + ", " +
                   stringTargetWeightSQL + ", " +
                   stringIWantToSQL  + ", " +
                   stringWeeklyGoalSQL + ", " +
                   goalDateSQL  + ", " +
                   stringActivityLevelSQL + ", " +
                   goalEnergyBMRSQL + ", " +
                   proteinsBMRSQL + ", " +
                   carbsBMRSQL  + ", " +
                   fatBMRSQL + ", " +
                   energyDietSQL + ", " +
                   proteinsDietSQL + ", " +
                   carbsDietSQL  + ", " +
                   fatDietSQL + ", " +
                   energyWithActivitySQL + ", " +
                   proteinsWithActivitySQL  + ", " +
                   carbsWithActivitySQL   + ", " +
                   fatWithActivitySQL   + ", " +
                   energyWithActivityAndDietSQL + ", " +
                   proteinsWithActivityAndDietSQL + ", " +
                   carbsWithActivityAndDietSQL + ", " +
                   fatWithActivityAndDietSQL;


           db.insert("goal",inpFields,inpValues);
           updateNumberTable();

           Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

           FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
           fragmentManager.beginTransaction().replace(R.id.flContent, new GoalFragment(), GoalFragment.class.getName()).commit();

       }

   }

   public void updateNumberTable(){
       DBAdapter db = new DBAdapter(getActivity());
       db.open();

       String[] fieldsGoal = new String[] {
               "_id",
               "goal_energy_BMR",
               "goal_proteins_BMR",
               "goal_carbs_BMR",
               "goal_fat_BMR",
               "goal_energy_with_diet",
               "goal_proteins_with_diet",
               "goal_carbs_with_diet",
               "goal_fat_with_diet",
               "goal_energy_with_activity",
               "goal_proteins_with_activity",
               "goal_carbs_with_activity",
               "goal_fat_with_activity",
               "goal_energy_with_activity_and_diet",
               "goal_proteins_with_activity_and_diet",
               "goal_carbs_with_activity_and_diet",
               "goal_fat_with_activity_and_diet"
       };
       Cursor goalCursor = db.select("goal",fieldsGoal,"","","_id","DESC");


       // Ready as variables
       String goalEnergyBmr = goalCursor.getString(1);
       String goalProteinsBmr = goalCursor.getString(2);
       String goalCarbsBmr = goalCursor.getString(3);
       String goalFatBmr = goalCursor.getString(4);
       String goalEnergyDiet = goalCursor.getString(5);
       String goalProteinsDiet = goalCursor.getString(6);
       String goalCarbsDiet = goalCursor.getString(7);
       String goalFatDiet = goalCursor.getString(8);
       String goalEnergyWithActivity = goalCursor.getString(9);
       String goalProteinsWithActivity = goalCursor.getString(10);
       String goalCarbsWithActivity = goalCursor.getString(11);
       String goalFatWithActivity = goalCursor.getString(12);
       String goalEnergyWithActivityAndDiet = goalCursor.getString(13);
       String goalProteinsWithActivityAndDiet = goalCursor.getString(14);
       String goalCarbsWithActivityAndDiet = goalCursor.getString(15);
       String goalFatWithActivityAndDiet = goalCursor.getString(16);



       /* Numbers */

       // 1 Diet
       TextView textViewGoalEnergyDiet = getActivity().findViewById(R.id.textViewGoalEnergyDiet);
       textViewGoalEnergyDiet.setText(goalEnergyDiet);
       TextView textViewGoalProteinsDiet = getActivity().findViewById(R.id.textViewGoalProteinsDiet);
       textViewGoalProteinsDiet.setText(goalProteinsDiet);
       TextView textViewGoalCarbsDiet = getActivity().findViewById(R.id.textViewGoalCarbsDiet);
       textViewGoalCarbsDiet.setText(goalCarbsDiet);
       TextView textViewGoalFatDiet = getActivity().findViewById(R.id.textViewGoalFatDiet);
       textViewGoalFatDiet.setText(goalFatDiet);

       // 2 WithActivityAndDiet
       TextView textViewGoalEnergyWithActivityAndDiet =getActivity().findViewById(R.id.textViewGoalEnergyWithActivityAndDiet);
       textViewGoalEnergyWithActivityAndDiet.setText(goalEnergyWithActivityAndDiet);
       TextView textViewGoalProteinsWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalProteinsWithActivityAndDiet);
       textViewGoalProteinsWithActivityAndDiet.setText(goalProteinsWithActivityAndDiet);
       TextView textViewGoalCarbsWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalCarbsWithActivityAndDiet);
       textViewGoalCarbsWithActivityAndDiet.setText(goalCarbsWithActivityAndDiet);
       TextView textViewGoalFatWithActivityAndDiet = getActivity().findViewById(R.id.textViewGoalFatWithActivityAndDiet);
       textViewGoalFatWithActivityAndDiet.setText(goalFatWithActivityAndDiet);

       // 3 BMR
       TextView textViewGoalEnergyBMR = getActivity().findViewById(R.id.textViewGoalEnergyBMR);
       textViewGoalEnergyBMR.setText(goalEnergyBmr);
       TextView textViewGoalProteinsBMR = getActivity().findViewById(R.id.textViewGoalProteinsBMR);
       textViewGoalProteinsBMR.setText(goalProteinsBmr);
       TextView textViewGoalCarbsBMR = getActivity().findViewById(R.id.textViewGoalCarbsBMR);
       textViewGoalCarbsBMR.setText(goalCarbsBmr);
       TextView textViewGoalFatBMR = getActivity().findViewById(R.id.textViewGoalFatBMR);
       textViewGoalFatBMR.setText(goalFatBmr);


       // 4 WithActivity
       TextView textViewGoalEnergyWithActivity = getActivity().findViewById(R.id.textViewGoalEnergyWithActivity);
       textViewGoalEnergyWithActivity.setText(goalEnergyWithActivity);
       TextView textViewGoalProteinsWithActivity = getActivity().findViewById(R.id.textViewGoalProteinsWithActivity);
       textViewGoalProteinsWithActivity.setText(goalProteinsWithActivity);
       TextView textViewGoalCarbsWithActivity = getActivity().findViewById(R.id.textViewGoalCarbsWithActivity);
       textViewGoalCarbsWithActivity.setText(goalCarbsWithActivity);
       TextView textViewGoalFatWithActivity = getActivity().findViewById(R.id.textViewGoalFatWithActivity);
       textViewGoalFatWithActivity.setText(goalFatWithActivity);



       db.close();
   }

   private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        return "" + age;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        GoalFragment fragment = (GoalFragment) getFragmentManager().findFragmentById(R.id.flContent);

        getFragmentManager().beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();

        return true;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
