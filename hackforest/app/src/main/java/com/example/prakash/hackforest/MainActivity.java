package com.example.prakash.hackforest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button getLocationBtn,sndbtn,btbtn;
    TextView locationText,locationText2,textclr;
    EditText idedit,sndtxt;
    ImageView im1;
    LocationManager locationManager;
    private static final String TAG3 = "LA";
//    RequestQueue requestQueue;
    BluetoothAdapter myBluetooth ;
    private Set<BluetoothDevice> pairedDevices;
    String address ;
    BluetoothSocket btSocket ;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

   String idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

   //     requestQueue = Volley.newRequestQueue(this);

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        sndbtn=findViewById(R.id.sndbtn1);
        btbtn=findViewById(R.id.btlist);
        sndtxt=findViewById(R.id.editText);
        locationText = (TextView)findViewById(R.id.locationText);
        locationText2 = (TextView)findViewById(R.id.locationText2);
        textclr=findViewById(R.id.textalert);
        im1=findViewById(R.id.colorimg);
        idedit=findViewById(R.id.getID);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        if(address !=null) {
            new ConnectBT().execute();
        }
        btbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(MainActivity.this,DeviceList.class);
                startActivity(i1);
            }
        });
        sndbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message1=sndtxt.getText().toString();
                turnOn(message1);
            }
        });
        idd = idedit.getText().toString();
        String iddd = idd;
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
    String lat, lon;
    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
         lat = Double.toString(location.getLatitude());
         lon = Double.toString(location.getLongitude());

//new AsyncTaskRunner().execute();
new BackgroundWorker(getApplicationContext()).execute();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private class BackgroundWorker extends AsyncTask<String, Void, String> {

        Context context;
        String result;

    private BackgroundWorker(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        //String register_url = "http://www.axisbank.epizy.com";
        Log.d("ID:", idd);
        String register_url = "http://159.65.144.222/test.php?X="+lat+"&Y="+lon+"&ID=2";
        try {
            URL url = new URL(register_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("X", "UTF-8")+"="+URLEncoder.encode(lat, "UTF-8")
                    +"&"+URLEncoder.encode("Y","UTF-8")+"="+URLEncoder.encode(lon,"UTF-8")
                    +"&"+URLEncoder.encode("ID","UTF-8")+"="+URLEncoder.encode("1","UTF-8")

                    ;
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();


            Log.d("URL:", register_url);
            outputStream.close();
            try {
                InputStream inputStream = httpURLConnection.getInputStream();
                Log.d("encode", "encoded");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                Log.d("encode", "encoded");
                result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
               // Log.d("encode",result);
               // locationText2.setText(result);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPreExecute() {
        //  alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
        super.onPreExecute();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("encode",result);
        locationText2.setText(result);
        if(result!=null) {
            try {
                JSONObject object = new JSONObject(result);
                String attr1 = object.getString("Alert");
                String attr2 = object.getString("Alert1");


                Log.d("Alert ki value", attr1);
                Log.d("Alert2 ki value", attr2);
                if((attr1.equals("1")) && (attr2.equals("1")))

                {
                   // textclr.setText(1+"\n"+1);
                    textclr.setText("DANGER ZONE");
                    turnOn("1");
                    im1.setBackgroundColor(R.color.common_action_bar_splitter);
                }
                else  if((attr1.equals("1")) && (attr2.equals("0")))
                {
                //textclr.setText(1+"\n"+0);

                    textclr.setText("CROWDED ZONE");
                    turnOn("2");
                    im1.setBackgroundColor(R.color.wallet_holo_blue_light);
                }
               else  if((attr1.equals("0")) && (attr2.equals("1")))
                {
                    //textclr.setText(1+"\n"+0);

                    textclr.setText("DANGER ZONE");
                    turnOn("2");
                    im1.setBackgroundColor(R.color.colorAccent);
                }
                 else if((attr1.equals("0")) && (attr2.equals("0")))

                {
                    textclr.setText("NORMAL ZONE");
                    turnOn("3");
                    im1.setBackgroundColor(R.color.colorPrimaryDark);
                    //textclr.setText(0+"\n"+1);
                }

              //im1.setColorFilter(ContextCompat.getColor(context, R.color.RED), android.graphics.PorterDuff.Mode.MULTIPLY);
                //im1.setColorFilter(getContext().getResources.getColor(R.id.blue));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // result
    }


}

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    public void turnOn(String message) {
        final String out = message;

        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(out.getBytes());
                Log.d("Live:", "Sent: " + out);
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }

    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            //  progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            // progress.dismiss();
        }
    }


}
