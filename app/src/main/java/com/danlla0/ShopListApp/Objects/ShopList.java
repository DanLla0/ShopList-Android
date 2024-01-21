package com.danlla0.ShopListApp.Objects;

import android.database.Cursor;

import com.danlla0.ShopListApp.dto.DB;

public class ShopList {
    public int id;
    public String name;
    public String date;
    public boolean selected;


    public ShopList(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public ShopList(int id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.selected = false;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return name + " | " + date;
    }

    //MÉTODO QUE DEVUELVE UN STRING CON FORMATO PARA REPRESENTAR LA LISTA DE LA COMPRA
    public String toMessage() {
        //p.product_id - 0,
        // p.product_name - 1,
        // p.product_price - 2,
        // ld.product_amount - 3,
        // list_name - 4,
        // list_id - 5
        Cursor c = DB.getDB.rawQuery("select p.product_id as 'ID-PRODUCTO', p.product_name as 'NOMBRE-PRODUCTO'," +
                " p.product_price as 'PRECIO',ld.product_amount as 'CANTIDAD'," +
                " l.list_name, l.list_id from Products p join ListDetails ld on p.product_id = ld.product_id " +
                "join Lists l on ld.list_id = l.list_id " +
                "where l.list_id = " + this.getId() + " " +
                "order by p.product_id", null);
        String msg = "La lista está vacía.",
                line = "----------------------------------------------------------------------------\n";
        if (c.getCount() != 0) {
            c.moveToFirst();
            msg = c.getString(5) + " - Lista: " + c.getString(4) + "\n";
            msg += line;
            String format = "%-12s %-20s %-15s %-15s %-10s %n";
            msg += String.format(format, c.getColumnName(0), c.getColumnName(1), c.getColumnName(2), c.getColumnName(3), "TOTAL");
            Double total = 0.0d;
            do {
                double totalProductPrice = 0.0d;
                if (c.getString(3).matches("^[0-9]+$"))
                    totalProductPrice = Double.parseDouble(c.getString(2)) * Double.parseDouble(c.getString(3));
                else {
                    String amount = c.getString(3).replaceAll("[^0-9]", "");
                    // Este cálculo es impreciso, se tendría que hacer una comparación con la relación precio / cantidad del objeto,
                    // al ser este caso de uso imaginario hacemos este cálculo de ejemplo.
                    totalProductPrice = (Double.parseDouble(c.getString(2)) * Double.parseDouble(amount)) * 0.5;
                }
                msg += String.format("%-12s %-20s %-15s %-15s %.1f %n", c.getString(0), c.getString(1), c.getString(2), c.getString(3), totalProductPrice);
                total += totalProductPrice;
            } while (c.moveToNext());
            msg += line;
            msg += "Total: " + total + "€";
        }
        c.close();
        return msg;
    }
}
