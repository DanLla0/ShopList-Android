package com.danlla0.ShopListApp.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    public int id;
    public String name;
    public String telephoneNumber;
    public boolean isSelected;

    //CONSTRUCTORES
    public Contact(String name, String telefoneNumber) {

        this.name = name;
        this.telephoneNumber = telefoneNumber;

    }

    public Contact(int id, String name, String telefoneNumber) {
        this.id = id;
        this.name = name;
        this.telephoneNumber = telefoneNumber;

    }

    public Contact() {


    }
    // GETTERS / SETTERS


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }


    //MÉTODOS PARCELABLE

    protected Contact(Parcel in) {
        name = in.readString();
        telephoneNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(telephoneNumber);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    //OTROS MÉTODOS

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}