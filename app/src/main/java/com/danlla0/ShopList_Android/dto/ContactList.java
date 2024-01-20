package com.danlla0.ShopList_Android.dto;

import com.danlla0.ShopList_Android.Fragments.Adapters.MyContactRecyclerViewAdapter;
import com.danlla0.ShopList_Android.Objects.Contact;

import java.util.ArrayList;

public class ContactList {
    public static ArrayList<Contact> contactList = new ArrayList<>();
    public static ArrayList<Contact> selectedContactList = new ArrayList<>();
    public static MyContactRecyclerViewAdapter myAdapter;
}
