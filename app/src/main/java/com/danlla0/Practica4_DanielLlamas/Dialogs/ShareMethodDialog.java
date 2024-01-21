package com.danlla0.Practica4_DanielLlamas.Dialogs;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.danlla0.Practica4_DanielLlamas.Objects.Alarm;
import com.danlla0.Practica4_DanielLlamas.Objects.Contact;
import com.danlla0.Practica4_DanielLlamas.Objects.ShopList;
import com.danlla0.Practica4_DanielLlamas.dto.ContactList;
import com.danlla0.Practica4_DanielLlamas.R;


public class ShareMethodDialog extends DialogFragment {
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";
    private ShopList selectedList;
    Context context;


    public ShareMethodDialog(ShopList selectedList, Context context) {
        this.selectedList = selectedList;
        this.context = context;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("¿Quieres compartir la lista ahora?");
        builder.setNegativeButton("Programar envío", scheduleDate);
        builder.setPositiveButton("Enviar ahora", positiveButtonListener);
        return builder.create();
    }



    //LISTENER PARA "ENVIAR AHORA"
    DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            sendMessage();
            dialog.cancel();
        }
    };
    //INICIA EL INTENT DE ENVIAR LA LISTA POR WHATSAPP PARA CADA CONTACTO SELECCIONADO
    private void sendMessage() {
        for (Contact contact : ContactList.selectedContactList) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", contact.getTelephoneNumber(), selectedList.toMessage()))));
        }

    }


    //LISTENER PARA "PROGRAMAR ENVÍO"
    DialogInterface.OnClickListener scheduleDate = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (view.isShown()) {

                        for (Contact contact : ContactList.selectedContactList) {
                            int id = context.getSharedPreferences("alarms-preferences", Context.MODE_PRIVATE).getAll().size() + 1;
                            System.out.println(selectedList.toMessage());
                            Alarm newAlarm = new Alarm(id, contact, hourOfDay, minute, selectedList.toMessage());
                            newAlarm.setAlarm(context, true);
                        }

                        Toast.makeText(context, "El envío se ha programado correctamente.", Toast.LENGTH_SHORT).show();
                        restartSelectedContacts();
                    }
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, 12, 30, true);
            timePickerDialog.setTitle("Elige la hora: ");
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
            dialog.cancel();
        }
    };


    //MÉTODO PARA DESELECCIONAR LOS CONTACTOS DE LA LISTA
    private void restartSelectedContacts() {
        int position = 0;
        while(position < ContactList.selectedContactList.size()) {
            ContactList.contactList.get(position++).setSelected(false);
        }
        try {
            ContactList.myAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(LOG_ID + "108", getString(R.string.null_adapter_text));
        }
    }


}

