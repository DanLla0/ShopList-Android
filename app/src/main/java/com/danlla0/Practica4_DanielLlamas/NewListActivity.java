package com.danlla0.Practica4_DanielLlamas;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.danlla0.Practica4_DanielLlamas.dto.DB;
import com.danlla0.Practica4_DanielLlamas.Objects.Product;
import com.danlla0.Practica4_DanielLlamas.dto.ProductList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewListActivity extends AppCompatActivity {

    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";

    private final static int ALL_NULL_CODE = 0;
    private final static int VALID_LIST_CODE = 1;
    private final static int INVALID_LIST_CODE = 2;
    private final static int EMPTY_FIELDS_CODE = 3;
    public static String listName;
    public static String listId;
    private EditText edName;
    private Button btnNewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        DB.getDBData();
        Intent intent = getIntent();
        listName = intent.getStringExtra("list-name");
        edName = findViewById(R.id.ed_ListName);
        btnNewList = findViewById(R.id.btnNewList);
        edName.setText(listName);
        btnNewList.setOnClickListener(btnNewListListener);
    }




    //MÉTODO QUE ACTUALIZA / INSERTA LA LISTA QUE HEMOS CREADO / MODIFICADO, EN LA BASE DE DATOS
    private void insertQuery() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Trabajo en Background aquí

                Cursor c = DB.getDB.rawQuery("Select * from Lists where list_id = " + listId + ";", null);
                String query = "";

                if (c.getCount() == 0) {
                    query = "insert into Lists(list_name) values('" + listName + "');";
                } else {
                    c.moveToFirst();
                    query = "update Lists set list_name = '" + listName + "' where list_id = " + listId + ";";
                }
                c.close();
                DB.getDB.execSQL(query);
                insertDetails();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Trabajo en la interfaz de usuario aquí
                        Toast.makeText(getApplicationContext(), R.string.successListInsert, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    //MÉTODO QUE LANZA UN HILO ASÍNCRONO QUE INSERTA LOS DATOS LA COMPRA EN LA BASE DE DATOS
    private void insertDetails() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Trabajo en Background aquí
                String listId = DB.getListId(listName);
                DB.getDB.execSQL("delete from ListDetails where list_id = " + listId + ";");
                for (Product p : ProductList.productList) {
                    if (p.getAmount() != null) {
                        if (!p.getAmount().equals("")) {
                            //INSERTAR LOS DATOS DE LOS DETALLES DE LA COMPRA
                            DB.getDB.execSQL("insert into ListDetails values(?,?,'" + p.getAmount() + "');", new Object[]{listId, p.getId()});

                        }
                    }
                }
                DB.updateTimesInLists();
                DB.getListHistory();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Trabajo en la interfaz de usuario aquí
                        Log.d(LOG_ID + " 113", "Datos actualizados correctamente.");
                        newList();
                    }
                });
            }
        });

    }

//MÉTODO QUE COMPRUEBA EL EDITTEXT DEL NOMBRE DE LA LISTA, NO ESTÉ VACIO
    public boolean emptyName() {
        if (edName.getText().toString().equals(""))
            return true;
        else
            return false;

    }

    //MÉTODO QUE INICIALIZA EL EDITTEXT DEL NOMBRE JUNTO CON LAS CANTIDADES DE LOS PRODUCTOS
    public void newList() {
        try {
            edName.setText("");
            Log.d(LOG_ID+ "135", "Valores de los productos cambiados");
            ProductList.productList.forEach(x -> x.setAmount(null));
            ProductList.myAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(LOG_ID + "139", "Adaptador ProductList Nulo");
        }
    }

    //MÉTODO QUE EXAMINA LOS DATOS QUE HAY INTRODUCIDOS EN LA LISTA DE PRODUCTOS Y DEVUELVE UN CÓDIGO
    //SEGÚN LOS DATOS QUE HAY EN ELLA
    private int getCodeValue() {
        int code = -1;
        if (!emptyName()) {
            for (Product p : ProductList.productList) {
                if (p.getAmount() != null && !p.getAmount().equals("")) {
                    if (p.getAmount().trim().matches("^[\\s\\d]+.*$"))
                        //ESTA EXPRESIÓN REGEX COMPRUEBA QUE EL TEXTO QUE TIENE LA CANTIDAD DEL PRODUCTO
                        //EMPIEZE POR NÚMERO.
                        code = VALID_LIST_CODE;
                    else
                        return INVALID_LIST_CODE;
                } else if (code != VALID_LIST_CODE)
                    code = ALL_NULL_CODE;
            }
        } else return EMPTY_FIELDS_CODE;
        return code;
    }

    //LISTENER PARA CUANDO HACEMOS CLICK EN EL BOTÓN DE CREAR LISTA
    View.OnClickListener btnNewListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (getCodeValue()) {
                case ALL_NULL_CODE:
                    Toast.makeText(getApplicationContext(), R.string.allProductNull, Toast.LENGTH_SHORT).show();
                    break;
                case VALID_LIST_CODE:
                    listId = DB.getListId(listName);
                    listName = edName.getText().toString();
                    insertQuery();
                    finish();
                    break;
                case INVALID_LIST_CODE:
                    Toast.makeText(getApplicationContext(), R.string.badAmountInput, Toast.LENGTH_SHORT).show();
                    break;
                case EMPTY_FIELDS_CODE:
                    Toast.makeText(getApplicationContext(), R.string.emptyListText, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.d(LOG_ID + "184", "Código de error sin contemplar");
            }
        }
    };


}