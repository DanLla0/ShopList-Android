package com.danlla0.ShopListApp.dto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.danlla0.ShopListApp.Objects.ShopList;
import com.danlla0.ShopListApp.Objects.Product;
import com.danlla0.ShopListApp.R;

import java.util.Comparator;

public class DB {
    public static SQLiteDatabase getDB;

    private static final String LOG_ID = "LOG - DB - ";

    // MÉTODO QUE HACE UNA CONSULTA A LA BASE DE DATOS Y DEVUELVE SI LA LISTA
    // QUE HEMOS PASADO COMO PARÁMETRO EXISTE EN LA BASE DE DATOS O NO
    public static boolean listExist(String listName) {
        Cursor c = DB.getDB.rawQuery("select * from Lists where list_name = '" + listName + "';", null);
        if (c.getCount() != 0) {
            return true;
        } else return false;
    }


    //MÉTODO PARA CARGAR UNOS DATOS DE PRUEBA EN LA BASE DE DATOS
    public static void loadDemoData() {

        //LISTS
        for (int i = 1; i <= 10; i++) {
            String listname = "DemoList" + i;
            DB.getDB.execSQL("insert into Lists(list_name) values ('" + listname + "');");
        }
        //LISTS DETAILS
        //list_id, product_id, product_amount
        //LISTA 1, DemoList1, 4 PRODUCTOS
        DB.getDB.execSQL("insert into ListDetails values (1,5,10);");
        DB.getDB.execSQL("insert into ListDetails values (1,2,20);");
        DB.getDB.execSQL("insert into ListDetails values (1,4,1);");
        DB.getDB.execSQL("insert into ListDetails values (1,7,2);");
        //LISTA 2, DemoList2, 3 PRODUCTOS
        DB.getDB.execSQL("insert into ListDetails values (2,4,12);");
        DB.getDB.execSQL("insert into ListDetails values (2,6,22);");
        DB.getDB.execSQL("insert into ListDetails values (2,5,1);");

        //LISTA 3, DemoList3, 6 PRODUCTOS
        DB.getDB.execSQL("insert into ListDetails values (3,1,12);");
        DB.getDB.execSQL("insert into ListDetails values (3,2,5);");
        DB.getDB.execSQL("insert into ListDetails values (3,3,1);");
        DB.getDB.execSQL("insert into ListDetails values (3,4,2);");
        DB.getDB.execSQL("insert into ListDetails values (3,5,10);");
        DB.getDB.execSQL("insert into ListDetails values (3,6,1);");

        Log.d(LOG_ID + "58", "Carga de datos de prueba completa");

    }

    //MÉTODO PARA ACTUALIZAR LA COLUMNA EN LA QUE APARECE EN NÚMERO LAS VECES
    // QUE UN OBJETO ESTÁ EN LAS LISTAS DE LA COMPRA
    public static void updateTimesInLists() {
        Cursor c = DB.getDB.rawQuery("select product_id from Products", null);
        c.moveToFirst();
        int items = c.getCount();
        for (int i = items; i > 0; i--) {
            String id = i + "";
            Cursor c2 = DB.getDB.rawQuery("select count(*) from ListDetails d where d.product_id = ?", new String[]{id});
            c2.moveToFirst();
            int times_in_lists = c2.getInt(0);
            DB.getDB.execSQL("update Products set product_times_in_lists = ? where product_id = ?", new Object[]{times_in_lists, id});
            c2.close();
        }

        c.close();
        //UNA VEZ ACTUALIZADAS LAS COLUMNAS, PEDIMOS A LA BASE DE DATOS LA INFORMACIÓN Y LA PASAMOS A LOS PRODUCTOS
        Cursor c3 = DB.getDB.rawQuery("select product_id, product_times_in_lists from Products", null);
        while (c3.moveToNext()){
            ProductList.productList.stream().filter(product -> product.getId() == c3.getInt(0)).forEach(product -> product.setTimes_in_lists(c3.getInt(1)));
        };
            ProductList.productList.sort(Comparator.comparing(Product::getTimes_in_lists).reversed());
        try {
            ProductList.myAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(LOG_ID + "87", "Adaptador nulo");
        }
        c3.close();
        Log.d(LOG_ID + "90", "Actualización columna veces que salen los objetos en las listas completa");
    }

    //MÉTODO QUE CARGA EN EL ARRAY DE LAS LISTAS, LAS LISTAS QUE HAY EN LA BASE DE DATOS
    public static void getListHistory() {
        Cursor c = DB.getDB.rawQuery("select * from Lists", null);
        if (c.getCount() != 0) {
            ShopListList.shopListArray.clear();
            while (c.moveToNext()) {
                ShopListList.shopListArray.add(new ShopList(c.getInt(0), c.getString(1), c.getString(2)));
            }
            Log.d(LOG_ID + "101", "Listas Actualizadas.");
        } else {
            Log.d(LOG_ID + "103", "Tabla Lists vacía");
        }
        try {
            ShopListList.myAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(LOG_ID + "108", "Adaptador Nulo");
        }
        c.close();
    }

    //MÉTODO QUE DEVUELVE EL ID DE LA LISTA QUE SE PASA COMO PARÁMETRO
    public static String getListId(String listNameAux) {
        Cursor c = DB.getDB.rawQuery("Select * from Lists where list_name = '" + listNameAux + "';", null);
        if (c.getCount() != 0) {
            c.moveToFirst();
            String listid = c.getString(0);
            c.close();
            return listid;
        } else return "0";
    }


    //MÉTODO PARA CARGAR LOS DATOS DE LA BASE DE DATOS
    public static void getDBData() {
        if (DBChanged()) {
            ProductList.productList.clear();
            Cursor c = DB.getDB.rawQuery("SELECT * FROM Products order by product_times_in_lists desc", null);
            while (c.moveToNext()) {
                byte[] imageBytes = c.getBlob(5);
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ProductList.productList.add(new Product(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3), c.getString(4), imageBitmap));
            }
            Log.d(LOG_ID + "135", "Cargado de Base de Datos");
            c.close();
        }
    }

    //MÉTODO QUE COMPRUEBA SI LOS DATOS DE LA LISTA DE PRODUCTOS Y LOS DE LA BASE DE DATOS SON IGUALES
    private static boolean DBChanged() {
        Cursor c = DB.getDB.rawQuery("select product_id from Products", null);
        if (c.getCount() != ProductList.productList.size()) {
            Log.d(LOG_ID + "144", "Han cambiado los datos");
            return true;
        } else {
            Log.d(LOG_ID + "147", "No hay cambio en los datos");
            return false;
        }

    }


}