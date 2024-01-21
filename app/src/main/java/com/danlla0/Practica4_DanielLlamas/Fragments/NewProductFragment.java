package com.danlla0.Practica4_DanielLlamas.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.danlla0.Practica4_DanielLlamas.databinding.FragmentNewProductBinding;
import com.danlla0.Practica4_DanielLlamas.dto.DB;
import com.danlla0.Practica4_DanielLlamas.R;
import com.danlla0.Practica4_DanielLlamas.Objects.Product;
import com.danlla0.Practica4_DanielLlamas.dto.ProductList;
import com.danlla0.Practica4_DanielLlamas.Fragments.ViewModels.SlideshowViewModel;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewProductFragment extends Fragment {
    private static final int CAMERA_IMAGE_INTENT_CODE = 1;
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";
    private FragmentNewProductBinding binding;
    private Bitmap productImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentNewProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.imgNewProduct.setImageResource(R.drawable.baseline_add_a_photo_24);

        binding.imgNewProduct.setOnClickListener(listenerGetImage);
        binding.txtNewProductName.addTextChangedListener(listenerInputText);
        binding.txtNewProductDescription.addTextChangedListener(listenerInputText);
        binding.txtNewProductPrice.addTextChangedListener(listenerInputText);
        binding.btnCreateNewProduct.setOnClickListener(listenerCreateProduct);
        return root;
    }

    //MÉTODO QUE INICIALIZA LOS CAMPOS DE LA VENTANA
    public void initializeProductForm() {
        binding.txtNewProductName.setText("");
        binding.txtNewProductDescription.setText("");
        binding.txtNewProductPrice.setText("");
        binding.imgNewProduct.setImageResource(R.drawable.baseline_add_a_photo_24);
    }


    //MÉTODO PARA TRANSFORMAR UNA CADENA A UNA
    // CON LA PRIMERA LETRA MAYÚSCULA ÚNICAMENTE
    public String capitalizeFirstChar(String word) {
        if (word.length() > 1)
            return (word.toLowerCase().substring(0, 1).toUpperCase() + word.toLowerCase().substring(1));
        else
            return word.toUpperCase();
    }

    //MÉTODO PARA COMPROBAR QUE LOS DATOS QUE QUEREMOS
    // INTRODUCIR EN LA BASE DE DATOS NO ESTEN REPETIDOS
    private boolean productExists() {
        Cursor c = DB.getDB.rawQuery("select product_name from Products where product_name = '" + capitalizeFirstChar(binding.txtNewProductName.getText().toString()) + "';", null);
        if (c.getCount() != 0)
            return true;
        else
            return false;

    }


    //MÉTODO PARA CONSEGUIR EL NOMBRE DE LA IMAGEN
    private String getImgName(String name, int id) {
        String auxImgName = "";
        if (id < 10)
            auxImgName = name.replace(" ", "").toLowerCase() + "0" + id + ".png";
        else
            auxImgName = name.replace(" ", "").toLowerCase() + id + ".png";
        return auxImgName;
    }



// LISTENER PARA AÑAIDIR EL PRODUCTO EN LA BASE DE DATOS
    View.OnClickListener listenerCreateProduct = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    if (!productExists()) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (productImage != null)
                            productImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteImage = stream.toByteArray();
                        String name = capitalizeFirstChar(binding.txtNewProductName.getText().toString()),
                                description = capitalizeFirstChar(binding.txtNewProductDescription.getText().toString());
                        double price = Double.parseDouble(binding.txtNewProductPrice.getText().toString());


                        Cursor c = DB.getDB.rawQuery("select max(product_id) from Products order by product_id", null);
                        c.moveToFirst();
                        int id = c.getInt(0) + 1;
                        c.close();
                        String insertQuery = "INSERT INTO Products(product_name, product_description, product_price,product_image_name,product_image) VALUES('" +
                                name + "', '" +       //name
                                description + "', " +//description
                                price + ", '" +     //price
                                getImgName(name, id) + "', " + // image_name
                                "?);";            //image
                        DB.getDB.execSQL(insertQuery, new Object[]{byteImage});

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Producto creado correctamente", Toast.LENGTH_LONG).show();
                                ProductList.productList.add(new Product(id, name, description, price, productImage));
                                initializeProductForm();
                                try {
                                    ProductList.myAdapter.notifyDataSetChanged();
                                }catch (Exception e){
                                    Log.d(LOG_ID+"140", getString(R.string.null_adapter_text));
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), R.string.product_exists_text, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
    //LISTENER QUE CREA LA ACTIVIDAD / INTENT DE ABRIR LA CÁMARA
    //CUANDO PULSAMOS EL ELEMENTO DE LA IMAGEN

    View.OnClickListener listenerGetImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent getCameraImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(getCameraImageIntent, CAMERA_IMAGE_INTENT_CODE);
        }
    };
    //MÉTODO QUE SE EJECUTA CUANDO LA ACTIVIDAD / INTENT ACABA Y DONDE OPERAMOS CON LA IMAGEN HECHA
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_INTENT_CODE) {
            try {
                productImage = (Bitmap) data.getExtras().get("data");
                binding.imgNewProduct.setImageBitmap(productImage);
            } catch (Exception e) {
                Log.d(LOG_ID + "170", "No se ha hecho foto al objeto.");
            }
        }
    }

    //LISTENER QUE COMPRUEBA CADA VEZ QUE DAMOS UN INPUT DE TEXTO EN UN OBJETO
    //Y ACTIVA EL BOTÓN DE CREAR SI LOS CAMPOS NO ESTÁN VACÍOS
    TextWatcher listenerInputText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            buttonEnableChecker();

        }
    };


    // SI TODOS LOS CAMPOS DE TEXTO ESTÁN VACÍOS EL BOTÓN ESTÁ DESACTIVADO
    private void buttonEnableChecker() {
        if (!binding.txtNewProductName.getText().toString().equals("") &&
                !binding.txtNewProductDescription.getText().toString().equals("") &&
                !binding.txtNewProductPrice.getText().toString().equals(""))
        {
            binding.btnCreateNewProduct.setEnabled(true);
            binding.btnCreateNewProduct.setBackgroundResource(R.drawable.fade_orange_color);

        } else {
            binding.btnCreateNewProduct.setEnabled(false);
            binding.btnCreateNewProduct.setBackgroundResource(R.color.gray_400);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}