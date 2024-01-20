package com.danlla0.ShopList_Android;

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

import com.danlla0.ShopList_Android.Dialogs.ListHistoryDialog;
import com.danlla0.ShopList_Android.Objects.Alarm;
import com.danlla0.ShopList_Android.Objects.Contact;
import com.danlla0.ShopList_Android.databinding.ActivityMainBinding;
import com.danlla0.ShopList_Android.dto.ContactList;
import com.danlla0.ShopList_Android.dto.DB;
import com.danlla0.ShopList_Android.Objects.Product;
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

    private static final int PERMISO_CONTACTOS = 1;
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


        //STACK OVERFLOW SUPUESTA SOLUCIÓN DE JAVA OFICIAL
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        NavController navCo = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navCo, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navCo);

        //ANDROID STUDIO GENERATED CODE
/*        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

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
                        //Trabajo en Background aquí
                        createBD();
                        checkPermissions();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Trabajo en la interfaz de usuario aquí
                                DB.updateTimesInLists();
                                DB.getListHistory();


                            }
                        });
                    }
                });
                //LISTENER QUE INVOCA EL DIALOGO CUANDO PULSAMOS CONSULTAR LISTAS
                findViewById(R.id.nav_checkLists).setOnClickListener(navCheckListListener);
            }
        });

    }


    public void loadAlarms() {
        SharedPreferences myPreferences = getSharedPreferences("alarms-preferences", MODE_PRIVATE);
        HashMap<String, String> alarms = (HashMap<String, String>) myPreferences.getAll();
        if (alarms.size() > 0) {
            for (int i = 1; i <= alarms.size(); i++) {
                String alarmID = "alarm" + i;
                if (alarms.containsKey(alarmID)) {
                    String[] values = alarms.get(alarmID).toString().split(";");
                    ContactList.contactList.sort(Comparator.comparing(Contact::getId));
                    int id = Integer.parseInt(values[0]);
                    int contactID = Integer.parseInt(values[1]);
                    Contact contact = ContactList.contactList.stream().filter(contactAux -> contactAux.getId() == contactID).findFirst().get();
                    int hour = Integer.parseInt(values[2]);
                    int minute = Integer.parseInt(values[3]);
                    String message = values[4];
                    Alarm alarm = new Alarm(id, contact, hour, minute, message);
                    alarm.setAlarm(getApplicationContext(), false);
                    Log.d(LOG_ID + 151, alarm.toString());
                }
            }
            Log.d(LOG_ID + 151, "Alarmas cargadas correctamente");
        }
    }


    //CREACION DEL MENÚ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    //MÉTODO PARA PODER ACCEDER A LOS OBJETOS DEL MENÚ

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            System.out.println("---------------------------SELECTED CONTACTS-------------------------");
            ContactList.selectedContactList.forEach(contact -> System.out.println(contact.toString()));
            System.out.println("------------------------------CONTACTS-------------------------------");
            ContactList.contactList.forEach(contact -> System.out.println(contact.toString()));
        }
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
            //tenemos conexión a internet, podemos intentar traer la url
            try {
                //Establecer la conexión
                URL url = new URL("https://fp.cloud.riberadeltajo.es/listacompra/listaproductos.json");
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setReadTimeout(10000); // Le damos un segundo para leer los datos-> aborta
                conexion.setConnectTimeout(5000); //le damos un segundo para conectar
                conexion.setRequestMethod("GET");
                conexion.setDoInput(true);
                conexion.connect();
                //si llegamos aquí: Estamos conectados y listos para leer la respuesta
                if (conexion.getResponseCode() == 200) {
                    ArrayList<Product> product = getProducts(conexion.getInputStream());
                    for (Product p : product) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        p.getImgProduct().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        String insertQuery = "INSERT INTO Products(product_name, product_description, product_image_name, product_image, product_price) VALUES('" +
                                p.getName() + "', '" +       //name
                                p.getDescription() + "', '" +//description
                                p.getImgName() + "', " +      //image name
                                "?, " +                      //image
                                p.getPrice() + ");";         //price
                        DB.getDB.execSQL(insertQuery, new Object[]{byteArray});
                    }
                    Log.d(LOG_ID + "232", "Datos Cargados desde Json");
                    conexion.getInputStream().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //MÉTODO QUE LANZA UN HILO ASINCRONO QUE CARGA LOS CONTACTOS EN EL ARRAY
    private void getContacts(String text) {
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
                String selectionArgs[] = {"%" + text + "%"};
                ContentResolver miCr = getContentResolver();
                Cursor miCursor = miCr.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (miCursor.getCount() > 0) {
                    while (miCursor.moveToNext()) {
                        String tlfNumberDefault = "-----------";
                        String tlfNumber = tlfNumberDefault;
                        if (miCursor.getString(2).equals("1")) {
                            Cursor phones = miCr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + " = " + miCursor.getString(0), null, null);
                            while (phones.moveToNext()) {
                                tlfNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            phones.close();
                        }
                        int id = Integer.parseInt(miCursor.getString(0));
                        String name = miCursor.getString(1);
                        ContactList.contactList.add(new Contact(id, name, tlfNumber));
                    }
                }
                miCursor.close();
                loadAlarms();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Trabajo de la interfaz de usuario aquí
                        ContactList.contactList.sort(Comparator.comparing(Contact::getTelephoneNumber).reversed());
                        try {
                            ContactList.myAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d(LOG_ID + "286", "Adaptador ContactList Nulo");
                        }
                        Log.d(LOG_ID + "288", "Contactos cargados correctamente.");
                    }
                });
            }
        });


    }

    //MÉTODO QUE COMPRUEBA SI LA APLICACIÓN TIENE LOS PERMISOS PARA ACCEDER A LOS CONTACTOS
    public void checkPermissions() {
        if (checkSelfPermission("android.permission.READ_CONTACTS") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, PERMISO_CONTACTOS);
        } else {
            getContacts("");
        }
    }

    //MÉTODO QUE PIDE QUE EL USUARIO CONCEDA EL PERMISO PARA ACCEDER A LOS CONTACTOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CONTACTOS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts("");
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
            JSONArray productosArray = json.getJSONArray("productos");

            for (int j = 0; j < productosArray.length(); j++) {
                JSONObject productoJson = productosArray.getJSONObject(j);

                String name = productoJson.getString("nombre");
                String imgName = productoJson.getString("imagen");
                String description = productoJson.getString("descripcion");
                String price = productoJson.getString("precio");
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

    //LISTENER PARA EL BOTÓN DEL MENÚ DE NAVEGACIÓN QUE MUESTRA EL DIALOGO PARA CONSULTAR UNA LISTA
    View.OnClickListener navCheckListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListHistoryDialog dialog = new ListHistoryDialog();
            dialog.show(getSupportFragmentManager(), "Dialog All Lists");
        }
    };
}