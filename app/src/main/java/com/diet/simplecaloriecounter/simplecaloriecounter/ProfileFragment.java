package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private View mainView;

    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        initializeGetDataFromDbAndDisplay();

    }

    public void initializeGetDataFromDbAndDisplay() {


        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        long rowID = 1;
        String[] fields = new String[]{
                "_id",
                "user_dob",
                "user_gender",
                "user_height",
                "user_mesurment"
        };
        Cursor c = db.selectPrimaryKey("users","_id",rowID, fields);
        String stringUserDob = c.getString(1);
        String stringUserGender = c.getString(2);
        String stringUserHeight = c.getString(3);
        String stringUserMesurment = c.getString(4);

        String[] items1 = stringUserDob.split("-");
        String stringUserDobYear = items1[0];
        String stringUserDobMonth = items1[1];
        String stringUserDobDate = items1[2];


        int spinnerDOBDaySelectedIndex = 0;

        String[] arraySpinnerDOBDay = new String[31];
        for (int x = 0; x < 31; x++) {
            arraySpinnerDOBDay[x] = "" + (x + 1);

            if (stringUserDobDate.equals("0" + (x + 1)) || stringUserDobDate.equals("" + (x + 1))) {
                spinnerDOBDaySelectedIndex = x;

            }

        }
        Spinner spinnerDOBDay =  getActivity().findViewById(R.id.spinnerEditProfileDOBDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapter);

        spinnerDOBDay.setSelection(spinnerDOBDaySelectedIndex);

        stringUserDobDate.replace("0", "");


        int intUserDobMonth = 0;
        try {
            intUserDobMonth = Integer.parseInt(stringUserDobMonth);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        intUserDobMonth = intUserDobMonth - 1;
        Spinner spinnerDOBMonth = getActivity().findViewById(R.id.spinnerEditProfileDOBMonth);
        spinnerDOBMonth.setSelection(intUserDobMonth);



        int spinnerDOBYearSelectedIndex = 0;
        String[] arraySpinnerDOBYear = new String[100];
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int end = year - 100;
        int index = 0;
        for (int x = year; x > end; x--) {

            arraySpinnerDOBYear[index] = "" + x;
            if (stringUserDobYear.equals("" + x)) {
                spinnerDOBYearSelectedIndex = index;
            }
            index++;

        }
        Spinner spinnerDOBYear = getActivity().findViewById(R.id.spinnerEditProfileDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);
        spinnerDOBYear.setSelection(spinnerDOBYearSelectedIndex);



        RadioButton radioButtonGenderMale =  getActivity().findViewById(R.id.radioButtonGenderMale);
        RadioButton radioButtonGenderFemale = getActivity().findViewById(R.id.radioButtonGenderFemale);
        if (stringUserGender.startsWith("m")) {
            radioButtonGenderMale.setChecked(true);
            radioButtonGenderFemale.setChecked(false);
        } else {
            radioButtonGenderMale.setChecked(false);
            radioButtonGenderFemale.setChecked(true);
        }


        EditText editTextEditProfileHeightCm =  getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextEditProfileHeightInches = getActivity().findViewById(R.id.editTextEditProfileHeightInches);
        TextView textViewEditProfileCm = getActivity().findViewById(R.id.textViewEditProfileCm);

        if (stringUserMesurment.startsWith("m")) {
            editTextEditProfileHeightInches.setVisibility(View.GONE);
            editTextEditProfileHeightCm.setText(stringUserHeight);
        } else {
            textViewEditProfileCm.setText("Feet and Inches");
            double heightCm = 0;
            double heightFeet;
            double heightInches;

            try {
                heightCm = Double.parseDouble(stringUserHeight);
            } catch (NumberFormatException nfe) {

            }
            if (heightCm != 0) {

                heightFeet = (heightCm)/30.48;
                heightInches =(heightFeet - (int)heightFeet)*12;

                editTextEditProfileHeightCm.setText((int)heightFeet);
                editTextEditProfileHeightInches.setText((int)heightInches);

            }

        }

        Spinner spinnerEditProfileMesurment = getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        if(stringUserMesurment.startsWith("m")) {
            spinnerEditProfileMesurment.setSelection(0);
        }
        else{
            spinnerEditProfileMesurment.setSelection(1);
        }


        spinnerEditProfileMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mesurmentChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        Button buttonEditProfileSubmit = getActivity().findViewById(R.id.buttonEditProfileSubmit);
        buttonEditProfileSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editProfileSubmit();
            }
        });

        db.close();
    }

    public void mesurmentChanged(){
        Spinner spinnerMesurment = getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();


        EditText editTextEditProfileHeightCm = getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextEditProfileHeightInches = getActivity().findViewById(R.id.editTextEditProfileHeightInches);

        TextView textViewEditProfileCm = getActivity().findViewById(R.id.textViewEditProfileCm);

        String stringHeightCm = editTextEditProfileHeightCm.getText().toString();
        String stringHeightInches = editTextEditProfileHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;

        if(stringMesurment.startsWith("M")) {

            editTextEditProfileHeightInches.setVisibility(View.GONE);
            textViewEditProfileCm.setText("cm");

            try {
                heightFeet = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {

            }

            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {

            }

            if(heightFeet != 0 && heightInches != 0) {
                heightCm = ((heightFeet * 12) + heightInches) * 2.54;
                editTextEditProfileHeightCm.setText("" + Math.round(heightCm));
            }

        }
        else{

            editTextEditProfileHeightInches.setVisibility(View.VISIBLE);
            textViewEditProfileCm.setText("Feet and Inches");
            try {
                heightCm = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {

            }
            if(heightCm != 0){

                heightFeet = heightCm/30.48;
                heightInches = (heightFeet - (int)heightFeet)*12;

                editTextEditProfileHeightCm.setText("" + (int)heightFeet);
                editTextEditProfileHeightInches.setText("" + (int)heightInches);

            }
        }
    }

    public void editProfileSubmit(){
        DBAdapter db = new DBAdapter(getActivity());
        db.open();



        int error = 0;


        Spinner spinnerDOBDay = getActivity().findViewById(R.id.spinnerEditProfileDOBDay);
        String stringDOBDay = spinnerDOBDay.getSelectedItem().toString();
        int intDOBDay = 0;
        try {
            intDOBDay = Integer.parseInt(stringDOBDay);

            if(intDOBDay < 10){
                stringDOBDay = "0" + stringDOBDay;
            }

        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            error = 1;
            Toast.makeText(getActivity(), "Please select a day for your birthday.", Toast.LENGTH_SHORT).show();
        }


        // Date of Birth Month
        Spinner spinnerDOBMonth = getActivity().findViewById(R.id.spinnerEditProfileDOBMonth);
        String stringDOBMonth;
        int positionDOBMonth = spinnerDOBMonth.getSelectedItemPosition();
        int month = positionDOBMonth+1;
        if(month < 10){
            stringDOBMonth = "0" + month;
        }
        else{
            stringDOBMonth = "" + month;
        }



        Spinner spinnerDOBYear = getActivity().findViewById(R.id.spinnerEditProfileDOBYear);
        String stringDOBYear = spinnerDOBYear.getSelectedItem().toString();
        int intDOBYear = 0;
        try {
            intDOBYear = Integer.parseInt(stringDOBYear);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            error = 1;
            Toast.makeText(getActivity(), "Please select a year for your birthday.", Toast.LENGTH_SHORT).show();
        }


        String dateOfBirth = intDOBYear + "-" + stringDOBMonth + "-" + stringDOBDay;
        String dateOfBirthSQL = db.quoteSmart(dateOfBirth);



        RadioGroup radioGroupGender = getActivity().findViewById(R.id.radioGroupGender);
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId();
        View radioButtonGender = radioGroupGender.findViewById(radioButtonID);
        int position = radioGroupGender.indexOfChild(radioButtonGender);

        String stringGender;
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }
        String genderSQL = db.quoteSmart(stringGender);




        EditText editTextHeightCm = getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextHeightInches = getActivity().findViewById(R.id.editTextEditProfileHeightInches);
        String stringHeightCm = editTextHeightCm.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;
        boolean metric = true;


        Spinner spinnerMesurment = getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        String stringMesurment;

        int intMesurment = spinnerMesurment.getSelectedItemPosition();
        if(intMesurment == 0){
            stringMesurment = "metric";
        }
        else{
            stringMesurment = "imperial";
            metric = false;
        }
        String mesurmentSQL = db.quoteSmart(stringMesurment);

        if(metric) {

            try {
                heightCm = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (cm) has to be a number.", Toast.LENGTH_SHORT).show();
            }
        }
        else {


            try {
                heightFeet = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (feet) has to be a number.", Toast.LENGTH_SHORT).show();
            }


            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (inches) has to be a number.", Toast.LENGTH_SHORT).show();
            }

            heightCm = ((heightFeet * 12) + heightInches) * 2.54;
            heightCm = Math.round(heightCm);
        }
        stringHeightCm = "" + heightCm;
        String heightCmSQL = db.quoteSmart(stringHeightCm);



        if(error == 0){

            long id = 1;

            String[] fields = new String[] {
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_mesurment"
            };
            String[] values = new String[] {
                    dateOfBirthSQL,
                    genderSQL,
                    heightCmSQL,
                    mesurmentSQL
            };

            db.update("users", "_id", id, fields, values);
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

        }

        db.close();
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_goal, menu);

        menuItemEdit = menu.findItem(R.id.menu_action_goal_edit);

    }

    private void setMainView(int id){

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
