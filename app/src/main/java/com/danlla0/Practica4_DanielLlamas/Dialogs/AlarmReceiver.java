package com.danlla0.Practica4_DanielLlamas.Dialogs;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.danlla0.Practica4_DanielLlamas.Objects.Contact;

public class AlarmReceiver extends BroadcastReceiver {
//MÉTDODO QUE SE EJECUTA CUANDO SE EJECUTA UNA ALARMA, CREA EL INTENT DE ENVIAR POR WHATSAPP
    // Y ELIMINA LA ALARMA QUE LO LLAMA DE LAS PREFERENCIAS DE LA APLICACIÓN
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("ShareList.alarm")) {
            SharedPreferences myPreferences = context.getSharedPreferences("alarms-preferences", context.MODE_PRIVATE);
            String alarmID = intent.getStringExtra("alarm-id");
            String msg = intent.getStringExtra("message");
            Contact contact = intent.getParcelableExtra("contact");
            String tlfNumber = contact.getTelephoneNumber();
            Intent shareOnWhatsAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", tlfNumber, msg)));
            shareOnWhatsAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            myPreferences.edit().remove(alarmID).apply();
            context.startActivity(shareOnWhatsAppIntent);
        }
    }
}