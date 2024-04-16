package com.example.navproto;

import android.content.Intent;
import android.graphics.Rect;
import android.location.GpsStatus;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.example.navproto.databinding.ActivityOpenStreetMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class OpenStreetMap extends AppCompatActivity implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private ActivityOpenStreetMapBinding OSMbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OSMbinding = ActivityOpenStreetMapBinding.inflate(getLayoutInflater());
        setContentView(OSMbinding.getRoot());

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        OSMbinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpenStreetMap.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mMap = OSMbinding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getMapCenter();
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());

        /*
            Using the different Location Providers
            Network
            GPS
            WIFI RTT
         */
        mMyLocationOverlay = new MyLocationNewOverlay(new MyLocationProvider(this), mMap);

        controller = mMap.getController();

        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);
        mMyLocationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            controller.setCenter(mMyLocationOverlay.getMyLocation());
            controller.animateTo(mMyLocationOverlay.getMyLocation());
        }));

        controller.setZoom(20.0);

        //Log.e("TAG", "onCreate:in " + controller.zoomIn());
        //Log.e("TAG", "onCreate: out " + controller.zoomOut());

        mMap.getOverlays().add(mMyLocationOverlay);
        mMap.addMapListener(this);
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        //Log.e("TAG", "onCreate:la " + event.getSource().getMapCenter().getLatitude());
        //Log.e("TAG", "onCreate:lo " + event.getSource().getMapCenter().getLongitude());
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        //Log.e("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());
        return false;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO
    }
}