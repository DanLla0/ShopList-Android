package com.danlla0.ShopListApp.Fragments.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danlla0.ShopListApp.Objects.ShopList;

import com.danlla0.ShopListApp.R;
import com.danlla0.ShopListApp.databinding.FragmentListhistoryBinding;
import com.danlla0.ShopListApp.dto.ShopListList;

import java.util.List;

public class MyListHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyListHistoryRecyclerViewAdapter.ViewHolder> {
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";
    private final List<ShopList> mValues;
    private onListSelected onListSelection;
    private String listName = "";


    public MyListHistoryRecyclerViewAdapter(List<ShopList> items) {
        mValues = items;
    }

    public MyListHistoryRecyclerViewAdapter(List<ShopList> items, onListSelected onListSelection) {
        mValues = items;
        this.onListSelection = onListSelection;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentListhistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.txtDataList.setText(mValues.get(position).toString());
        if (holder.mItem.isSelected())
            holder.txtDataList.setTextColor(Color.GREEN);
        else
            holder.txtDataList.setTextColor(Color.BLACK);

        holder.txtDataList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopList list = mValues.get(holder.getAbsoluteAdapterPosition());
                list.setSelected(true);
                ShopListList.shopListArray.stream().filter(shoplist -> shoplist.getId() != list.getId()).forEach(shoplist -> shoplist.setSelected(false));
                listName = mValues.get(holder.getAbsoluteAdapterPosition()).name;
                try {
                    ShopListList.myAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d(LOG_ID+"67", "Adaptador nulo");
                }
                onListSelection.selection(listName);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtDataList;
        public ShopList mItem;

        public ViewHolder(FragmentListhistoryBinding binding) {
            super(binding.getRoot());
            txtDataList = binding.txtDataList;
        }

    }

    public interface onListSelected {
        @NonNull
        Dialog onCreateDialog(@Nullable Bundle savedInstanceState);

        void selection(String listNameAux);
    }

}