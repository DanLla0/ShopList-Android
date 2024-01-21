package com.danlla0.ShopListApp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.danlla0.ShopListApp.dto.DB;
import com.danlla0.ShopListApp.R;
import com.danlla0.ShopListApp.databinding.FragmentHomeBinding;
import com.danlla0.ShopListApp.NewListActivity;
import com.danlla0.ShopListApp.Fragments.ViewModels.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.edListNameMain.addTextChangedListener(edListNameMainListener);
        binding.btnCreate.setOnClickListener(btnCreateListener);

        return root;
    }

    //MÉTODO QUE CREA Y LANZA LA ACTIVITY DE CREAR UNA NUEVA LISTA

    public void setNewListActivityIntent() {
        Intent NewListActivityIntent = new Intent(getActivity(), NewListActivity.class);
        NewListActivityIntent.putExtra("list-name", binding.edListNameMain.getText().toString());
        startActivity(NewListActivityIntent);
        binding.edListNameMain.setText("");
    }

    //LISTENER PARA EL BOTON DE CREAR LISTA, COMPRUEBA QUE EL NOMBRE DE LA LISTA NO EXISTA
    View.OnClickListener btnCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!DB.listExist(binding.edListNameMain.getText().toString()))
                setNewListActivityIntent();
            else
                Toast.makeText(getContext(), R.string.listExistText, Toast.LENGTH_LONG).show();


        }
    };

    //LISTENER PARA EL CAMPO DE TEXTO DEL NOMBRE DE LA LISTA,
    //COMPRUEBA QUE EL NOMBRE DE LA LISTA / TEXTO QUE HEMOS INTRODUCIDO EN EL EDITTEXT
    //NO ESTE VACÍO Y ACTIVA EL BOTÓN
    TextWatcher edListNameMainListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0)
                setButtonSettings(true, R.drawable.fade_orange_color);
            else
                setButtonSettings(false, R.color.gray_400);
        }
    };

    private void setButtonSettings(boolean enableMode, int style) {
        binding.btnCreate.setEnabled(enableMode);
        binding.btnCreate.setBackgroundResource(style);
    }

}