package com.danlla0.Practica4_DanielLlamas.Objects;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.danlla0.Practica4_DanielLlamas.Dialogs.AlarmReceiver;

import java.util.Calendar;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private Contact contact;
    private String message;

    //CONSTRUCTORES

    public Alarm(int id, Contact contact, int hour, int minute, String message) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.contact = contact;
        this.message = message;
    }

    public Alarm(int hour, int minute, Contact contact, String message) {
        this.hour = hour;
        this.minute = minute;
        this.contact = contact;
        this.message = message;
    }

//GETTERS / SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //OTROS MÉTODOS

    @Override
    public String toString() {
        return "Alarm{" +
                "id= " + id +
                ",hour=" + hour +
                ", minute=" + minute +
                ", contact=" + contact +
                ", message='" + message + '\'' +
                '}';
    }


    public String toAlarmCode() {
        // code like -> id, id-contacto, hora, minuto, mensaje / id-lista
        String alarmCodeAux = this.id + ";" +
                this.contact.getId() + ";" +
                this.hour + ";" +
                this.minute + ";" +
                this.message;
        return alarmCodeAux;
    }

    public void setAlarm(Context context, boolean isNewAlarm) {
        String alarmId = "alarm" + this.id;
        if (isNewAlarm) {
            SharedPreferences myPreferences = context.getSharedPreferences("alarms-preferences", Context.MODE_PRIVATE);
            String alarmCode = this.toAlarmCode();
            myPreferences.edit().putString(alarmId, alarmCode).apply();
            Log.d("LOG - Alarm - 112", "Alarma añadida a la configuración.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, this.hour);
        calendar.set(Calendar.MINUTE, this.minute);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("ShareList.alarm");
        intent.putExtra("message", this.message);
        intent.putExtra("contact", this.contact);
        intent.putExtra("alarm-id", alarmId);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }

}

