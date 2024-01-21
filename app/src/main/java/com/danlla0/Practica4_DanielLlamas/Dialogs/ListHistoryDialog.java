package com.danlla0.Practica4_DanielLlamas.Dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.danlla0.Practica4_DanielLlamas.R;
import com.danlla0.Practica4_DanielLlamas.dto.DB;
import com.danlla0.Practica4_DanielLlamas.Fragments.Adapters.MyListHistoryRecyclerViewAdapter;
import com.danlla0.Practica4_DanielLlamas.dto.ProductList;
import com.danlla0.Practica4_DanielLlamas.NewListActivity;
import com.danlla0.Practica4_DanielLlamas.dto.ShopListList;


public class ListHistoryDialog extends DialogFragment implements MyListHistoryRecyclerViewAdapter.onListSelected {
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName()+ " - ";
    public static String listName;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ShopListList.myAdapter = new MyListHistoryRecyclerViewAdapter(ShopListList.shopListArray, this);
        View v = getLayoutInflater().inflate(R.layout.listhistory_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Historial de Listas: ");
        builder.setView(v);
        builder.setNegativeButton("Cancelar", negativeButtonListener);
        builder.setPositiveButton("Aceptar", positiveButtonListener);
        return builder.create();
    }


    //MÉTODO PARA INICIAR EL INTENT DE CREAR UNA NUEVA LISTA
    public void setNewListActivityIntent() {
        Intent NewListActivityIntent = new Intent(getActivity(), NewListActivity.class);
        NewListActivityIntent.putExtra("list-name", listName);
        startActivity(NewListActivityIntent);
    }

    //MÉTODO QUE HACE UNA CONSULTA A LA BASE DE DATOS Y
    //CARGA LOS DATOS DE LA LISTA SELECCIONADA PARA CONSULTAR EN EL ARRAY DE OBJETOS
    public void getListDetails(String listName) {
        String query = "SELECT p.product_id, d.product_amount FROM Products p join ListDetails d on p.product_id = d.product_id where d.list_id in (select list_id from Lists where list_name = '" + listName + "') order by product_times_in_lists desc;";
        Cursor c = DB.getDB.rawQuery(query, null);
        if (c.getCount() == 0) {
            Log.d(LOG_ID + " 58", "La lista no existe.");
        } else {
            //CARGAR LOS DATOS DE LA BASE DE DATOS
            while (c.moveToNext()) {
                int cursorId = c.getInt(0);
                ProductList.productList.stream().filter(x -> x.getId() == cursorId).forEach(x -> x.setAmount(c.getString(1)));
                try {
                    ProductList.myAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d(LOG_ID + "67", getString(R.string.null_adapter_text));
                }
            }
            Log.d(LOG_ID + "70", "Cargado de Base de Datos");
        }
        c.close();

    }
    // LISTENER PARA EL BOTÓN "ACEPTAR"
    DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            getListDetails(listName);
            setNewListActivityIntent();
            dialog.cancel();
        }
    };
    //LISTENER PARA EL BOTÓN "CANCELAR"
    DialogInterface.OnClickListener negativeButtonListener = (dialog, which) -> dialog.cancel();
    //ESTA INTERFAZ ACTUALIZA EL NOMBRE DE LA LISTA QUE HEMOS SELECCIONADO EN EL ARRAY DE LAS LISTAS
    @Override
    public void selection(String listNameAux) {
        listName = listNameAux;
    }
}

