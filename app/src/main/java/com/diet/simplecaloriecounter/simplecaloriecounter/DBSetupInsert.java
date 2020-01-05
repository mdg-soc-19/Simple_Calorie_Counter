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
        db.close();/// serving name word =  'teaspoon' or 'plate' or 'cup' or 'tablespoon'
    }

    public void insertAllFood(){
        setupInsertToFood("NULL, 'Apple', 'Farheen Fruits Company, Kolkata, West Bengal', '100', 'gram', '1', 'piece', '59', '56', '1.2', '1.8', '59', '56', '1.2', '1.8', NULL, NULL, '11', NULL, NULL, NULL, NULL, NULL");
        setupInsertToFood("NULL, 'Dal(Cooked)', 'nil', '150', 'ml', '1', 'Katori/Cup', '68', '11', '51', '6', '100', '16', '80', '9', NULL, NULL, '2', NULL, NULL, NULL, NULL, NULL");
        setupInsertToFood("NULL, 'Maa ki Dal', 'nil', '150', 'ml', '1', 'Katori/Cup', '61', '20', '41', '0', '92', '30', '62', '0', NULL, NULL, '3', NULL, NULL, NULL, NULL, NULL");

    }

    public void insertAllCategories(){
        setupInsertToCategories("NULL, 'Pulses and Legumes and their Preparations', '0', '', NULL");
        setupInsertToCategories("NULL, 'Pulses and Legumes', '1', '', NULL");
        setupInsertToCategories("NULL, 'Preparations of Pulses and Legumes', '1', '', NULL");


        setupInsertToCategories("NULL, 'Nuts and Oilseeds', '0', '', NULL");
        setupInsertToCategories("NULL, 'Nuts', '4', '', NULL");
        setupInsertToCategories("NULL, 'Oilseeds', '4', '', NULL");


        setupInsertToCategories("NULL, 'Vegetables Preparations', '0', '', NULL");
        setupInsertToCategories("NULL, 'Vegetable', '7', '', NULL");
        setupInsertToCategories("NULL, 'Preparations of Vegetables', '7', '', NULL");


        setupInsertToCategories("NULL, 'Fruits', '0', '', NULL");
        setupInsertToCategories("NULL, 'Apple and Pears', '10', '', NULL");
        setupInsertToCategories("NULL, 'Citrus  Fruits', '10', '', NULL");
        setupInsertToCategories("NULL, 'Stone Fruit', '10', '', NULL");
        setupInsertToCategories("NULL, 'Tropical and exotic', '10', '', NULL");
        setupInsertToCategories("NULL, 'Berries', '10', '', NULL");
        setupInsertToCategories("NULL, 'Melons', '10', '', NULL");
        setupInsertToCategories("NULL, 'Tomatoes and avocados', '10', '', NULL");



        setupInsertToCategories("NULL, 'Dairy Products', '0', '', NULL");
        setupInsertToCategories("NULL, 'Butter', '18', '', NULL");
        setupInsertToCategories("NULL, 'Cream', '18', '', NULL");
        setupInsertToCategories("NULL, 'Yogurt', '18', '', NULL");
        setupInsertToCategories("NULL, 'Ice Cream', '18', '', NULL");
        setupInsertToCategories("NULL, 'Milk', '18', '', NULL");
        setupInsertToCategories("NULL, 'Cheese', '18', '', NULL");



        setupInsertToCategories("NULL, 'Cereal and Cereal products', '0', '', NULL");
        setupInsertToCategories("NULL, 'Cereals', '25', '', NULL");
        setupInsertToCategories("NULL, 'Their Products', '25', '', NULL");


        setupInsertToCategories("NULL, 'Added Sugars', '0', '', NULL");
        setupInsertToCategories("NULL, 'Honey', '28', '', NULL");
        setupInsertToCategories("NULL, 'Sugar/Jaggery', '28', '', NULL");


        setupInsertToCategories("NULL, 'Soups', '0', '', NULL");
        setupInsertToCategories("NULL, 'Veg Soups', '31', '', NULL");
        setupInsertToCategories("NULL, 'Non Veg Soups', '31', '', NULL");


        setupInsertToCategories("NULL, 'Bread/Cereal Preparations', '0', '', NULL");
        setupInsertToCategories("NULL, 'Breads', '34', '', NULL");
        setupInsertToCategories("NULL, 'Their Preparations', '34', '', NULL");

        setupInsertToCategories("NULL, 'Salad/Chutney/Raita Preparations', '0', '', NULL");
        setupInsertToCategories("NULL, 'Salad/Chutney/Raita Preparations', '37', '', NULL");

        setupInsertToCategories("NULL, 'Meat, Fish and Egg Preparations', '0', '', NULL");
        setupInsertToCategories("NULL, 'Meats', '39', '', NULL");
        setupInsertToCategories("NULL, 'Fishes', '39', '', NULL");
        setupInsertToCategories("NULL, 'Eggs', '39', '', NULL");
        setupInsertToCategories("NULL, 'Their Preparations', '39', '', NULL");

        setupInsertToCategories("NULL, 'Sweets and Desserts', '0', '', NULL");
        setupInsertToCategories("NULL, 'Frozen desserts', '44', '', NULL");
        setupInsertToCategories("NULL, 'Biscuits or cookies', '44', '', NULL");
        setupInsertToCategories("NULL, 'Chocolate and Candies', '44', '', NULL");
        setupInsertToCategories("NULL, 'Custards and Puddings', '44', '', NULL");
        setupInsertToCategories("NULL, 'Pies', '44', '', NULL");
        setupInsertToCategories("NULL, 'Deep Fried Deserts', '44', '', NULL");
        setupInsertToCategories("NULL, 'Sweets', '44', '', NULL");


        setupInsertToCategories("NULL, 'Snacks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Pakodas', '52', '', NULL");
        setupInsertToCategories("NULL, 'Sandwiches', '52', '', NULL");

        setupInsertToCategories("NULL, 'Alcoholic Beverages ', '0', '', NULL");
        setupInsertToCategories("NULL, 'Beer', '55', '', NULL");
        setupInsertToCategories("NULL, 'Gin', '55', '', NULL");
        setupInsertToCategories("NULL, 'Rum', '55', '', NULL");
        setupInsertToCategories("NULL, 'Whiskey', '55', '', NULL");
        setupInsertToCategories("NULL, 'Wine', '55', '', NULL");

        setupInsertToCategories("NULL, 'Cakes And Pastries', '0', '', NULL");
        setupInsertToCategories("NULL, 'Egg', '61', '', NULL");
        setupInsertToCategories("NULL, 'EggLess', '61', '', NULL");

        setupInsertToCategories("NULL, 'Fast Foods', '0', '', NULL");
        setupInsertToCategories("NULL, 'Pizzas', '64', '', NULL");
        setupInsertToCategories("NULL, 'Burgers', '64', '', NULL");
        setupInsertToCategories("NULL, 'Fried Chicken', '64', '', NULL");
        setupInsertToCategories("NULL, 'Hot Dog', '64', '', NULL");
    }
}
