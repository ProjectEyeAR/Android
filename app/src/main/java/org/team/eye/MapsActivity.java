package org.team.eye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import timber.log.Timber;

public class MapsActivity extends FragmentActivity {
    private static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 1;
    private static final int DEFAULT_ZOOM = 18;
    private static final long MIN_DISTANCE = 5;
    private static final long MIN_TIME = 500;

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationLayerPlugin mLocationLayerPlugin;
    private Session mSession;

    private FloatingActionButton mMyLocationFloatingActionButton;

    private int mRenderMode = 0;
    private int mCameraMode = CameraMode.NONE;

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
                int color = Color.WHITE;

                switch (mRenderMode) {
                    case RenderMode.NORMAL:
                        mRenderMode = RenderMode.COMPASS;
                        color = getResources().getColor(R.color.color_accent);

                        break;
                    case RenderMode.COMPASS:
                        mRenderMode = RenderMode.NORMAL;
                        color = getResources().getColor(R.color.color_primary);

                        break;
                }

                switch (mCameraMode) {
                    case CameraMode.NONE:
                        mCameraMode = CameraMode.TRACKING;
                        color = getResources().getColor(R.color.color_accent);

                        break;
                    case CameraMode.TRACKING:
                        mCameraMode = CameraMode.TRACKING_COMPASS;
                        color = getResources().getColor(R.color.color_accent);

                        break;
                    case CameraMode.TRACKING_COMPASS:
                        mCameraMode = CameraMode.NONE;
                        color = getResources().getColor(R.color.color_primary);

                        break;
                }

                mLocationLayerPlugin.setRenderMode(mRenderMode);
                mLocationLayerPlugin.setCameraMode(mCameraMode);

                mMyLocationFloatingActionButton.setBackgroundTintList(
                        ColorStateList.valueOf(color)
                );
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

        mMapView = findViewById(R.id.MapsActivity_MapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new com.mapbox.mapboxsdk.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.setStyle("Light");
                mLocationLayerPlugin = new LocationLayerPlugin(mMapView, mapboxMap);
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mMapView, mapboxMap);

                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Timber.d(exception.toString());
                }

                int leftMargin = mapboxMap.getUiSettings().getLogoMarginLeft();
                mMapboxMap.getUiSettings().setCompassMargins(
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 74),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16)
                );
                mMapboxMap.getUiSettings().setLogoMargins(
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 110)
                );
                mMapboxMap.getUiSettings().setAttributionMargins(
                        mMapboxMap.getUiSettings().getAttributionMarginLeft() - leftMargin + DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 16),
                        DisplayUnitUtil.dpToPx(MapsActivity.this, 110)
                );

                mLocationLayerPlugin.addOnCameraTrackingChangedListener(new OnCameraTrackingChangedListener() {
                    @Override
                    public void onCameraTrackingDismissed() {
                        mRenderMode = RenderMode.NORMAL;
                        mCameraMode = CameraMode.NONE;

                        mLocationLayerPlugin.setRenderMode(mRenderMode);
                        mLocationLayerPlugin.setCameraMode(mCameraMode);

                        int color = getResources().getColor(R.color.color_primary);

                        mMyLocationFloatingActionButton.setBackgroundTintList(
                                ColorStateList.valueOf(color)
                        );
                    }

                    @Override
                    public void onCameraTrackingChanged(int currentMode) {

                    }
                });
            }
        });

        mSession = Session.getInstance();
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

    @Override
    protected void onResume() {
        super.onResume();

        requestAccessFineLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION: {
                if (grantResults.length != 1 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestAccessFineLocationPermission();
                        }
                    }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }).setTitle(R.string.dialog_title_permission)
                            .setMessage(R.string.dialog_message_permission)
                            .show();
                }
            }
        }
    }

    private void requestAccessFineLocationPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(
                            MapsActivity.this,
                            new String[]{permission},
                            REQUEST_ACCESS_FINE_LOCATION_PERMISSION
                    );
                }
            }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }).setTitle(R.string.dialog_title_permission)
                    .setMessage(R.string.dialog_message_location_permission)
                    .show();
        } else if (ActivityCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    REQUEST_ACCESS_FINE_LOCATION_PERMISSION
            );
        }
    }
}
