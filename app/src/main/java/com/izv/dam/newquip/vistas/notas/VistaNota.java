package com.izv.dam.newquip.vistas.notas;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.basedatos.AyudanteOrm;
import com.izv.dam.newquip.contrato.ContratoNota;
import com.izv.dam.newquip.pojo.Localizacion;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.vistas.mapa.MapsActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

public class VistaNota extends AppCompatActivity implements ContratoNota.InterfaceVista,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText editTextTitulo, editTextNota;
    private Nota nota = new Nota();
    private PresentadorNota presentador;
    private TextView tvLocalizacion;
    private GoogleApiClient mGoogleApiClient;

    private FloatingActionButton btMapa;
    private Context c = this;

    private AyudanteOrm a = new AyudanteOrm(c);
    RuntimeExceptionDao<Localizacion, Integer> Dao = a.getSimpleRunTimeDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);

        presentador = new PresentadorNota(this);

        editTextTitulo = (EditText) findViewById(R.id.etTitulo);
        editTextNota = (EditText) findViewById(R.id.etNota);
        btMapa = (FloatingActionButton) findViewById(R.id.btMapa);

        if (savedInstanceState != null) {
            nota = savedInstanceState.getParcelable("nota");
        } else {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                nota = b.getParcelable("nota");
            }
        }
        mostrarNota(nota);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Localizacion> loc;
                try {
                    QueryBuilder<Localizacion, Integer> qb = Dao.queryBuilder();
                    qb.setWhere(qb.where().eq("idnota", nota.getId()));
                    loc = (ArrayList<Localizacion>) Dao.query(qb.prepare());
                    Intent i = new Intent(c, MapsActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("localizacion", loc);
                    i.putExtras(b);
                    startActivity(i);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

        @Override
        protected void onPause () {
        saveNota();
        presentador.onPause();
        super.onPause();
    }

        @Override
        protected void onResume () {
        presentador.onResume();
        super.onResume();
    }

        @Override
        protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("nota", nota);
    }

        @Override
        public void mostrarNota (Nota n){
        editTextTitulo.setText(nota.getTitulo());
        editTextNota.setText(nota.getNota());
    }

    private void saveNota() {
        nota.setTitulo(editTextTitulo.getText().toString());
        nota.setNota(editTextNota.getText().toString());
        long r = presentador.onSaveNota(nota);
        if (r > 0 & nota.getId() == 0) {
            nota.setId(r);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("On connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "NO FINE LOCATION", Toast.LENGTH_SHORT);
            System.out.println("no fine location");
            return;
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "NO COARSED LOCATION", Toast.LENGTH_SHORT);
            System.out.println("no coarsed location");
            return;

        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT);
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"La conexion se ha suspendido",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"La conexion ha fallado",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        saveNota();
        float latitude = (float) location.getLatitude();
        float longitude = (float) location.getLongitude();
        Localizacion loc = new Localizacion(nota.getId(),latitude,longitude);
        Dao.create(loc);
    }
}
