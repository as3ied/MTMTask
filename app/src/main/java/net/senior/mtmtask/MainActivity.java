package net.senior.mtmtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.senior.SourceAdapter;
import net.senior.model.Source;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private GoogleMap mMap;
    private TextView src;
    private TextView destination;
    private Marker marker;
    private LatLng latLng;
    LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    RecyclerView recyclerView;
    SourceAdapter sourceAdapter;
    List<Source> sources;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DrawerLayout drawer;
    ImageView menu;
    private int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.icon);
        recyclerView = findViewById(R.id.rec);
        src = findViewById(R.id.src);
        destination = findViewById(R.id.destination);
        sources = new ArrayList();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {

            chkPermsionGranted();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    @SuppressLint("MissingPermission")
    private void chkPermsionGranted() {


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());


                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    marker.setPosition(latLng);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            chkPermsionGranted();
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        ) {
            Toast.makeText(this, "You Have T Give Location Permission First", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
            startActivity(intent);
        } else {
            Toast.makeText(this, "You Have To Give Location Permission First", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    public String getStringAddress(double longitude, double latitude) {
        String address = "";
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void readSources() {
if(sources.size()!=0)
    sources.clear();
        db.collection(Source.Contract.DOC)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                final Source s = new Source(document.getReference(), data);
                                db.document(s.getReference().getPath())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                        if (task1.isSuccessful()) {
                                            Map<String, Object> data = task1.getResult().getData();

                                            sources.add(s);

                                            sourceAdapter = new SourceAdapter(sources);
                                            sourceAdapter.setOnSorceClicked(new SourceAdapter.OnSourceClicked() {
                                                @Override
                                                public void onSourceClick(int pos) {

                                                    if (x == 1)
                                                        src.setText(sources.get(pos).getName());
                                                    else
                                                        destination.setText(getStringAddress(sources.get(pos).getLat(), sources.get(pos).getLng()));

                                                    recyclerView.setVisibility(View.GONE);

                                                }
                                            });

                                            recyclerView.setAdapter(sourceAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                        }

                                    }

                                });


                            }
                        }
                    }
                });

    }


    public void showSources(View view) {
        recyclerView.setVisibility(View.VISIBLE);
        if (view.getId() == R.id.src)
            x = 1;
        else
            x = 0;
        readSources();

    }

    public void showDrawer(View view) {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.close();
        else
            drawer.openDrawer(GravityCompat.START);

    }
}
