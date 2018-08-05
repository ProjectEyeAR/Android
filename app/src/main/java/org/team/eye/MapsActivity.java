package org.team.eye;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private enum MyLocationMode {
        DISABLED(0),
        ENABLED(1),
        ENABLED_DIRECTION(2);

        private final int id;

        MyLocationMode(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static MyLocationMode fromId(int id) {
            for (MyLocationMode myLocationMode: values()) {
                if (myLocationMode.id == id) {
                    return myLocationMode;
                }
            }

            return MyLocationMode.DISABLED;
        }
    }

    private static final int DEFAULT_ZOOM = 20;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Session mSession;
    private Location mLocation;

    private FloatingActionButton mMyLocationFloatingActionButton;

    private List<String> mRequiredPermissionList;

    private MyLocationMode mMyLocationMode = MyLocationMode.DISABLED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ConstraintLayout constraintLayout = findViewById(R.id.MapsActivity_ConstraintLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mMyLocationFloatingActionButton = findViewById(
                R.id.MapsActivity_MyLocationFloatActionButton
        );
        mMyLocationFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chageMyLocationMode(MyLocationMode.fromId(mMyLocationMode.getId() + 1));
            }
        });

        FloatingActionButton arFloatingActionButton = findViewById(
                R.id.MapsActivity_ARFloatActionButton
        );
        arFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ARCameraActivity.class));
            }
        });

        Button myPageButton = findViewById(R.id.MapsActivity_MyPageButton);
        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserActivity.class);

                startActivity(intent);
            }
        });

        SeekBar seekBar = findViewById(R.id.MapsActivity_SeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 100) {
                    seekBar.setProgress(0);

                    Intent intent = new Intent(
                            MapsActivity.this,
                            CameraActivity.class
                    );

                    startActivity(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() != 100) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBar.setProgress(0, true);
                    } else {
                        seekBar.setProgress(0);
                    }
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MapsActivity_Map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSession = Session.getInstance();

        mRequiredPermissionList = new ArrayList<>();

        mRequiredPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mRequiredPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        for (String permission: mRequiredPermissionList) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1000, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;

                if (mMyLocationMode.getId() > 0) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                    mMap.animateCamera(cameraUpdate);
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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mSession.isHaveSession()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(intent);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (String permission: mRequiredPermissionList) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == REASON_GESTURE) {
                    chageMyLocationMode(MyLocationMode.DISABLED);
                }
            }
        });
    }

    private void chageMyLocationMode(MyLocationMode myLocationMode) {
        mMyLocationMode = myLocationMode;

        int color;

        switch (mMyLocationMode) {
            case ENABLED:
            case ENABLED_DIRECTION:
                color = getResources().getColor(R.color.color_primaryDark);

                if (mLocation != null) {
                    LatLng latLng = new LatLng(
                            mLocation.getLatitude(),
                            mLocation.getLongitude()
                    );
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            DEFAULT_ZOOM
                    );
                    mMap.animateCamera(cameraUpdate);
                }

                break;
            case DISABLED:
            default:
                color = Color.WHITE;
                break;
        }

        mMyLocationFloatingActionButton.setBackgroundTintList(
                ColorStateList.valueOf(color)
        );
    }
}
