package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FoodCursorAdapter extends CursorAdapter {

    public FoodCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_food_list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textViewListName = view.findViewById(R.id.textViewListName);
        TextView textViewListNumber = view.findViewById(R.id.textViewListNumber);
        TextView textViewListSubName = view.findViewById(R.id.textViewListSubName);


        int getID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String getName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));

        String getManufactorName = cursor.getString(cursor.getColumnIndexOrThrow("food_manufactor_name"));
        String getDescription = cursor.getString(cursor.getColumnIndexOrThrow("food_description"));
        String getServingSize = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size"));
        String getServingMesurment = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_measurement"));
        String getServingNameNumber = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_name_number"));
        String getServingNameWord = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_name_word"));
        int getEnergyCalculated = cursor.getInt(cursor.getColumnIndexOrThrow("food_energy_calculated"));

        String subLine = getManufactorName + ", " +
                getServingSize + " " +
                getServingMesurment + ", " +
                getServingNameNumber + " " +
                getServingNameWord;


        textViewListName.setText(getName);
        textViewListNumber.setText(String.valueOf(getEnergyCalculated));
        textViewListSubName.setText(subLine);

    }
}
