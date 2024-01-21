package com.danlla0.Practica4_DanielLlamas;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.danlla0.Practica4_DanielLlamas.Dialogs.ListHistoryDialog;
import com.danlla0.Practica4_DanielLlamas.Objects.Alarm;
import com.danlla0.Practica4_DanielLlamas.Objects.Contact;
import com.danlla0.Practica4_DanielLlamas.databinding.ActivityMainBinding;
import com.danlla0.Practica4_DanielLlamas.dto.ContactList;
import com.danlla0.Practica4_DanielLlamas.dto.DB;
import com.danlla0.Practica4_DanielLlamas.Objects.Product;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int CONTACT_PERMISSION = 1;
    private final String LOG_ID = "LOG - " + this.getClass().getSimpleName().toString() + " - ";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    Handler handler;
    ExecutorService executor;


    //ON CREATE / MAIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_newList, R.id.nav_checkLists, R.id.nav_newProduct, R.id.nav_shareList)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //INICIAMOS LA APLICACIÓN UNA VEZ QUE HA CARGADO EL LAYOUT.
        DrawerLayout mainActivity_layout = findViewById(R.id.drawer_layout);
        mainActivity_layout.post(new Runnable() {
            @Override
            public void run() {
                DB.getDB = getApplicationContext().openOrCreateDatabase("shopListDB", Context.MODE_PRIVATE, null);
                executor = Executors.newSingleThreadExecutor();
                handler = new Handler(Looper.getMainLooper());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        createBD();
                        checkPermissions();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                DB.updateTimesInLists();
                                DB.getListHistory();
                            }
                        });
                    }
                });
                //LISTENER QUE INVOCA EL DIÁLOGO CUANDO PULSAMOS CONSULTAR LISTAS
                findViewById(R.id.nav_checkLists).setOnClickListener(navCheckListListener);
            }
        });

    }

    //MÉTODO PARA CARGAR LAS ALARMAS QUE TIENE ESTABLECIDAS LA APLICACIÓN
    public void loadAlarms() {
        SharedPreferences myPreferences = getSharedPreferences("alarms-preferences", MODE_PRIVATE);
        HashMap<String, String> alarms = (HashMap<String, String>) myPreferences.getAll();
        if (alarms.size() > 0) {
            for (int i = 1; i <= alarms.size(); i++) {
                String alarmID = "alarm" + i;
                if (alarms.containsKey(alarmID)) {
                    String[] values = alarms.get(alarmID).toString().split(";");
                    int contactID = Integer.parseInt(values[1]);
                    Contact contact = ContactList.contactList.stream().filter(contactAux -> contactAux.getId() == contactID).findFirst().get();
                    if (contact != null) {
                        int id = Integer.parseInt(values[0]);
                        int hour = Integer.parseInt(values[2]);
                        int minute = Integer.parseInt(values[3]);
                        String message = values[4];
                        Alarm alarm = new Alarm(id, contact, hour, minute, message);
                        alarm.setAlarm(getApplicationContext(), false);
                    }
                }
            }
            Log.d(LOG_ID + 140, "Alarmas cargadas correctamente");
        }
    }


    //CREACION DEL MENÚ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    //MÉTODO PARA PODER ACCEDER A LAS OPCIONES DEL MENÚ

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //MENÚ DE NAVEGACIÓN
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // MÉTODO PARA CREAR LA BASE DE DATOS Y CARGAR LOS DATOS
    public void createBD() {
        DB.getDB.execSQL("DROP TABLE IF EXISTS Products");
        DB.getDB.execSQL("DROP TABLE IF EXISTS Lists");
        DB.getDB.execSQL("DROP TABLE IF EXISTS ListDetails");

        DB.getDB.execSQL("CREATE TABLE IF NOT EXISTS Products(" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name VARCHAR(100) ," +
                "product_description VARCHAR(100)," +
                "product_price REAL ," +
                "product_image_name VARCHAR(100)," +
                "product_image BLOB," +
                "product_times_in_lists INTEGER DEFAULT 0);");

        DB.getDB.execSQL("CREATE TABLE IF NOT EXISTS Lists(" +
                "list_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_name VARCHAR(100) NOT NULL," +
                "list_date DATE DEFAULT CURRENT_DATE);");

        DB.getDB.execSQL("CREATE TABLE IF NOT EXISTS ListDetails(" +
                "list_id INTEGER," +
                "product_id INTEGER," +
                "product_amount REAL," +
                "FOREIGN KEY (list_id) REFERENCES Lists(list_id),\n" +
                "FOREIGN KEY (product_id) REFERENCES Products(product_id)\n" +
                ");");
        Cursor c = DB.getDB.rawQuery("SELECT * FROM Products order by product_times_in_lists desc", null);
        if (c.getCount() == 0) {
            //CARGAR LOS DATOS DESDE EL JSON DE INTERNET
            getJson();
        }
        c.close();
        DB.loadDemoData();
        DB.getDBData();


    }


    //MÉTODO PARA CONSEGUIR LOS DATOS DEL JSON DE INTERNET
    // Y CARGARLOS EN LA LÓGICA
    public void getJson() {
        ConnectivityManager conManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                URL url = new URL("https://fp.cloud.riberadeltajo.es/listacompra/listaproductos.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000); // Le damos un segundo para leer los datos-> aborta
                connection.setConnectTimeout(5000); //le damos un segundo para conectar
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    ArrayList<Product> products = getProducts(connection.getInputStream());
                    for (Product product : products) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        product.getImgProduct().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteImage = stream.toByteArray();
                        String insertQuery = "INSERT INTO Products(product_name, product_description, product_image_name, product_image, product_price) VALUES('" +
                                product.getName() + "', '" +       //name
                                product.getDescription() + "', '" +//description
                                product.getImgName() + "', " +      //image name
                                "?, " +                      //image
                                product.getPrice() + ");";         //price
                        DB.getDB.execSQL(insertQuery, new Object[]{byteImage});
                    }
                    Log.d(LOG_ID + "237", "Datos Cargados desde Json");
                    connection.getInputStream().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //MÉTODO QUE LANZA UN HILO ASÍNCRONO QUE CARGA LOS CONTACTOS EN EL ARRAY DE LA LÓGICA
    private void getContacts() {
        ContactList.contactList.clear();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @SuppressLint("Range")
            @Override
            public void run() {
                String projection[] = {ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER};
                String selection = ContactsContract.Contacts.DISPLAY_NAME + " like ?";
                ContentResolver contentResolver = getContentResolver();
                Cursor contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, null, null);
                if (contactCursor.getCount() > 0) {
                    while (contactCursor.moveToNext()) {
                        String phoneNumber = "-----------";
                        if (contactCursor.getString(2).equals("1")) {
                            Cursor phonesCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + " = " + contactCursor.getString(0), null, null);
                            while (phonesCursor.moveToNext()) {
                                phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            phonesCursor.close();
                        }
                        int id = Integer.parseInt(contactCursor.getString(0));
                        String name = contactCursor.getString(1);
                        ContactList.contactList.add(new Contact(id, name, phoneNumber));
                    }
                }
                contactCursor.close();
                loadAlarms();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ContactList.contactList.sort(Comparator.comparing(Contact::getTelephoneNumber).reversed());
                        try {
                            ContactList.myAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d(LOG_ID + "288", getString(R.string.null_adapter_text));
                        }
                        Log.d(LOG_ID + "290", "Contactos cargados correctamente.");
                    }
                });
            }
        });


    }

    //MÉTODO QUE COMPRUEBA SI LA APLICACIÓN TIENE LOS PERMISOS PARA ACCEDER A LOS CONTACTOS
    public void checkPermissions() {
        if (checkSelfPermission("android.permission.READ_CONTACTS") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, CONTACT_PERMISSION);
        } else {
            getContacts();
        }
    }

    //MÉTODO QUE PIDE QUE EL USUARIO CONCEDA EL PERMISO PARA ACCEDER A LOS CONTACTOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACT_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }


    //MÉTODO QUE CARGA UN ARRAY DE PRODUCTOS SEGÚN LOS DATOS
    //QUE RECIBE DE LA CONEXIÓN AL JSON DE INTERNET
    private ArrayList<Product> getProducts(InputStream is) {
        int id = 1;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ArrayList<Product> products = new ArrayList<>();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                bo.write(buffer, 0, len);
            }
            String jsonString = bo.toString("UTF-8");
            JSONObject json = new JSONObject(jsonString);
            JSONArray productsArray = json.getJSONArray("productos");

            for (int j = 0; j < productsArray.length(); j++) {
                JSONObject jsonProduct = productsArray.getJSONObject(j);

                String name = jsonProduct.getString("nombre");
                String imgName = jsonProduct.getString("imagen");
                String description = jsonProduct.getString("descripcion");
                String price = jsonProduct.getString("precio");
                Bitmap imgProduct = getImages(imgName);

                Product product = new Product(id++, name, description, Double.parseDouble(price), imgName, imgProduct);
                products.add(product);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return products;


    }

    //MÉTODO PARA CONVERTIR LA IMAGEN QUE OBTENEMOS DE INTERNET A UN OBJETO BITMAP Y LA DEVUELVE
    private Bitmap getImages(String imgName) {
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL("https://fp.cloud.riberadeltajo.es/listacompra/images/" + imgName);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            connection.getInputStream().close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;

    }

    //LISTENER PARA EL BOTÓN DEL MENÚ DE NAVEGACIÓN QUE MUESTRA EL DIÁLOGO PARA CONSULTAR UNA LISTA
    View.OnClickListener navCheckListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListHistoryDialog dialog = new ListHistoryDialog();
            dialog.show(getSupportFragmentManager(), "Dialog All Lists");
        }
    };
}