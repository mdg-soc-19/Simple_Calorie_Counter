package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class CategoriesFragment extends Fragment implements IOnBackPressed {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Cursor categoriesCursor;
    private View mainView;

    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;

    private String currentId;
    private String currentName;


    private OnFragmentInteractionListener mListener;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Categories");
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        populateList("0", "");

        setHasOptionsMenu(true);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        try{
           getActivity().getMenuInflater().inflate(R.menu.menu_categories, menu);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        Toast.makeText(getActivity(), menuItem +" Clicked", Toast.LENGTH_LONG).show();

        if(id == R.id.action_add){
              createNewCategory();
        }else if(id == R.id.action_edit){
               editCategory();
        }else if(id == R.id.action_delete){
               deleteCategory();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void createNewCategory(){
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        String[] fields = new String[]{
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0","category_name","ASC");

        String[] arraySpinnerCategories = new String[dbCursor.getCount() + 1 ];

        arraySpinnerCategories[0] = "This is a new parent";

        for(int x = 1; x < arraySpinnerCategories.length; x++){
            arraySpinnerCategories[x] = dbCursor.getString(1);
            dbCursor.moveToNext();
        }

        Spinner spinnerParent = getActivity().findViewById(R.id.spinnerParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);

        Button buttonCategorySubmit = getActivity().findViewById(R.id.buttonCategorySubmit);
        buttonCategorySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 createNewCategorySubmitOnClick();
            }
        });

        db.close();
    }

    public void createNewCategorySubmitOnClick(){

        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        int error = 0;


        EditText editTextName = getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a Name.", Toast.LENGTH_LONG).show();
            error = 1;
        }


        Spinner spinner = getActivity().findViewById(R.id.spinnerParent);
        String stringSpinnerParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerParent.equals("This is a new parent")){
            parentID = "0";
        }
        else{

            String stringSpinnerParentSQL = db.quoteSmart(stringSpinnerParent);
            String[] fields = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerParentSQL); // For example, stringSpinnerParentSQL = category_name = 'Bread'
            parentID = findParentID.getString(0);


        }

        if(error == 0){

            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);


            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.insert("categories", "_id, category_name, category_parent_id", input);


            Toast.makeText(getActivity(), "Category created", Toast.LENGTH_LONG).show();


            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }


        db.close();
    }



    public void editCategory(){
        Toast.makeText(getActivity(), "You want to Edit " + currentName, Toast.LENGTH_LONG).show();

        setMainView(R.layout.fragment_categories_add_edit);

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        String[] fieldsC = new String[] { "category_parent_id" };
        String currentIdSQL = db.quoteSmart(currentCategoryId);

        Cursor c = db.select("categories", fieldsC, "_id", currentIdSQL);
        String currentParentID = c.getString(0);
        int intCurrentParentID = 0;
        try {
            intCurrentParentID = Integer.parseInt(currentParentID);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        EditText editTextName = getActivity().findViewById(R.id.editTextName);
        editTextName.setText(currentName);
        String[] fields = new String[]{
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0","category_name","ASC");

        String[] arraySpinnerCategories = new String[dbCursor.getCount() + 1 ];

        arraySpinnerCategories[0] = "This is a new parent";

        int correctParentID = 0;
        for(int x = 1; x < arraySpinnerCategories.length; x++){
            arraySpinnerCategories[x] = dbCursor.getString(1);

            if(dbCursor.getString(0).equals(currentParentID)){
                correctParentID = x;
            }
            dbCursor.moveToNext();

        }

        Spinner spinnerParent = getActivity().findViewById(R.id.spinnerParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);


        spinnerParent.setSelection(correctParentID);

        db.close();
        Button buttonHome = getActivity().findViewById(R.id.buttonCategorySubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategorySubmitOnClick();
            }
        });

    }

    public void editCategorySubmitOnClick(){
        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        int error = 0;


        EditText editTextName = getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }



        Spinner spinner = getActivity().findViewById(R.id.spinnerParent);
        String stringSpinnerParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerParent.equals("This is a new parent")){
            parentID = "0";
        }
        else{

            String stringSpinnerParentSQL = db.quoteSmart(stringSpinnerParent);
            String[] fields = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerParentSQL);
            parentID = findParentID.getString(0);


        }

        if(error == 0){

            long longCurrentID = Long.parseLong(currentCategoryId);


            long currentIDSQL = db.quoteSmart(longCurrentID);
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);


            db.update("categories", "_id", currentIDSQL, "category_name", stringNameSQL);
            db.update("categories", "_id", currentIDSQL, "category_parent_id", parentIDSQL);


            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_LONG).show();


            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }


        db.close();
    }



    public void deleteCategory(){

        int id = R.layout.fragment_categories_delete;
        setMainView(id);


        Button buttonCancel = getActivity().findViewById(R.id.buttonCategoriesCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryCancelOnClick();
            }
        });

        Button buttonConfirm = getActivity().findViewById(R.id.buttonCategoriesConfirmDelete);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryConfirmOnClick();
            }
        });


    }

    public void deleteCategoryCancelOnClick(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }

    public void deleteCategoryConfirmOnClick(){

        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        long longCurrentID = Long.parseLong(currentCategoryId);


        long currentIDSQL = db.quoteSmart(longCurrentID);


        db.delete("categories", "_id", currentIDSQL);


        db.close();


        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_LONG).show();


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }



    public void populateList(String parentID, String parentName) {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        String[] fields = new String[]{
                "_id",
                "category_name",
                "category_parent_id"
        };
        categoriesCursor = db.select("categories", fields, "category_parent_id", parentID,"category_name","ASC" );

        ArrayList<String> values = new ArrayList<>();

        int categoriesCount = categoriesCursor.getCount();

        for (int x = 0; x < categoriesCount; x++) {

            values.add(categoriesCursor.getString(categoriesCursor.getColumnIndex("category_name")));
            categoriesCursor.moveToNext();

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, values);

        ListView lv = getActivity().findViewById(R.id.listViewCategories);
        lv.setAdapter(adapter);

        if (parentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemClicked(position);
                }
            });

        }else{

            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);

        }
        db.close();
    }

    public void listItemClicked(int listItemIDClicked){

        categoriesCursor.moveToPosition(listItemIDClicked);

        currentCategoryId = categoriesCursor.getString(0);
        currentName = categoriesCursor.getString(1);
        String parentID = categoriesCursor.getString(2);


        ((MainActivity)getActivity()).getSupportActionBar().setTitle(currentName);

        populateList(currentCategoryId,currentName);

        showFoodInCategory(currentCategoryId, currentName, parentID);
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


            ListView lvItemsFood = getActivity().findViewById(R.id.listViewFood);


            FoodCursorAdapter adapter = new FoodCursorAdapter(getActivity(), listCursorFood);


            lvItemsFood.setAdapter(adapter);


            lvItemsFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    foodListItemClicked(arg2);
                }
            });


            db.close();

        }
    }

    private void foodListItemClicked(int intFoodListItemIndex){

        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = FoodFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bundle bundle = new Bundle();
        bundle.putString("currentFoodId", ""+currentFoodId);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup)getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
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
        CategoriesFragment fragment = (CategoriesFragment) getFragmentManager().findFragmentById(R.id.flContent);

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
