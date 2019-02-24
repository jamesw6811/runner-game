package jameswrunner.runnergame;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import jameswrunner.runnergame.gameworld.GameWorld;

import static jameswrunner.runnergame.maputils.MapUtilities.locationToLatLng;

public class RunMapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final float MINIMUM_ACCURACY_REQUIRED = 25f;


    private GoogleMap mMap;
    private TextToSpeech tts;
    private GameWorld gw;

    private Marker lastOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_run_map);
        initializeTextToSpeechAudio();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startListeningLocation();
    }

    private void initializeTextToSpeechAudio() {
        // initialization of the audio attributes and focus request
        final AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d(this.getClass().getName(), "TextToSpeech initialized with status:"+status);
                if (status != TextToSpeech.ERROR){
                    Log.d(this.getClass().getName(), "TextToSpeech no error");
                }
            }
        });

        final AudioManager.OnAudioFocusChangeListener afcl = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        };

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                mAudioManager.requestAudioFocus(afcl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            }

            @Override
            public void onDone(String utteranceId) {
                mAudioManager.abandonAudioFocus(afcl);
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    @Override
    public void onDestroy(){
        if(gw != null){
            gw.stopRunning();
        }
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        startListeningLocation();
    }

    public void startListeningLocation() {
        // Acquire a reference to the system Location Manager
        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(this);

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, perms, 0);
            return;
        }
        LocationRequest lr = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(500);
        flpc.requestLocationUpdates(lr,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            makeUseOfNewLocation(location);
                        }
                    };
                },
                null);
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
        mMap.getUiSettings().setAllGesturesEnabled(false);
        disableMarkerScrolling();
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

    private void makeUseOfNewLocation(Location loc) {
        if (loc.getAccuracy() < MINIMUM_ACCURACY_REQUIRED) {
            if (gw == null) {
                gw = new GameWorld(loc, mMap, tts, this);
                gw.initializeAndStartRunning();
                initializeCurrentPosition(locationToLatLng(loc));
            }
            gw.updatePlayerLocation(loc);
        }
    }

    private void initializeCurrentPosition(LatLng ll) {
        zoomToWalkable(ll);
    }

    private void zoomToWalkable(LatLng ll) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
    }


    protected void showErrorToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
