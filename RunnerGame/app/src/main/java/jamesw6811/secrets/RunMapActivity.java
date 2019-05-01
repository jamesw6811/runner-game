package jamesw6811.secrets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class RunMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_PACE = "jamesw6811.secrets.RunMapActivity.EXTRA_PACE";
    private static final String LOGTAG = RunMapActivity.class.getName();
    SupportMapFragment mapFragment;
    private Button button_quit;
    private GoogleMap mMap;
    private Marker lastOpened;
    // A reference to the service used to get location updates.
    private GameService gameService = null;


    // Tracks the bound state of the service.

    private boolean mBound = false;
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameService.bindUI(RunMapActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            gameService = null;
            mBound = false;
        }
    };
    private boolean mMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_run_map);

        // Obtain the SupportMapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        button_quit = findViewById(R.id.button_quit);
        button_quit.setOnClickListener(v -> {
            if (gameService != null) {
                gameService.abortClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Once map is ready to be used
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        bindGameService();
    }

    private void bindGameService() {
        Log.d(LOGTAG, "Binding GameService");

        // Handle starting settings
        Intent intent = getIntent();
        double pace = intent.getDoubleExtra(EXTRA_PACE, -1);
        Intent serviceIntent = new Intent(this, GameService.class);
        serviceIntent.putExtra(EXTRA_PACE, pace);

        todo // add extra to select mission

        bindService(serviceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.getUiSettings().setAllGesturesEnabled(false);
        disableMarkerScrolling();
        mMapReady = true;
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, perms, 0);
            return;
        }
        bindGameService();
    }

    private void disableMarkerScrolling() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpened != null) {
                    // Close the info window
                    lastOpened.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpened.equals(marker)) {
                        // Nullify the lastOpened object
                        lastOpened = null;
                        // Return so that the info window isn't opened again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last opened such that we can close it later
                lastOpened = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });
    }


    protected void showErrorToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void processMapUpdate(MapUpdate mu) {
        if (mMapReady) {
            runOnUiThread(mu.getRunnable(mMap));
        }
    }

    public abstract static class MapUpdate {
        public Runnable getRunnable(final GoogleMap gm) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        updateMap(gm);
                    } catch (IllegalArgumentException unmanaged) {
                        Log.w(LOGTAG, "Updating unmanaged descriptor");
                    }
                }
            };
        }

        abstract public void updateMap(GoogleMap map);
    }

}
