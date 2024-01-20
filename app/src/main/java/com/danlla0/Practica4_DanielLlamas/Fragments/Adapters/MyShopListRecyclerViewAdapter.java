package com.danlla0.Practica4_DanielLlamas.Fragments.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.danlla0.Practica4_DanielLlamas.Objects.Product;
import com.danlla0.Practica4_DanielLlamas.databinding.FragmentShoplistBinding;


import java.util.List;

public class MyShopListRecyclerViewAdapter extends RecyclerView.Adapter<MyShopListRecyclerViewAdapter.ViewHolder> {

    private final List<Product> mValues;

    public MyShopListRecyclerViewAdapter(List<Product> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentShoplistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.txtName.setText(mValues.get(position).getName());
        holder.txtDescription.setText(mValues.get(position).getDescription());
        String priceText = mValues.get(position).getPrice() + "€";
        holder.txtPrice.setText(priceText);
        holder.imgProduct.setImageBitmap(mValues.get(position).getImgProduct());
        holder.edAmont.setText(mValues.get(position).getAmount());


        //LISTENER PARA EL EDITTEXT DE LA CANTIDAD, QUE ACTUALIZA LA CANTIDAD QUE ESTAMOS ESCRIBIENDO,
        //EN EL PRODUCTO QUE ESTAMOS MODIFICANDO.
        holder.edAmont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                mValues.get(holder.getAbsoluteAdapterPosition()).setAmount(holder.edAmont.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imgProduct;
        public final TextView txtName;
        public final TextView txtDescription;
        public final TextView txtPrice;
        public final EditText edAmont;

        public Product mItem;

        public ViewHolder(FragmentShoplistBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            txtName = binding.txtName;
            txtDescription = binding.txtDescription;
            txtPrice = binding.txtPrice;
            edAmont = binding.edAmount;

        }

    }
}