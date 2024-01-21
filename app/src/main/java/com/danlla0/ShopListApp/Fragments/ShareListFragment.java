package com.danlla0.ShopListApp.Fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.danlla0.ShopListApp.Dialogs.ShareMethodDialog;
import com.danlla0.ShopListApp.dto.ContactList;
import com.danlla0.ShopListApp.R;
import com.danlla0.ShopListApp.databinding.FragmentShareListBinding;
import com.danlla0.ShopListApp.Objects.ShopList;
import com.danlla0.ShopListApp.dto.ShopListList;
import com.danlla0.ShopListApp.Fragments.ViewModels.ShareListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShareListFragment extends Fragment {
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";

    private ShareListViewModel mViewModel;
    private FragmentShareListBinding binding;
    private ShopList selectedList;


    public static ShareListFragment newInstance() {
        return new ShareListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShareListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.listSpinner.setAdapter(new myCustomAdapter(getContext(), R.layout.fragment_listhistory, ShopListList.shopListArray));
        binding.btnShareList.setOnClickListener(btnShareListListener);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ShareListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    //LISTENER QUE CREA LA ACTIVIDAD DE COMPARTIR LA LISTA POR WHATSAPP PARA CADA CONTACTO SELECCIONADO
    View.OnClickListener btnShareListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ContactList.selectedContactList.size()!=0){
                selectedList = (ShopList) binding.listSpinner.getSelectedItem();
                ShareMethodDialog shareMethodDialog = new ShareMethodDialog(selectedList,getActivity().getApplicationContext());
                shareMethodDialog.show(getParentFragmentManager(),"alarm-time-pìcker-dialog");
            }else{
                Toast.makeText(getContext(), R.string.emptySelectedContactList, Toast.LENGTH_SHORT).show();
            }
        }
    };


    public class myCustomAdapter extends ArrayAdapter<ShopList> {
        private ArrayList<ShopList> myLists;

        public myCustomAdapter(@NonNull Context context, int resource, @NonNull List<ShopList> objects) {
            super(context, resource, objects);
            myLists = (ArrayList<ShopList>) objects;
        }

        public myCustomAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createCustomRow(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createCustomRow(position, convertView, parent);
        }

        private View createCustomRow(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myRow = layoutInflater.inflate(R.layout.fragment_listhistory, parent, false);

            TextView txtDataList = myRow.findViewById(R.id.txtDataList);
            txtDataList.setText(myLists.get(position).toString());

            return myRow;
        }

    }
}