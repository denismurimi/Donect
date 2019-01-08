package sined.bloodconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Home extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Context context = Home.this;
    private List<Marker> donorMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register_donor:
                startActivity(new Intent(context, Register.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // load registered donors here
        //  add markers for them
        DonorDatabase database = new DonorDatabase(context);
        List<Donor> donors = database.getDonors();

        float zoom = 10f;

        if (donors.size() > 0) {
            for (Donor donor : donors) {
                googleMap.addMarker(new MarkerOptions().position(donor.getLocation()).title(donor.getBloodGroup()));
            }
            LatLng lastDonorLocation = donors.get(donors.size() - 1).getLocation();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastDonorLocation));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastDonorLocation, zoom);
            googleMap.animateCamera(cameraUpdate);
        } else {
            LatLng nairobi = new LatLng(-1.286511, 36.816375);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(nairobi));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(nairobi, zoom);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
}
