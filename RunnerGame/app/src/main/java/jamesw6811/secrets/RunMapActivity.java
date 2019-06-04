package jamesw6811.secrets;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import jamesw6811.secrets.location.ManualGameLocationPoller;
import jamesw6811.secrets.location.MapUtilities;

public class RunMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_PACE = "jamesw6811.secrets.RunMapActivity.EXTRA_PACE";
    public static final boolean MANUAL_MODE_ENABLED = false;
    private static final String LOGTAG = RunMapActivity.class.getName();
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker lastOpened;
    // A reference to the service used to get location updates.
    private GameService gameService = null;
    private Button button_quit;
    AlertDialog GPSDialog;


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
                createConfirmAbortDialog();
            }
        });
    }

    private void createConfirmAbortDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Aborting Mission...")
                .setMessage("Do you really want to abort?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> confirmAbort())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void confirmAbort() {
        gameService.abortClicked();
        button_quit.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, perms, 0);
        } else {
            permissionsReady();
        }
    }

    protected void permissionsReady(){
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
        permissionsReady();
    }

    private void bindGameService() {
        Log.d(LOGTAG, "Binding GameService");

        // Handle starting settings
        Intent intent = getIntent();
        Intent serviceIntent = new Intent(this, GameService.class);
        serviceIntent.putExtras(getIntent());

        bindService(serviceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(() -> {
            if (gameService != null) {
                Location last = gameService.getLastLocation();
                if (last != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(MapUtilities.locationToLatLng(last)));
                }
            }
        });
        disableMarkerScrolling();
        mMapReady = true;
        bindGameService();
    }

    private void showLookingforGPSDialog(float accuracy) {
        if (GPSDialog == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_map);
            alertDialogBuilder.setTitle("GPS Signal");
            alertDialogBuilder.setMessage(String.format(getString(R.string.gps_dialog_message), accuracy)).setCancelable(false);
            GPSDialog = alertDialogBuilder.create();
        } else {
            GPSDialog.setMessage(String.format(getString(R.string.gps_dialog_message), accuracy));
        }
        if (!GPSDialog.isShowing()) GPSDialog.show();
    }

    private void dismissLookingforGPSDialog() {
        if (GPSDialog != null && GPSDialog.isShowing()) GPSDialog.dismiss();
    }

    private void initializeManualMode() {
        ManualGameLocationPoller gameLocationPoller = new ManualGameLocationPoller(this, gameService);
        gameService.setGameLocationPoller(gameLocationPoller);
        mMap.setOnMapClickListener(gameLocationPoller::manualSetLocation);
        mMap.setOnMarkerClickListener(marker -> {
            gameLocationPoller.manualSetLocation(marker.getPosition());
            return true;
        });
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

    public void gameStarted() {
        runOnUiThread(() -> {
            if (MANUAL_MODE_ENABLED) initializeManualMode();
            dismissLookingforGPSDialog();
        });
    }

    public void showGPSWarning(float accuracy) {
        runOnUiThread(() -> showLookingforGPSDialog(accuracy));
    }

    public void hideGPSWarning() {
        runOnUiThread(this::dismissLookingforGPSDialog);
    }

    public abstract static class MapUpdate {
        Runnable getRunnable(final GoogleMap gm) {
            return () -> {
                try {
                    updateMap(gm);
                } catch (IllegalArgumentException unmanaged) {
                    Log.w(LOGTAG, "Updating unmanaged descriptor");
                }
            };
        }

        abstract public void updateMap(GoogleMap map);
    }

}
