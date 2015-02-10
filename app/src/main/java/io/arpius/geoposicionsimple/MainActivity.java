package io.arpius.geoposicionsimple;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button btnActivar, btnDesactivar;
    private TextView lblLatitud, lblLongitud, lblPrecision, lblAltitud, lblEstadoActual, lblDireccion;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Geocoder geocoder;
    private List<Address> direcciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnActivar = (Button)findViewById(R.id.btnActivar);
        btnDesactivar = (Button)findViewById(R.id.btnDesactivar);
        lblLatitud = (TextView)findViewById(R.id.lblLatitud);
        lblLongitud = (TextView)findViewById(R.id.lblLongitud);
        lblPrecision = (TextView)findViewById(R.id.lblPrecision);
        lblAltitud = (TextView)findViewById(R.id.lblAltitud);
        lblEstadoActual = (TextView)findViewById(R.id.lblEstadoActual);
        lblDireccion = (TextView)findViewById(R.id.lblDireccion);

        btnActivar.setOnClickListener(this);
        btnDesactivar.setOnClickListener(this);

        geocoder = new Geocoder(this, Locale.getDefault());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnActivar:
                actualizarPosicion();
                break;
            case R.id.btnDesactivar:
                locationManager.removeUpdates(locationListener);
                break;
        }
    }

    private void actualizarPosicion() {
        //definimos cada cuanto tiempo se actualizarán los datos en milisegundos
        int tiempoActualizacion = 3000;

        //obtenemos una referencia al LocationManager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //obtenemos la última posición conocida
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //mostramos la última posición conocida
        mostrarPosicion(location);

        //nos registramos para recibir actualizaciones de la posición
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("LocAndroid", "Provider status: " +status);
                lblEstadoActual.setText(getResources().getString(R.string.provider)+ ": " +status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                lblEstadoActual.setText(R.string.provider_on);
            }

            @Override
            public void onProviderDisabled(String provider) {
                lblEstadoActual.setText(R.string.provider_off);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempoActualizacion, 0, locationListener);
    }

    private void mostrarPosicion(Location loc) {

        String latitud = getResources().getString(R.string.latitud);
        String longitud = getResources().getString(R.string.longitud);
        String precision = getResources().getString(R.string.precision);
        String altitud = getResources().getString(R.string.altitud);
        String direc = getResources().getString(R.string.direccion);
        String sin_datos = getResources().getString(R.string.sin_datos);

        if (loc != null) {

            lblLatitud.setText(latitud+ ": " +String.valueOf(String.format("%.4f", loc.getLatitude())));
            lblLongitud.setText(longitud+ ": " +String.valueOf(String.format("%.4f", loc.getLongitude())));
            lblPrecision.setText(precision+ ": " +String.valueOf(String.format("%.2f", loc.getAccuracy())));
            lblAltitud.setText(altitud+ ": " +String.valueOf(String.format("%.2f", loc.getAltitude())));

            try {
                direcciones = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                if(geocoder.isPresent()) {
                    Address direccion = direcciones.get(0);
                    StringBuilder direccionCompleta = new StringBuilder(direc+ ":\n");

                    for(int i=0; i < direccion.getMaxAddressLineIndex(); i++) {
                        direccionCompleta.append(direccion.getAddressLine(i)).append("\n");
                    }

                    lblDireccion.setText(direccionCompleta.toString());
                }
                else{
                    lblDireccion.setText(getResources().getString(R.string.no_devuelve));
                }
            } catch (IOException e) {
                e.printStackTrace();
                lblDireccion.setText(getResources().getString(R.string.imposible_obtener));
            }

            Log.i("LocAndroid", String.valueOf(loc.getLatitude()) + " - " + String.valueOf(loc.getLongitude()));
        }
        else {
            lblLatitud.setText(latitud+ ": (" +sin_datos+ ")");
            lblLongitud.setText(longitud+ ": (" +sin_datos+ ")");
            lblPrecision.setText(precision+ ": (" +sin_datos+ ")");
            lblAltitud.setText(altitud+ ": (" +sin_datos+ ")");
            lblDireccion.setText(direc+ ": (" +sin_datos+ ")");
        }
    }
}