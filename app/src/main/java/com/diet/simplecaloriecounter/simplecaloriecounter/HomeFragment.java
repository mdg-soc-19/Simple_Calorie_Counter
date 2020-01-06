package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class HomeFragment extends Fragment implements IOnBackPressed{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private View mainView;
    private Cursor listCursor;


    // Action buttons on toolbar
    private MenuItem menuItemAddFood;


    // Holder for buttons on toolbar
    private String currentFoodId;
    private String currentFoodName;
    private String currentFdId;

    private String currentDateYear = "";
    private String currentDateMonth = "";
    private String currentDateDay = "";

    boolean lockPortionSizeByGram;
    boolean lockPortionSizeByPcs;


    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Home");


        initializeHome();

        setHasOptionsMenu(true);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        menuItemAddFood = menu.findItem(R.id.menu_action_add_food);

        menuItemAddFood.setVisible(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.menu_action_add_food) {
            addFoodToDiarySelectMealNumber();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initializeHome(){

        if(currentDateYear.equals("") || currentDateMonth.equals("") || currentDateDay.equals("")) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);

            currentDateYear = "" + year;


            ++month;
            if(month < 10){
                currentDateMonth = "0" + month;
            }
            else{
                currentDateMonth = "" + month;
            }

            if(day < 10){
                currentDateDay = "0" + day;
            }
            else{
                currentDateDay = "" + day;
            }
        }
        String stringFdDate = currentDateYear + "-" + currentDateMonth + "-" + currentDateDay;



        updateTableItems(stringFdDate, "0");
        updateTableItems(stringFdDate, "1");
        updateTableItems(stringFdDate, "2");
        updateTableItems(stringFdDate, "3");
        updateTableItems(stringFdDate, "4");
        updateTableItems(stringFdDate, "5");
        updateTableItems(stringFdDate, "6");

        calcualteNumberOfCalEatenToday(stringFdDate);


        ImageView imageViewAddBreakfast = getActivity().findViewById(R.id.imageViewAddBreakfast);
        imageViewAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(0);
            }
        });

        ImageView imageViewAddLunch = getActivity().findViewById(R.id.imageViewAddLunch);
        imageViewAddLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(1); // 1 == Lunch
            }
        });

        ImageView imageViewAddBeforeTraining = getActivity().findViewById(R.id.imageViewAddBeforeTraining);
        imageViewAddBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(2); // 2 == Before training
            }
        });
        ImageView imageViewAddAfterTraining = getActivity().findViewById(R.id.imageViewAddAfterTraining);
        imageViewAddAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(3); // 3 == After training
            }
        });
        ImageView imageViewAddDinner = getActivity().findViewById(R.id.imageViewAddDinner);
        imageViewAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(4); // 4 == Dinner
            }
        });
        ImageView imageViewAddSnacks = getActivity().findViewById(R.id.imageViewAddSnacks);
        imageViewAddSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(5); // 5 = Snacks
            }
        });

        ImageView imageViewAddSupper = getActivity().findViewById(R.id.imageViewAddSupper);
        imageViewAddSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(6); // 6 = Supper
            }
        });
    }

    public void calcualteNumberOfCalEatenToday(String stringDate){

        /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Date SQL
        String stringDateSQL = db.quoteSmart(stringDate);

        // Food diary sum
        String[] fieldsFoodDiarySum = new String[] {
                "_id",
                "food_diary_sum_date",
                "food_diary_sum_energy",
                "food_diary_sum_proteins",
                "food_diary_sum_carbs",
                "food_diary_sum_fat"
        };
        Cursor cursorFoodDiarySum = db.select("food_diary_sum", fieldsFoodDiarySum, "food_diary_sum_date", stringDateSQL);
        int cursorFoodDiarySumCount = cursorFoodDiarySum.getCount();




        // Select for food_diary_cal_eaten
        String[] fieldsFdce = new String[]{
                "_id",
                "fdce_id",
                "fdce_date",
                "fdce_meal_no",
                "fdce_eaten_energy",
                "fdce_eaten_proteins",
                "fdce_eaten_carbs",
                "fdce_eaten_fat"
        };
        Cursor cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, "fdce_date", stringDateSQL);
        int cursorFdceCount = cursorFdce.getCount();

        // Ready variables
        int intFdcetEatenEnergy = 0;
        int intFdcetEatenProteins = 0;
        int intFdcetEatenCarbs = 0;
        int intFdcetEatenFat = 0;

        String stringGetFdceMealNo = "";
        String stringGetFdceEatenEnergy = "";
        String stringGetFdceEatenProteins = "";
        String stringGetFdceEatenCarbs = "";
        String stringGetFdceEatenFat = "";
        int intFdceEatenEnergy = 0;
        int intFdceEatenProteins = 0;
        int intFdceEatenCarbs = 0;
        int intFdceEatenFat = 0;

        for (int x = 0; x < cursorFdceCount; x++) {
            // Get variables from cursor
            stringGetFdceMealNo = cursorFdce.getString(3);
            stringGetFdceEatenEnergy = cursorFdce.getString(4);
            stringGetFdceEatenProteins = cursorFdce.getString(5);
            stringGetFdceEatenCarbs = cursorFdce.getString(6);
            stringGetFdceEatenFat = cursorFdce.getString(7);


            try {
                intFdceEatenEnergy = Integer.parseInt(stringGetFdceEatenEnergy);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            try {
                intFdceEatenProteins = Integer.parseInt(stringGetFdceEatenProteins);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            try {
                intFdceEatenCarbs = Integer.parseInt(stringGetFdceEatenCarbs);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            try {
                intFdceEatenFat = Integer.parseInt(stringGetFdceEatenFat);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }


            intFdcetEatenEnergy = intFdcetEatenEnergy + intFdceEatenEnergy;
            intFdcetEatenProteins = intFdcetEatenProteins + intFdceEatenProteins;
            intFdcetEatenCarbs = intFdcetEatenCarbs + intFdceEatenCarbs;
            intFdcetEatenFat = intFdcetEatenFat + intFdceEatenFat;

            // Move to next
            cursorFdce.moveToNext();
        }


        if (cursorFoodDiarySumCount == 0) {
            // Insert database
            String insFields = "_id, food_diary_sum_date, food_diary_sum_energy, food_diary_sum_proteins, food_diary_sum_carbs, food_diary_sum_fat";
            String insValues = "NULL, " + stringDateSQL + ", '" + intFdcetEatenEnergy + "', '" +
                    intFdcetEatenProteins + "', '" + intFdcetEatenCarbs + "', '" + intFdcetEatenFat + "'";
            db.insert("food_diary_sum", insFields, insValues);
        } else {
            // Update
            String[] updateFields = new String[]{
                    "food_diary_sum_energy", "food_diary_sum_proteins", "food_diary_sum_carbs", "food_diary_sum_fat"
            };
            String[] updateValues = new String[]{
                    "'" + intFdcetEatenEnergy + "'",
                    "'" + intFdcetEatenProteins + "'",
                    "'" + intFdcetEatenCarbs + "'",
                    "'" + intFdcetEatenFat + "'"
            };

            long longFoodDiaryId = Long.parseLong(cursorFoodDiarySum.getString(0));

            db.update("food_diary_sum", "_id", longFoodDiaryId, updateFields, updateValues);
        }


        // Get goal
        String[] fieldsGoal = new String[]{
                "_id",
                "goal_energy_with_activity_and_diet"
        };
        Cursor cursorGoal = db.select("goal", fieldsGoal);
        cursorGoal.moveToLast();
        String stringGoalEnergyWithActivityAndDiet = cursorGoal.getString(1);

        // TextView goal
        TextView textViewBodyGoalWithActivity =  getActivity().findViewById(R.id.textViewBodyGoalWithActivity);
        textViewBodyGoalWithActivity.setText(stringGoalEnergyWithActivityAndDiet);

        // TextView Food
        TextView textViewBodyFood = getActivity().findViewById(R.id.textViewBodyFood);

        textViewBodyFood.setText("" + intFdcetEatenEnergy);

        // TextView Sum
        int intGoalEnergyWithActivityAndDiet = 0;
        try {
            intGoalEnergyWithActivityAndDiet = Integer.parseInt(stringGoalEnergyWithActivityAndDiet);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        int textViewBodyResult = intGoalEnergyWithActivityAndDiet - intFdcetEatenEnergy;

        TextView textViewBodyRemaining = (TextView) getActivity().findViewById(R.id.textViewBodyRemaining);

        textViewBodyRemaining.setText("" + textViewBodyResult);



        db.close();
    }

    public void updateTableItems(String stringDate, String stringMealNumber){
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Meal number SQL
        String stringMealNumberSQL = db.quoteSmart(stringMealNumber);
        String stringDateSQL = db.quoteSmart(stringDate);

        // Select
        String[] fdFields = new String[] {
                "_id",
                "fd_food_id",
                "fd_serving_size_gram",
                "fd_serving_size_gram_measurement",
                "fd_serving_size_pcs",
                "fd_serving_size_pcs_measurement",
                "fd_energy_calculated",
                "fd_proteins_calculated",
                "fd_carbohydrates_calculated",
                "fd_fats_calculated"
        };
        String[] fdWhereClause = new String[]{
                "fd_date",
                "fd_meal_number"
        };
        String[] fdWhereCondition = new String[]{
                stringDateSQL,
                stringMealNumberSQL
        };
        String[] fdWhereAndOr = new String[]{
                "AND"
        };
        Cursor cursorFd = db.select("food_diary", fdFields, fdWhereClause, fdWhereCondition, fdWhereAndOr);

        // Select for food name
        String[] fieldsFood = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name"
        };
        Cursor cursorFood;

        // Select for food_diary_cal_eaten
        Cursor cursorFdce;
        String[] fieldsFdce= new String[] {
                "_id",
                "fdce_id",
                "fdce_date",
                "fdce_meal_no",
                "fdce_eaten_energy",
                "fdce_eaten_proteins",
                "fdce_eaten_carbs",
                "fdce_eaten_fat"
        };
        String[] whereClause = new String[]{
                "fdce_date",
                "fdce_meal_no"
        };
        String[] whereCondition = new String[]{
                stringDateSQL,
                stringMealNumberSQL
        };
        String[] whereAndOr = new String[]{
                "AND"
        };

        cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, whereClause, whereCondition, whereAndOr);
        int cursorFdceCount = cursorFdce.getCount();

        int errorFdce = 0;
        if(cursorFdceCount == 0){
            String insFields = "_id, fdce_date, fdce_meal_no, fdce_eaten_energy, fdce_eaten_proteins, fdce_eaten_carbs, fdce_eaten_fat";
            String insValues = "NULL, " + stringDateSQL + ", " + stringMealNumberSQL + ", '0', '0', '0', '0'";
            db.insert("food_diary_cal_eaten", insFields, insValues);

            cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, whereClause, whereCondition, whereAndOr);
        }
        String stringFdceId = cursorFdce.getString(0);
        long longstringFdceId = Long.parseLong(stringFdceId);


        int intFdceEatenEnergy = 0;
        int intFdceEatenProteins = 0;
        int intFdceEatenCarbs = 0;
        int intFdceEatenFat = 0;


        int intCursorFdCount = cursorFd.getCount();
        for(int x=0;x<intCursorFdCount;x++){
            String stringFdId = cursorFd.getString(0);

            String fdFoodId = cursorFd.getString(1);
            String fdFoodIdSQL = db.quoteSmart(fdFoodId);

            String fdServingSizeGram = cursorFd.getString(2);
            String fdServingSizeGramMesurment = cursorFd.getString(3);
            String fdServingSizePcs = cursorFd.getString(4);
            String fdServingSizePcsMesurment = cursorFd.getString(5);
            String fdEnergyCalculated = cursorFd.getString(6);
            String fdProteinsCalculated = cursorFd.getString(7);
            String fdCarbsCalculated = cursorFd.getString(8);
            String fdFatCalculated = cursorFd.getString(9);
            int intFdEnergyCalculated = Integer.parseInt(fdEnergyCalculated);
            int intFdProteinsCalculated = Integer.parseInt(fdProteinsCalculated);
            int intFdCarbsCalculated = Integer.parseInt(fdCarbsCalculated);
            int intFdFatCalculated = Integer.parseInt(fdFatCalculated);

            // Get food name
            cursorFood = db.select("food", fieldsFood, "_id", fdFoodIdSQL);

            // Variables from food
            String foodID = cursorFood.getString(0);
            String foodName = cursorFood.getString(1);
            String foodManufactorName  = cursorFood.getString(2);

            String subLine = "(" + fdServingSizeGram + " " +
                    fdServingSizeGramMesurment + " = " +
                    fdServingSizePcs + " " +
                    fdServingSizePcsMesurment + ")";



            TableLayout tl;
            if(stringMealNumber.equals("0")) {
                tl = getActivity().findViewById(R.id.tableLayoutBreakfastItems);
            }
            else if(stringMealNumber.equals("1")) {
                tl = getActivity().findViewById(R.id.tableLayoutLunchItems);
            }
            else if(stringMealNumber.equals("2")) {
                tl = getActivity().findViewById(R.id.tableLayoutBeforeTrainingItems);
            }
            else if(stringMealNumber.equals("3")) {
                tl = getActivity().findViewById(R.id.tableLayoutAfterTrainingItems);
            }
            else if(stringMealNumber.equals("4")) {
                tl = getActivity().findViewById(R.id.tableLayoutDinnerItems);
            }
            else if(stringMealNumber.equals("5")) {
                tl = getActivity().findViewById(R.id.tableLayoutSnacksItems);
            }
            else {
                tl = getActivity().findViewById(R.id.tableLayoutSupperItems);
            }
            TableRow tr1 = new TableRow(getActivity());
            tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TableRow tr2 = new TableRow(getActivity());
            tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


            TextView textViewName = new TextView(getActivity());
            textViewName.setText(foodName);
            tr1.addView(textViewName);


            TextView textViewEnergy = new TextView(getActivity());
            textViewEnergy.setText(fdEnergyCalculated);
            tr1.addView(textViewEnergy);


            TextView textViewSubLine = new TextView(getActivity());
            textViewSubLine.setText(subLine);
            tr2.addView(textViewSubLine);


            tl.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)); /* Add row to TableLayout. */
            tl.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)); /* Add row to TableLayout. */


            tr1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Context context = getContext();
                    TableRow row = (TableRow)v;
                    TextView tv = (TextView)row.getChildAt(0);
                    String tvText ="" + tv.getText();
                    rowOnClickEditDeleteFdLine(tvText);
                }
            });

            // Add Listener
            tr2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Context context = getContext();
                    TableRow row = (TableRow)v;
                    TextView tv = (TextView)row.getChildAt(0);
                    String tvText ="" + tv.getText();
                    rowOnClickEditDeleteFdLine(tvText);
                }
            });

            // Sum fields
            intFdceEatenEnergy = intFdceEatenEnergy+intFdEnergyCalculated;
            intFdceEatenProteins = intFdceEatenProteins+intFdProteinsCalculated;
            intFdceEatenCarbs = intFdceEatenCarbs+intFdCarbsCalculated;
            intFdceEatenFat = intFdceEatenFat+intFdFatCalculated;


            cursorFd.moveToNext();
        }



        if(stringMealNumber.equals("0")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergyBreakfast);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else if(stringMealNumber.equals("1")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergyLunch);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else if(stringMealNumber.equals("2")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergyBeforeTraining);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else if(stringMealNumber.equals("3")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergyAfterTraining);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else if(stringMealNumber.equals("4")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergyDinner);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else if(stringMealNumber.equals("5")) {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergySnacks);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }
        else {
            TextView textViewEnergyX = getActivity().findViewById(R.id.textViewEnergySupper);
            textViewEnergyX.setText(""+ intFdceEatenEnergy);
        }

        String[] updateFields = new String[] {
                "fdce_eaten_energy",
                "fdce_eaten_proteins",
                "fdce_eaten_carbs",
                "fdce_eaten_fat"
        };
        String[] updateValues = new String[] {
                "'" + intFdceEatenEnergy + "'",
                "'" + intFdceEatenProteins + "'",
                "'" + intFdceEatenCarbs + "'",
                "'" + intFdceEatenFat + "'"
        };

        db.update("food_diary_cal_eaten", "_id", longstringFdceId, updateFields, updateValues);

        db.close();

    }

    private void rowOnClickEditDeleteFdLine(String stringTableRowTextName){
        /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* Change layout */
        int newViewID = R.layout.fragment_home_edit_or_delete;
        setMainView(newViewID);

        /* Find information */
        // Select
        String fields[] = new String[] {
                "_id",
                "fd_food_id",
                "fd_serving_size_gram",
                "fd_serving_size_gram_measurement",
                "fd_serving_size_pcs",
                "fd_serving_size_pcs_measurement",
                "fd_energy_calculated",
                "fd_proteins_calculated",
                "fd_carbohydrates_calculated",
                "fd_fats_calculated"
        };
        String stringFdDate = currentDateYear + "-" + currentDateMonth + "-" + currentDateDay;
        String stringDateSQL = db.quoteSmart(stringFdDate);
        Cursor cursorFd = db.select("food_diary", fields, "fd_date", stringDateSQL);
        String stringFdId = cursorFd.getString(0);

        // Select for food name
        String fieldsFood[] = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name"
        };
        Cursor cursorFood;

        // Ready variables
        String stringFdFoodId = "";
        String stringFdFoodIdSQL = "";

        String stringFdServingSizeGram = "";
        String stringFdServingSizeGramMesurment = "";
        String stringFdServingSizePcs = "";
        String stringFdServingSizePcsMesurment = "";
        String stringFdEnergyCalculated = "";

        String stringFoodID = "";
        String stringFoodName = "";
        String stringFoodManufactorName  = "";

        // Loop trough cursor, find the corresponding line that has been clicked
        int intCursorFdCount = cursorFd.getCount();
        for(int x=0;x<intCursorFdCount;x++) {


            // Variables from food diary
            stringFdFoodId = cursorFd.getString(1);
            stringFdFoodIdSQL = db.quoteSmart(stringFdFoodId);
            stringFdServingSizeGram = cursorFd.getString(2);
            stringFdServingSizeGramMesurment = cursorFd.getString(3);
            stringFdServingSizePcs = cursorFd.getString(4);
            stringFdServingSizePcsMesurment = cursorFd.getString(5);
            stringFdEnergyCalculated = cursorFd.getString(6);

            // Get food name
            cursorFood = db.select("food", fieldsFood, "_id", stringFdFoodIdSQL);

            // Variables from food
            stringFoodID = cursorFood.getString(0);
            stringFoodName = cursorFood.getString(1);
            stringFoodManufactorName  = cursorFood.getString(2);

            String subLine = stringFoodManufactorName + ", " +
                    stringFdServingSizeGram + " " +
                    stringFdServingSizeGramMesurment + ", " +
                    stringFdServingSizePcs + " " +
                    stringFdServingSizePcsMesurment;


            if(stringTableRowTextName.equals(stringFoodName)){
                break;
            }
            else if(stringTableRowTextName.equals(subLine)){
                break;
            }


            cursorFd.moveToNext();
        }

        // Show fields
        if(stringFoodName.equals("")) {
            Toast.makeText(getActivity(), "Error: Could not load food name.", Toast.LENGTH_LONG).show();
        }
        else{
            // Add to current
            currentFoodName = stringFoodName;
            currentFoodId   = stringFoodID;
            currentFdId     = stringFdId;

            TextView textViewViewFoodName = getActivity().findViewById(R.id.textViewViewFoodName);
            textViewViewFoodName.setText(stringFoodName);

            TextView textViewViewFoodManufactorName = getActivity().findViewById(R.id.textViewViewFoodManufactorName);
            textViewViewFoodManufactorName.setText(stringFoodManufactorName);


            EditText editTextServingSizePcs = getActivity().findViewById(R.id.editTextServingSizePcs);
            editTextServingSizePcs.setText(stringFdServingSizePcs);

            TextView textViewServingSizePcsMesurment = getActivity().findViewById(R.id.textViewServingSizePcsMesurment);
            textViewServingSizePcsMesurment.setText(stringFdServingSizePcsMesurment);

            EditText editTextServingSizeGram = getActivity().findViewById(R.id.editTextServingSizeGram);
            editTextServingSizeGram.setText(stringFdServingSizeGram);

            TextView textViewServingSizeGramMesurment = getActivity().findViewById(R.id.textViewServingSizeGramMesurment);
            textViewServingSizeGramMesurment.setText(stringFdServingSizeGramMesurment);

            /* Listener for editTextPortionSizePcs */
            editTextServingSizePcs.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if(!(s.toString().equals(""))){
                        editTextPortionSizePcsOnChange();
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
            editTextServingSizePcs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                    }else {
                        String lock = "portionSizePcs";
                        releaseLock(lock);
                    }
                }
            });

            editTextServingSizeGram.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if(!(s.toString().equals(""))){
                        // My code here
                        editTextPortionSizeGramOnChange();
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
            editTextServingSizeGram.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                    }else {
                        String lock = "portionSizeGram";
                        releaseLock(lock);
                    }
                }
            });


            // Watcher
            Button buttonSubmitEdit = getActivity().findViewById(R.id.buttonSubmitEdit);
            buttonSubmitEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClickEditFdLineSubmit();
                }
            });
            Button buttonSubmitDelete = getActivity().findViewById(R.id.buttonSubmitDelete);
            buttonSubmitDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClickDeleteFdLineSubmit();
                }
            });
        }
        /* Close db */
        db.close();
    }

    private void addFood(int mealNumber){

        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = AddFoodToDiaryFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("mealNumber", ""+mealNumber);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    private void addFoodToDiarySelectMealNumber() {
        /* Change layout */
        int newViewID = R.layout.fragment_home_select_meal_number;
        setMainView(newViewID);

        TextView textViewBreakfast = (TextView)getActivity().findViewById(R.id.textViewBreakfast);
        textViewBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(0);
            }
        });

        TextView textViewLunch = (TextView)getActivity().findViewById(R.id.textViewLunch);
        textViewLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(1);
            }
        });

        TextView textViewBeforeTraining = (TextView)getActivity().findViewById(R.id.textViewBeforeTraining);
        textViewBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(2);
            }
        });

        TextView textViewAfterTraining = (TextView)getActivity().findViewById(R.id.textViewAfterTraining);
        textViewAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(3);
            }
        });

        TextView textViewDinner = (TextView)getActivity().findViewById(R.id.textViewDinner);
        textViewDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(4);
            }
        });

        TextView textViewSnacks = (TextView)getActivity().findViewById(R.id.textViewSnacks);
        textViewSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(5);
            }
        });

        TextView textViewSupper = (TextView)getActivity().findViewById(R.id.textViewSupper);
        textViewSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(6);
            }
        });



    }

    public void editTextPortionSizePcsOnChange(){
        if(!(lockPortionSizeByGram)) {
            // Lock
            lockPortionSizeByPcs = true;

            // Get value of pcs
            EditText editTextPortionSizePcs =  getActivity().findViewById(R.id.editTextServingSizePcs);
            String stringPortionSizePcs = editTextPortionSizePcs.getText().toString();

            double doublePortionSizePcs = 0;

            if (stringPortionSizePcs.equals("")) {
                doublePortionSizePcs = 0;
            } else {
                try {
                    doublePortionSizePcs = Double.parseDouble(stringPortionSizePcs);
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }


            // Database
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[]{
                    "food_serving_size"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            // Convert cursor to strings
            String stringServingSize = foodCursor.getString(0);
            db.close();


            // Convert cursor to double
            double doubleServingSize = 0;
            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // Calculate how much n portionsize is in gram
            // We are changing pcs
            // Update gram
            double doublePortionSizeGram = Math.round(doublePortionSizePcs * doubleServingSize);

            // Update portion size gram
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextServingSizeGram);
            editTextPortionSizeGram.setText("" + doublePortionSizeGram);
        }
    }

    public void editTextPortionSizeGramOnChange(){
        if(!(lockPortionSizeByPcs)) {

            // Lock
            lockPortionSizeByGram = true;

            // Get value of gram
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextServingSizeGram);
            String stringPortionSizeGram = editTextPortionSizeGram.getText().toString();
            double doublePortionSizeGram = 0;
            try {
                doublePortionSizeGram = Double.parseDouble(stringPortionSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // Database
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[]{
                    "food_serving_size",
                    "food_serving_name_number"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            // Convert cursor to strings
            String stringServingSizeGram = foodCursor.getString(0);
            String stringServingNameNumber = foodCursor.getString(1);

            db.close();


            // Convert cursor to double
            double doubleServingSizeGram = 0;
            try {
                doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            double doubleServingNumber = 0;
            try {
                doubleServingNumber = Double.parseDouble(stringServingNameNumber);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }



            double doublePortionSizePcs = Math.round(doublePortionSizeGram / (doubleServingSizeGram/doubleServingNumber));


            EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextServingSizePcs);
            editTextPortionSizePcs.setText("" + doublePortionSizePcs);
        }

    }

    public void OnClickEditFdLineSubmit(){

        int error = 0;

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        long longFdID = 0;
        try{
            longFdID = Long.parseLong(currentFdId);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        String[] fields = new String[] {
                "food_serving_size",
                "food_energy",
                "food_proteins",
                "food_carbohydrates",
                "food_fats"
        };
        String currentIdSQL = db.quoteSmart(currentFoodId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Convert cursor to strings
        String stringGetFromSQLFoodServingSizeGram = foodCursor.getString(0);
        double doubleGetFromSQLFoodServingSizeGram = Double.parseDouble(stringGetFromSQLFoodServingSizeGram);

        String stringGetFromSQLFoodEnergy = foodCursor.getString(1);
        double doubleGetFromSQLFoodEnergy = Double.parseDouble(stringGetFromSQLFoodEnergy);

        String stringGetFromSQLFoodProteins = foodCursor.getString(2);
        double doubleGetFromSQLFoodProteins = Double.parseDouble(stringGetFromSQLFoodProteins);

        String stringGetFromSQLFoodCarbohydrates = foodCursor.getString(3);
        double doubleGetFromSQLFoodCarbohydrates = Double.parseDouble(stringGetFromSQLFoodCarbohydrates);

        String stringGetFromSQLFoodFat = foodCursor.getString(4);
        double doubleGetFromSQLFoodFat = Double.parseDouble(stringGetFromSQLFoodFat);


        // Update fd serving size gram
        EditText editTextServingSizeGram = getActivity().findViewById(R.id.editTextServingSizeGram);
        String stringFdServingSizeGram = editTextServingSizeGram.getText().toString();
        String stringFdServingSizeGramSQL = db.quoteSmart(stringFdServingSizeGram);
        db.update("food_diary", "_id", longFdID, "fd_serving_size_gram", stringFdServingSizeGramSQL);
        double doubleFdServingSizeGram = Double.parseDouble(stringFdServingSizeGram);

        // Update fd serving size pcs
        double doubleFdServingSizePcs = Math.round(doubleFdServingSizeGram / doubleGetFromSQLFoodServingSizeGram);
        String stringFdServingSizePcs = "" + doubleFdServingSizePcs;
        String stringFdServingSizePcsSQL = db.quoteSmart(stringFdServingSizePcs);
        db.update("food_diary", "_id", longFdID, "fd_serving_size_pcs", stringFdServingSizePcsSQL);

        // Update fd energy calculated
        double doubleFdEnergyCalculated = Math.round((doubleFdServingSizeGram*doubleGetFromSQLFoodEnergy)/100);
        String stringFdEnergyCalcualted = "" + doubleFdEnergyCalculated;
        String stringFdEnergyCalcualtedSQL = db.quoteSmart(stringFdEnergyCalcualted);
        db.update("food_diary", "_id", longFdID, "fd_energy_calculated", stringFdEnergyCalcualtedSQL);

        // Proteins calcualted
        double doubleFdProteinsCalculated = Math.round((doubleFdServingSizeGram*doubleGetFromSQLFoodProteins)/100);
        String stringFdProteinsCalcualted = "" + doubleFdProteinsCalculated;
        String stringFdProteinsCalcualtedSQL = db.quoteSmart(stringFdProteinsCalcualted);
        db.update("food_diary", "_id", longFdID, "fd_proteins_calculated", stringFdProteinsCalcualtedSQL);

        // Carbohydrates calcualted
        double doubleFdCarbohydratesCalculated = Math.round((doubleFdServingSizeGram*doubleGetFromSQLFoodCarbohydrates)/100);
        String stringFdCarbohydratesCalcualted = "" + doubleFdCarbohydratesCalculated;
        String stringFdCarbohydratesCalcualtedSQL = db.quoteSmart(stringFdCarbohydratesCalcualted);
        db.update("food_diary", "_id", longFdID, "fd_carbohydrates_calculated", stringFdCarbohydratesCalcualtedSQL);


        // Fat calcualted
        double doubleFdFatCalculated = Math.round((doubleFdServingSizeGram*doubleGetFromSQLFoodFat)/100);
        String stringFdFatCalcualted = "" + doubleFdFatCalculated;
        String stringFdFatCalcualtedSQL = db.quoteSmart(stringFdFatCalcualted);
        db.update("food_diary", "_id", longFdID, "fd_fats_calculated", stringFdFatCalcualtedSQL);


        db.close();


        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();


        /* Restart fragment */
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment(), HomeFragment.class.getName()).commit();


    }

    public void OnClickDeleteFdLineSubmit(){
        Toast.makeText(getActivity(), "Deleted " + currentFoodName, Toast.LENGTH_SHORT).show();


        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        long longPrimaryKey = 0;
        try{
            longPrimaryKey = Long.parseLong(currentFdId);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        db.delete("food_diary", "_id", longPrimaryKey);

        db.close();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment(), HomeFragment.class.getName()).commit();


    }

    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    private void releaseLock(String lock){
        if(lock.equals("portionSizeGram")){
            lockPortionSizeByGram = false;
        }
        else {
            lockPortionSizeByPcs = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
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
        HomeFragment fragment = (HomeFragment) getFragmentManager().findFragmentById(R.id.flContent);

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
