package com.diet.simplecaloriecounter.simplecaloriecounter;

import android.content.Context;

public class DBSetupInsert {

    private final Context context;

    public DBSetupInsert(Context ctx) {
        this.context = ctx;
    }


    public void setupInsertToCategories(String values){
        DBAdapter db = new DBAdapter(context);
        db.open();

        db.insert("categories" ,"category_id, category_name, category_parent_id, category_icon, category_note", values);
        db.close();
    }


    public void setupInsertToFood(String values){
        DBAdapter db = new DBAdapter(context);
        db.open();

        db.insert("food" ,"food_id, food_name, food_manufactor_name, food_serving_size, food_serving_measurement, food_serving_name_number, food_serving_name_word, food_energy, food_proteins, food_carbohydrates, food_fats, food_energy_calculated, food_proteins_calculated, food_carbohydrates_calculated, food_fats_calculated, food_user_id, food_barcode, food_category_id, food_thumb, food_image_a, food_image_b, food_image_c, food_note", values);
        db.close();
    }

    public void insertAllFood(){
        setupInsertToFood("NULL, 'Milk','AMUL',NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL");
        setupInsertToFood("NULL, 'Egg, whole ,cooked, hard boiled ', 'AMUL', '136.0','g', '211.0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL");
    }

    public void insertAllCategories(){
        setupInsertToCategories("NULL, 'Drinks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Eggs', '1', '', NULL");
    }
}
