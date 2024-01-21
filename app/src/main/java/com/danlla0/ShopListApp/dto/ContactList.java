package com.danlla0.ShopListApp.dto;

import com.danlla0.ShopListApp.Fragments.Adapters.MyContactRecyclerViewAdapter;
import com.danlla0.ShopListApp.Objects.Contact;

import java.util.ArrayList;

public class ContactList {
    public static ArrayList<Contact> contactList = new ArrayList<>();
    public static ArrayList<Contact> selectedContactList = new ArrayList<>();
    public static MyContactRecyclerViewAdapter myAdapter;
}
