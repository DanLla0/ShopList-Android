package com.danlla0.ShopListApp.Fragments.Adapters;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.danlla0.ShopListApp.Objects.Contact;
import com.danlla0.ShopListApp.R;
import com.danlla0.ShopListApp.databinding.FragmentContactBinding;
import com.danlla0.ShopListApp.dto.ContactList;

import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link }.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";
    private final List<Contact> mValues;

    public MyContactRecyclerViewAdapter(List<Contact> items) {
        mValues = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentContactBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.txtTlfNumber.setText(mValues.get(position).telephoneNumber);
        holder.txtName.setText(mValues.get(position).name);
        if (!holder.mItem.isSelected())
            holder.cbSelectedContact.setChecked(false);
        else
            holder.cbSelectedContact.setChecked(true);


        //LISTENER PARA PONER A UN CONTACTO EL VALOR DE SELECCIONADO
        View.OnClickListener OnContactClickListener = new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Contact c = mValues.get(holder.getAbsoluteAdapterPosition());
                if (c.isSelected()) {
                    c.setSelected(false);
                    ContactList.selectedContactList.remove(holder.mItem);
                } else {
                    c.setSelected(true);
                    ContactList.selectedContactList.add(holder.mItem);
                }
                ContactList.contactList.sort(Comparator.comparing(Contact::isSelected).reversed());
                try {
                    ContactList.myAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d(LOG_ID + "71", "Adaptador nulo");
                }
            }
        };


        holder.contactLayout.setOnClickListener(OnContactClickListener);
        holder.cbSelectedContact.setOnClickListener(OnContactClickListener);


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtName;
        public final TextView txtTlfNumber;
        public final CheckBox cbSelectedContact;
        public final ConstraintLayout contactLayout;
        public Contact mItem;

        public ViewHolder(FragmentContactBinding binding) {
            super(binding.getRoot());
            txtName = binding.txtContactName;
            txtTlfNumber = binding.txtTlfNumber;
            contactLayout = binding.contactLayout;
            cbSelectedContact = binding.cbSelectedContact;
        }

    }
}