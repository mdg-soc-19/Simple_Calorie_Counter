package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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


public class CategoriesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Cursor categoriesCursor;
    private View mainView;


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
        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Categories");
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        populateList("0", "");

        setHasOptionsMenu(true);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        try{
           getActivity().getMenuInflater().inflate(R.menu.menu_categories, menu);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        Toast.makeText(getActivity(), menuItem +" Clicked", Toast.LENGTH_LONG).show();

        if(id == R.id.action_add){
              createNewCategory();
        }else if(id == R.id.action_delete){

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
        }
        db.close();
    }

    public void listItemClicked(int listItemIDClicked){
        Toast.makeText(getActivity(),"Position = "+listItemIDClicked,Toast.LENGTH_LONG).show();

        categoriesCursor.moveToPosition(listItemIDClicked);

        String id = categoriesCursor.getString(0);
        String name = categoriesCursor.getString(1);
        String parentID = categoriesCursor.getString(2);


        ((MainActivity)getActivity()).getSupportActionBar().setTitle(name);

        populateList(id,name);
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

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
