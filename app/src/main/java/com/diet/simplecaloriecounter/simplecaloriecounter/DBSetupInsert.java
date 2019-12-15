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
        setupInsertToCategories("NULL, 'Bread', '0', '', NULL");
        setupInsertToCategories("NULL, 'Bread', '1', '', NULL");
        setupInsertToCategories("NULL, 'Cereals', '1', '', NULL");
        setupInsertToCategories("NULL, 'Frozen bread and rolls', '1', '', NULL");
        setupInsertToCategories("NULL, 'Crispbread', '1', '', NULL");


        setupInsertToCategories("NULL, 'Dessert and baking', '0', '', NULL");
        setupInsertToCategories("NULL, 'Baking', '2', '', NULL");
        setupInsertToCategories("NULL, 'Biscuit', '2', '', NULL");


        setupInsertToCategories("NULL, 'Drinks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Soda', '3', '', NULL");


        setupInsertToCategories("NULL, 'Fruit and vegetables', '0', '', NULL");
        setupInsertToCategories("NULL, 'Frozen fruits and vegetables', '4', '', NULL");
        setupInsertToCategories("NULL, 'Fruit', '4', '', NULL");
        setupInsertToCategories("NULL, 'Vegetables', '4', '', NULL");
        setupInsertToCategories("NULL, 'Canned fruits and vegetables', '4', '', NULL");


        setupInsertToCategories("NULL, 'Health', '0', '', NULL");
        setupInsertToCategories("NULL, 'Meal substitutes', '5', '', NULL");
        setupInsertToCategories("NULL, 'Protein bars', '5', '', NULL");
        setupInsertToCategories("NULL, 'Protein powder', '5', '', NULL");


        setupInsertToCategories("NULL, 'Meat, chicken and fish', '0', '', NULL");
        setupInsertToCategories("NULL, 'Meat', '6', '', NULL");
        setupInsertToCategories("NULL, 'Chicken', '6', '', NULL");
        setupInsertToCategories("NULL, 'Seafood', '6', '', NULL");


        setupInsertToCategories("NULL, 'Dairy and eggs', '0', '', NULL");
        setupInsertToCategories("NULL, 'Eggs', '7', '', NULL");
        setupInsertToCategories("NULL, 'Cream and sour cream', '7', '', NULL");
        setupInsertToCategories("NULL, 'Yogurt', '7', '', NULL");


        setupInsertToCategories("NULL, 'Dinner', '0', '', NULL");
        setupInsertToCategories("NULL, 'Ready dinner dishes', '8', '', NULL");
        setupInsertToCategories("NULL, 'Pizza', '8', '', NULL");
        setupInsertToCategories("NULL, 'Noodle', '8', '', NULL");
        setupInsertToCategories("NULL, 'Pasta', '8', '', NULL");
        setupInsertToCategories("NULL, 'Rice', '8', '', NULL");
        setupInsertToCategories("NULL, 'Taco', '8', '', NULL");


        setupInsertToCategories("NULL, 'Cheese', '0', '', NULL");
        setupInsertToCategories("NULL, 'Cream cheese', '9', '', NULL");


        setupInsertToCategories("NULL, 'On bread', '0', '', NULL");
        setupInsertToCategories("NULL, 'Cold meats', '10', '', NULL");
        setupInsertToCategories("NULL, 'Sweet spreads', '10', '', NULL");
        setupInsertToCategories("NULL, 'Jam', '10', '', NULL");


        setupInsertToCategories("NULL, 'Snacks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Nuts', '11', '', NULL");
        setupInsertToCategories("NULL, 'Potato chips', '11', '', NULL");
    }
}
