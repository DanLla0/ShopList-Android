package com.danlla0.Practica4_DanielLlamas.dto;

import com.danlla0.Practica4_DanielLlamas.Fragments.Adapters.MyContactRecyclerViewAdapter;
import com.danlla0.Practica4_DanielLlamas.Objects.Contact;

import java.util.ArrayList;

public class ContactList {
    public static ArrayList<Contact> contactList = new ArrayList<>();
    public static ArrayList<Contact> selectedContactList = new ArrayList<>();
    public static MyContactRecyclerViewAdapter myAdapter;
}
