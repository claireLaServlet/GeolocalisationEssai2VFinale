package com.example.install.geolocalisationessai2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager lm;
    private String choixSource;
    private Location location;
    private double lat;
    private double lon;
    private double altitude;
    private String lati;
    private String longi;
    private String alti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //On recupere le service de localisation
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.i("oncreate ", lm.toString());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        findViewById(R.id.btnOptenirPosition).setEnabled(false);
        findViewById(R.id.btnAffAdresse).setEnabled(false);
    }

    public void choisirSource(View v) {
        renitialisation();

        List<String> providers = lm.getProviders(true);
        final String[] sources = new String[providers.size()];
        int i = 0;
        //on stock le nom de ces source dans un tableau de string
        for (String provider : providers) {
            sources[i] = provider;
            i++;
        }


        //On affiche la liste des sources dans une fenetre de dialogue
        new AlertDialog.Builder(MainActivity.this).setItems(sources, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                findViewById(R.id.bynChoixSource).setEnabled(true);
                //on stock le choix de la source choisi
                choixSource = sources[which];

                //on modifie la barre de titre de l'application
                setTitle(String.format("%s - %s", getString(R.string.app_name), choixSource));
            }
        }).create().show();

        findViewById(R.id.btnOptenirPosition).setEnabled(true);

    }

    public void obtenirPosition() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //On demande au service de localisation de nous notifier tout changement de position
        //sur la source (le provider) choisie, toute les minutes
        lm.requestLocationUpdates(choixSource, 60000, 0, this);
    }

    public void afficherLocation()
    {
        obtenirPosition();
        // on affiche les infos a l'écran
        ((TextView)findViewById(R.id.txtLatitude)).setText(lati);
        ((TextView)findViewById(R.id.txtlongitude)).setText(longi);
        ((TextView)findViewById(R.id.txtAltitude)).setText(alti);

        findViewById(R.id.btnAffAdresse).setEnabled(true);

    }

   public void afficherAdresse() {
       //Le geocoder permet de récupérer ou chercher des adresses gràce à un mot clé ou une position
        Geocoder geo = new Geocoder(this);
        //Ici on récupère la premiere adresse trouvé gràce à la position que l'on a récupéré
        try {
            List<Address> adresses = geo.getFromLocation(lat, lon, 1);

            if (adresses != null && adresses.size() > 0) {
                Address adresse = adresses.get(0);
                //Si le geocoder a trouver une adresse, alors on l'affiche
                ((TextView) findViewById(R.id.txtAffichageAdresse)).setText(String.format("%s, %s %s",
                        adresse.getAddressLine(0), adresse.getPostalCode(), adresse.getLocality()));
                Log.i(adresse.getPostalCode(), "YYYYYYYYYYYYYYYYYYYYYYYY");
            } else {
                //sinon on affiche un message d'erreur
                ((TextView) findViewById(R.id.txtAffichageAdresse)).setText("L'adresse n'a pu être déterminée");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renitialisation()
    {
        ((TextView)findViewById(R.id.txtLatitude)).setText("0.0");
        ((TextView)findViewById(R.id.txtlongitude)).setText("0.0");
        ((TextView)findViewById(R.id.txtAltitude)).setText("0.0");
        ((TextView)findViewById(R.id.txtAffichageAdresse)).setText("");



    }


    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        altitude = location.getAltitude();

        lati = String.valueOf(lat);
        longi = String.valueOf(lon);
        alti = String.valueOf(altitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bynChoixSource:
                choisirSource(v);
                break;
            case R.id.btnOptenirPosition:
                afficherLocation();
                break;
            case R.id.btnAffAdresse:
                afficherAdresse();
                break;
            default:
                break;

        }
    }
}
