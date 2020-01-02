package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class AddFoodToDiaryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    private String currentMealNumber;
    private String currentCategoryId;
    private String currentCategoryName;
    private String currentFoodId;
    private String currentFoodName;

    private String currentPortionSizePcs;
    private String currentPortionSizeGram;
    private boolean lockPortionSizeByPcs;
    private boolean lockPortionSizeByGram;



    private OnFragmentInteractionListener mListener;

    public AddFoodToDiaryFragment() {
        // Required empty public constructor
    }


    public static AddFoodToDiaryFragment newInstance(String param1, String param2) {
        AddFoodToDiaryFragment fragment = new AddFoodToDiaryFragment();
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_categories, menu);

        menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Add food to diary");

        Bundle bundle = this.getArguments();

        if(bundle != null){
            currentMealNumber = bundle.getString("mealNumber");
        }
        populateListWithCategories("0", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_food_to_diary, container, false);
    }


    public void populateListWithCategories(String stringCategoryParentID, String stringCatgoryName){


        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String[] fields = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", stringCategoryParentID, "category_name", "ASC");


        ArrayList<String> values = new ArrayList<>();

        int categoriesCount = listCursorCategory.getCount();
        for(int x=0;x<categoriesCount;x++){
            values.add(listCursorCategory.getString(listCursorCategory.getColumnIndex("category_name")));
            listCursorCategory.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);


        ListView lv = getActivity().findViewById(R.id.listViewAddFoodToDiary);
        lv.setAdapter(adapter);


        if(stringCategoryParentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    categoryListItemClicked(arg2);
                }
            });
        }
        db.close();
    }

    public void categoryListItemClicked(int listItemIndexClicked){
        listCursorCategory.moveToPosition(listItemIndexClicked);

        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentCategoryID = listCursorCategory.getString(2);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Add food from " + currentCategoryName + " to diary");

        populateListWithCategories(currentCategoryId, currentCategoryName);

        showFoodInCategory(currentCategoryId, currentCategoryName, parentCategoryID);
    }

    public void showFoodInCategory(String categoryId, String categoryName, String categoryParentID){
        if(!(categoryParentID.equals("0"))) {

            int id = R.layout.fragment_food;
            setMainView(id);

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String[] fields = new String[] {
                    "_id",
                    "food_name",
                    "food_manufactor_name",
                    "food_description",
                    "food_serving_size",
                    "food_serving_measurement",
                    "food_serving_name_number",
                    "food_serving_name_word",
                    "food_energy_calculated"
            };
            listCursorFood = db.select("food", fields, "food_category_id", categoryId, "food_name", "ASC");


            ListView lvItems = getActivity().findViewById(R.id.listViewFood);


            FoodCursorAdapter adapter = new FoodCursorAdapter(getActivity(), listCursorFood);


            try{
                lvItems.setAdapter(adapter);
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "E: " + e.toString(), Toast.LENGTH_LONG).show();
            }


            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    foodInCategoryListItemClicked(arg2);
                }
            });

            db.close();
        }
    }

    public void foodInCategoryListItemClicked(int listItemFoodIndexClicked){

        int id = R.layout.fragment_add_food_to_diary_view_food;
        setMainView(id);

        listCursorFood.moveToPosition(listItemFoodIndexClicked);

        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);


        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Add " + currentFoodName);

        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String[] fields = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name",
                "food_description",
                "food_serving_size",
                "food_serving_measurement",
                "food_serving_name_number",
                "food_serving_name_word",
                "food_energy",
                "food_proteins",
                "food_carbohydrates",
                "food_fats",
                "food_energy_calculated",
                "food_proteins_calculated",
                "food_carbohydrates_calculated",
                "food_fats_calculated",
                "food_category_id"

        };
        String currentIdSQL = db.quoteSmart(currentFoodId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSize = foodCursor.getString(4);
        String stringServingMesurment = foodCursor.getString(5);
        String stringServingNameNumber = foodCursor.getString(6);
        String stringServingNameWord = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);
        String stringEnergyCalculated = foodCursor.getString(12);
        String stringProteinsCalculated = foodCursor.getString(13);
        String stringCarbohydratesCalculated = foodCursor.getString(14);
        String stringFatCalculated = foodCursor.getString(15);
        String stringCategoryId = foodCursor.getString(16);



        TextView textViewViewFoodName = getView().findViewById(R.id.textViewViewFoodName);
        textViewViewFoodName.setText(stringName);


        TextView textViewViewFoodManufactorName = getView().findViewById(R.id.textViewViewFoodManufactorName);
        textViewViewFoodManufactorName.setText(stringManufactorName);

        EditText editTextPortionSizePcs = getActivity().findViewById(R.id.editTextPortionSizePcs);
        editTextPortionSizePcs.setText(stringServingNameNumber);

        EditText editTextPortionSizeGram = getActivity().findViewById(R.id.editTextPortionSizeGram);
        editTextPortionSizeGram.setText(stringServingSize);


        TextView textViewViewFoodAbout = getView().findViewById(R.id.textViewViewFoodAbout);
        String foodAbout = stringServingSize + " " + stringServingMesurment + " = " +
                stringServingNameNumber  + " " + stringServingNameWord + ".";
        textViewViewFoodAbout.setText(foodAbout);


        TextView textViewViewFoodDescription = getView().findViewById(R.id.textViewViewFoodDescription);
        textViewViewFoodDescription.setText(stringDescription);


        TextView textViewViewFoodEnergyPerHundred =  getView().findViewById(R.id.textViewViewFoodEnergyPerHundred);
        TextView textViewViewFoodProteinsPerHundred = getView().findViewById(R.id.textViewViewFoodProteinsPerHundred);
        TextView textViewViewFoodCarbsPerHundred =  getView().findViewById(R.id.textViewViewFoodCarbsPerHundred);
        TextView textViewViewFoodFatPerHundred = getView().findViewById(R.id.textViewViewFoodFatPerHundred);

        TextView textViewViewFoodEnergyPerN = getView().findViewById(R.id.textViewViewFoodEnergyPerN);
        TextView textViewViewFoodProteinsPerN =  getView().findViewById(R.id.textViewViewFoodProteinsPerN);
        TextView textViewViewFoodCarbsPerN = getView().findViewById(R.id.textViewViewFoodCarbsPerN);
        TextView textViewViewFoodFatPerN =  getView().findViewById(R.id.textViewViewFoodFatPerN);

        textViewViewFoodEnergyPerHundred.setText(stringEnergy);
        textViewViewFoodProteinsPerHundred.setText(stringProteins);
        textViewViewFoodCarbsPerHundred.setText(stringCarbohydrates);
        textViewViewFoodFatPerHundred.setText(stringFat);

        textViewViewFoodEnergyPerN.setText(stringEnergyCalculated);
        textViewViewFoodProteinsPerN.setText(stringProteinsCalculated);
        textViewViewFoodCarbsPerN.setText(stringCarbohydratesCalculated);
        textViewViewFoodFatPerN.setText(stringFatCalculated);

        db.close();
        editTextPortionSizePcs.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!(s.toString().equals(""))){

                    editTextPortionSizePcsOnChange();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editTextPortionSizePcs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String lock = "portionSizePcs";
                    releaseLock(lock);
                }
            }
        });

        editTextPortionSizeGram.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!(s.toString().equals(""))){
                    editTextPortionSizeGramOnChange();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editTextPortionSizeGram.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String lock = "portionSizeGram";
                    releaseLock(lock);
                }
            }
        });

        Button buttonSubmit = getActivity().findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiary();
            }
        });
    }

    public void editTextPortionSizePcsOnChange(){
        if(!(lockPortionSizeByGram)) {

            lockPortionSizeByPcs = true;

            EditText editTextPortionSizePcs = getActivity().findViewById(R.id.editTextPortionSizePcs);
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

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String[] fields = new String[]{
                    "food_serving_size"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);


            String stringServingSize = foodCursor.getString(0);
            db.close();


            double doubleServingSize = 0;
            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            double doublePortionSizeGram = Math.round(doublePortionSizePcs * doubleServingSize);


            EditText editTextPortionSizeGram = getActivity().findViewById(R.id.editTextPortionSizeGram);
            editTextPortionSizeGram.setText("" + doublePortionSizeGram);
        }

    }

    public void editTextPortionSizeGramOnChange(){
        if(!(lockPortionSizeByPcs)) {

            lockPortionSizeByGram = true;

            EditText editTextPortionSizeGram = getActivity().findViewById(R.id.editTextPortionSizeGram);
            String stringPortionSizeGram = editTextPortionSizeGram.getText().toString();
            double doublePortionSizeGram = 0;
            try {
                doublePortionSizeGram = Double.parseDouble(stringPortionSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }


            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String[] fields = new String[]{
                    "food_serving_size",
                    "food_serving_name_number"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);


            String stringServingSize = foodCursor.getString(0);
            String stringServingNameNumber = foodCursor.getString(1);
            db.close();


            double doubleServingSize = 0;
            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            double doubleServingNumber = 0;
            try {
                doubleServingNumber = Double.parseDouble(stringServingNameNumber);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }



            double doublePortionSizePcs = Math.round(doublePortionSizeGram / (doubleServingSize/doubleServingNumber));


            EditText editTextPortionSizePcs = getActivity().findViewById(R.id.editTextPortionSizePcs);
            editTextPortionSizePcs.setText("" + doublePortionSizePcs);
        }

    }

    public void addFoodToDiary(){
        int error = 0;

        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String[] fields = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name",
                "food_description",
                "food_serving_size",
                "food_serving_measurement",
                "food_serving_name_number",
                "food_serving_name_word",
                "food_energy",
                "food_proteins",
                "food_carbohydrates",
                "food_fats",
                "food_energy_calculated",
                "food_proteins_calculated",
                "food_carbohydrates_calculated",
                "food_fats_calculated"
        };
        String currentIdSQL = db.quoteSmart(currentFoodId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Convert cursor to strings
        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSizeGram = foodCursor.getString(4);
        String stringServingSizeGramMesurment = foodCursor.getString(5);
        String stringServingSizePcs = foodCursor.getString(6);
        String stringServingSizePcsMesurment = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);


        EditText editTextPortionSizeGram = getActivity().findViewById(R.id.editTextPortionSizeGram);
        String fdServingSizeGram = editTextPortionSizeGram.getText().toString();
        String fdServingSizeGramSQL = db.quoteSmart(fdServingSizeGram);
        double doublePortionSizeGram = 0;
        try{
            doublePortionSizeGram = Double.parseDouble(fdServingSizeGram);
        }
        catch (NumberFormatException nfe){
            error = 1;
            Toast.makeText(getActivity(), "Please fill in a number in gram", Toast.LENGTH_SHORT).show();
        }
        if(fdServingSizeGram.equals("")){
            error = 1;
            Toast.makeText(getActivity(), "Gram cannot be empty", Toast.LENGTH_SHORT).show();
        }




        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        ++month;
        int date = Calendar.getInstance().get(Calendar.DATE);
        String stringFdDate = year + "-" + month + "-" + date;
        String stringFdDateSQL = db.quoteSmart(stringFdDate);


        String stringFdMealNumber = currentMealNumber;
        String stringFdMealNumberSQL = db.quoteSmart(stringFdMealNumber);


        String stringFdFoodId = currentFoodId;
        String StringFdFoodIdSQL = db.quoteSmart(stringFdFoodId);


        String fdServingSizeGramMesurmentSQL = db.quoteSmart(stringServingSizeGramMesurment);


        double doubleServingSizeGram = 0;
        try {
            doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        double doublePortionSizePcs = Math.round(doublePortionSizeGram / doubleServingSizeGram);
        String stringFdServingSizePcs = "" + doublePortionSizePcs;
        String stringFdServingSizePcsSQL = db.quoteSmart(stringFdServingSizePcs);


        String stringFdServingSizePcsMesurmentSQL = db.quoteSmart(stringServingSizePcsMesurment);


        double doubleEnergyPerHundred = Double.parseDouble(stringEnergy);

        double doubleFdEnergyCalculated = Math.round((doublePortionSizeGram*doubleEnergyPerHundred)/100);
        String stringFdEnergyCalcualted = "" + doubleFdEnergyCalculated;
        String stringFdEnergyCalcualtedSQL = db.quoteSmart(stringFdEnergyCalcualted);

        // Proteins calcualted
        double doubleProteinsPerHundred = Double.parseDouble(stringProteins);

        double doubleFdProteinsCalculated = Math.round((doublePortionSizeGram*doubleProteinsPerHundred)/100);
        String stringFdProteinsCalcualted = "" + doubleFdProteinsCalculated;
        String stringFdProteinsCalcualtedSQL = db.quoteSmart(stringFdProteinsCalcualted);


        // Carbohydrates calcualted
        double doubleCarbohydratesPerHundred = Double.parseDouble(stringCarbohydrates);

        double doubleFdCarbohydratesCalculated = Math.round((doublePortionSizeGram*doubleCarbohydratesPerHundred)/100);
        String stringFdCarbohydratesCalcualted = "" + doubleFdCarbohydratesCalculated;
        String stringFdCarbohydratesCalcualtedSQL = db.quoteSmart(stringFdCarbohydratesCalcualted);

        // Fat calcualted
        double doubleFatPerHundred = Double.parseDouble(stringFat);

        double doubleFdFatCalculated = Math.round((doublePortionSizeGram*doubleFatPerHundred)/100);
        String stringFdFatCalcualted = "" + doubleFdFatCalculated;
        String stringFdFatCalcualtedSQL = db.quoteSmart(stringFdFatCalcualted);

        if(error == 0){
            String inpFields = "_id, fd_date, fd_meal_number, fd_food_id," +
                    "fd_serving_size_gram, fd_serving_size_gram_measurement," +
                    " fd_serving_size_pcs, fd_serving_size_pcs_measurement," +
                    " fd_energy_calculated, fd_proteins_calculated," +
                    " fd_carbohydrates_calculated, fd_fats_calculated";

            String inpValues = "NULL, " + stringFdDateSQL + ", " + stringFdMealNumberSQL + ", " + StringFdFoodIdSQL + ", " +
                    fdServingSizeGramSQL + ", " + fdServingSizeGramMesurmentSQL + ", " +
                    stringFdServingSizePcsSQL + ", " + stringFdServingSizePcsMesurmentSQL + ", " +
                    stringFdEnergyCalcualtedSQL + ", " + stringFdProteinsCalcualtedSQL + ", " +
                    stringFdCarbohydratesCalcualtedSQL + ", " + stringFdFatCalcualtedSQL;

            db.insert("food_diary", inpFields, inpValues);

            Toast.makeText(getActivity(), "Food diary updated", Toast.LENGTH_SHORT).show();


            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        }

        db.close();
    }

    public void releaseLock(String lock){
        if(lock.equals("portionSizeGram")){
            lockPortionSizeByGram = false;
        }else{
            lockPortionSizeByPcs = false;
        }

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
