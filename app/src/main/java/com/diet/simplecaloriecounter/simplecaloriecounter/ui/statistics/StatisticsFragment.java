package com.diet.simplecaloriecounter.simplecaloriecounter.ui.statistics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.diet.simplecaloriecounter.simplecaloriecounter.DBAdapter;
import com.diet.simplecaloriecounter.simplecaloriecounter.IOnBackPressed;
import com.diet.simplecaloriecounter.simplecaloriecounter.MainActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import com.diet.simplecaloriecounter.simplecaloriecounter.R;


public class StatisticsFragment extends Fragment implements IOnBackPressed {

    private StatisticsViewModel mViewModel;
    private BarChart mChart;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private String currentDateYear = "";
    private String currentDateMonth = "";
    private String currentDateDay = "";

    private OnFragmentInteractionListener mListener;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Statistics");

        CalculatePresentDate();

    }

    public void CalculatePresentDate() {

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
        String stringDate = currentDateYear + "-" + currentDateMonth + "-" + currentDateDay;
        InitializeBarChart(stringDate);

    }

    private void InitializeBarChart(String currentDate){
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        mChart = getActivity().findViewById(R.id.BarChart);
        String currentDateSQL = db.quoteSmart(currentDate);

        String[] fieldsFoodDiarySum = new String[]{
                "_id",
                "food_diary_sum_date",
                "food_diary_sum_energy",
        };

        Cursor cursorFoodDiarySum  = db.select("food_diary_sum", fieldsFoodDiarySum);

        String foodId;
        String DiaryDate;
        String Energy;
        int EnergyInt;
        ArrayList xAxis = new ArrayList();
        ArrayList yAxis = new ArrayList();

        Toast.makeText(getActivity(), "" + cursorFoodDiarySum.getCount(), Toast.LENGTH_LONG).show();

        for(int x = 0; x < cursorFoodDiarySum.getCount(); x++){

            foodId = cursorFoodDiarySum.getString(0);
            DiaryDate = cursorFoodDiarySum.getString(1);
            Energy = cursorFoodDiarySum.getString(2);

            EnergyInt = Integer.parseInt(Energy);

            yAxis.add(new BarEntry(EnergyInt,x));
            xAxis.add(DiaryDate);

            cursorFoodDiarySum.moveToNext();

        }

        BarDataSet bardataset = new BarDataSet(yAxis, "Calories(kcal)");
        mChart.animateY(1500);
        BarData data = new BarData(xAxis ,bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        mChart.setData(data);

        // Changing Text sizes
        mChart.getXAxis().setTextSize(10f);
        bardataset.setValueTextSize(17f);

        // Setting Y - Axis Range
        YAxis LeftYAxis = mChart.getAxisLeft();
        LeftYAxis.setAxisMaxValue(4000f);
        LeftYAxis.setAxisMinValue(0f);
        LeftYAxis.setLabelCount(17,true);

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setAxisMaxValue(4000f);
        rightYAxis.setAxisMinValue(0f);
        rightYAxis.setLabelCount(17, true);

        String[] goalFields = new String[]{
                "_id",
                "goal_energy_BMR",
                "goal_energy_with_diet",
                "goal_energy_with_activity",
                "goal_energy_with_activity_and_diet"
        };

        Cursor cursorGoal = db.select("goal",goalFields);
        cursorGoal.moveToLast();

        String goalBMR = cursorGoal.getString(1);
        String goalDiet = cursorGoal.getString(2);
        String goalActivity = cursorGoal.getString(3);
        String goalActivityAndDiet = cursorGoal.getString(4);

        // Limit Line (Goal with Activity and Diet)

        LimitLine ll = new LimitLine(Float.parseFloat(goalActivityAndDiet), "Goal with Activity and Diet");
        mChart.getAxisLeft().addLimitLine(ll);
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        // Limit Line (Goal with Diet)
        LimitLine ll1 = new LimitLine(Float.parseFloat(goalDiet), "Goal with Diet");
        mChart.getAxisLeft().addLimitLine(ll1);
        ll1.setLineColor(Color.BLUE);
        ll1.setLineWidth(4f);
        ll1.setTextColor(Color.BLACK);
        ll1.setTextSize(12f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);

        // Limit Line (Goal with Activity)
        LimitLine ll2 = new LimitLine(Float.parseFloat(goalActivity), "Goal with Activity");
        mChart.getAxisLeft().addLimitLine(ll2);
        ll2.setLineColor(Color.GREEN);
        ll2.setLineWidth(4f);
        ll2.setTextColor(Color.BLACK);
        ll2.setTextSize(12f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        // Limit Line (Goal BMR/At rest)
        LimitLine ll3 = new LimitLine(Float.parseFloat(goalBMR), "Goal at Rest/Basal Metabolic Rate");
        mChart.getAxisLeft().addLimitLine(ll3);
        ll3.setLineColor(Color.YELLOW);
        ll3.setLineWidth(4f);
        ll3.setTextColor(Color.BLACK);
        ll3.setTextSize(12f);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);

        db.close();

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        //inflater.inflate(R.menu.menu_home, menu);

    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
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
        StatisticsFragment fragment = (StatisticsFragment) getFragmentManager().findFragmentById(R.id.flContent);

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
