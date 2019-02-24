package jameswrunner.runnergame.gameworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static jameswrunner.runnergame.maputils.MapUtilities.locationToLatLng;
import static jameswrunner.runnergame.maputils.MapUtilities.snapToRoad;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorld {
    public static final float GAME_HEIGHT_METERS = 300;
    public static final float GAME_WIDTH_METERS = 300;
    public static final float CATCH_RUNNER_DISTANCE_METERS = 20;
    public static final float RUNNER_SPEED = 3f;
    public static final int ANNOUNCEMENT_PERIOD = 20*1000;
    StraightRunnerAI srai;
    LinkedList<ControlPoint> cplist = new LinkedList<ControlPoint>();
    private GameBoundaries bounds;
    private LatLng lastPosition;
    private LatLng lastLastPosition;
    private GoogleMap googlemap;
    private GameWorldThread gameWorldThread;
    private Activity activityContext;
    private Circle currentPositionCircle;
    private Polygon gameBoundsPoly;
    private Circle gameBoundsBottomLeft;
    private TextToSpeech tts;
    private long lastAnnouncementTime = 0;

    public GameWorld(Location center, GoogleMap gm, TextToSpeech tts, Activity act) {
        bounds = new GameBoundaries(locationToLatLng(center), 0, GAME_HEIGHT_METERS, GAME_WIDTH_METERS);
        googlemap = gm;
        activityContext = act;
        this.tts = tts;
    }

    private void speakTTS(CharSequence speech){
        tts.speak(speech, TextToSpeech.QUEUE_ADD, null, "RunMapVoice");
    }

    private static void runOnMainThreadBlocking(final Runnable runnable) throws InterruptedException {
        final CountDownLatch completionSignal = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                completionSignal.countDown();
            }
        });

        completionSignal.await();
    }

    public void initializeNewGame() {
        drawGameBounds();
        try {
            generateControlPoints();
        } catch (Exception e) {
            e.printStackTrace();
            activityContext.finish();
        }
        if (srai != null) {
            srai.destroy();
            srai = null;
        }
    }

    public void updatePlayerLocation(Location loc) {
        LatLng ll = locationToLatLng(loc);
        lastPosition = ll;
    }

    public void drawGameBounds() {
        activityContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameBoundsPoly != null) {
                    gameBoundsPoly.remove();
                }
                gameBoundsPoly = googlemap.addPolygon(
                        new PolygonOptions().addAll(bounds.getLatLngCorners())
                                .strokeColor(Color.RED));
                gameBoundsBottomLeft = googlemap.addCircle(
                        new CircleOptions().strokeWidth(2).fillColor(Color.GREEN).radius(10).center(bounds.bottomLeft));
            }
        });
    }

    private void generateControlPoints() throws Exception {
        if (cplist.size() > 0) {
            for (ControlPoint cp : cplist) {
                cp.destroy();
            }
            cplist.clear();
        }
        List<GamePoint> controlpointpoints = new LinkedList<GamePoint>();
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 2, GAME_HEIGHT_METERS * 1 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 3, GAME_HEIGHT_METERS * 2 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 3, GAME_HEIGHT_METERS * 2 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 4, GAME_HEIGHT_METERS * 3 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 4, GAME_HEIGHT_METERS * 3 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 3 / 4, GAME_HEIGHT_METERS * 3 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 3, GAME_HEIGHT_METERS * 4 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 3, GAME_HEIGHT_METERS * 4 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 2, GAME_HEIGHT_METERS * 5 / 6));

        int i = 1;
        for (GamePoint gp : controlpointpoints) {
            cplist.add(new ControlPoint(getNearestGamePointWalkable(gp), "Control Point Number " + i, "CP#" + i));
            i += 1;
        }
    }

    private GamePoint getNearestGamePointWalkable(GamePoint gp) throws Exception {
        LatLng ll = bounds.gamePointtoLatLng(gp);
        LatLng llwalkable = snapToRoad(activityContext, ll);
        return bounds.latLngtoGamePoint(llwalkable);
    }

    public void initializeAndStartRunning() {
        if (gameWorldThread == null || !gameWorldThread.isAlive()) {
            gameWorldThread = new GameWorldThread(this);
            gameWorldThread.start();
            speakTTS("Game started.");
        }
    }

    public void stopRunning() {
        if (gameWorldThread != null) {
            gameWorldThread.stopRunning();
        }
    }

    private StraightRunnerAI generateNewRunnerAI() {
        GamePoint startPoint = bounds.getRandomPointOnBoundary(GameBoundaries.DIRECTION.NORTH);
        GameHeading heading = bounds.getSemiRandomHeading(GameBoundaries.DIRECTION.SOUTH);

        return new StraightRunnerAI(startPoint, heading, RUNNER_SPEED);
    }

    public void tickTime(final float time) throws InterruptedException {
        runOnMainThreadBlocking(new Runnable() {
            @Override
            public void run() {
                // Generate AI
                if (srai == null) {
                    srai = generateNewRunnerAI();
                    speakTTS("New AI runner generated.");
                }

                // Move AI
                srai.tick(time);
                if (!bounds.withinBounds(srai.position)) {
                    srai.destroy();
                    srai = null;
                } else if (bounds.crowDistance(
                        bounds.latLngtoGamePoint(lastPosition),
                        srai.position) < CATCH_RUNNER_DISTANCE_METERS) {
                    googlemap.addMarker(new MarkerOptions().position(bounds.gamePointtoLatLng(srai.position))
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("CAUGHT!"));
                    srai.destroy();
                    srai = null;
                } else {
                    srai.updateMarker(googlemap, bounds);
                }

                // Check control point collisions
                for (ControlPoint cp : cplist) {
                    // Give player precedence
                    if (bounds.crowDistance(
                            bounds.latLngtoGamePoint(lastPosition),
                            cp.position) < CATCH_RUNNER_DISTANCE_METERS &&
                        cp.capturestatus != ControlPoint.CAPTURESTATUS.OURTEAM) {
                        cp.updateCaptureStatus(activityContext, ControlPoint.CAPTURESTATUS.OURTEAM);
                        speakTTS("You've captured " + cp.name + "!");
                    } else if (srai != null && bounds.crowDistance(
                            srai.position,
                            cp.position) < CATCH_RUNNER_DISTANCE_METERS &&
                            cp.capturestatus != ControlPoint.CAPTURESTATUS.ENEMYTEAM) {
                        cp.updateCaptureStatus(activityContext, ControlPoint.CAPTURESTATUS.ENEMYTEAM);
                        speakTTS("AI runner captured " + cp.name + "!");
                    }
                    cp.updateMarker(activityContext, googlemap, bounds);
                }

                // Check player exiting bounds
                if (!bounds.withinBounds(bounds.latLngtoGamePoint(lastPosition))) {
                    if (bounds.withinBounds(bounds.latLngtoGamePoint(lastLastPosition))){
                        speakTTS("You have left the boundary. Turn around!");
                    }
                }

                // Check announcements
                if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD){
                    ControlPoint cp = getClosestUncapturedControlPoint();
                    if (cp != null) {
                        speakTTS("The closest uncaptured point is " + cp.name + ", to the " +
                                bounds.directionOfAngle(bounds.angleBetweenPoints(
                                        bounds.latLngtoGamePoint(lastPosition), cp.position
                                )).getName());
                    }
                    lastAnnouncementTime = System.currentTimeMillis();
                }

                // Check win conditions and draw
                drawCurrentPosition();
                checkWinConditions();

                // Handle post-tick position model
                lastLastPosition = lastPosition;
            }
        });
    }

    private ControlPoint getClosestUncapturedControlPoint() {
        double min_distance = Double.MAX_VALUE;
        ControlPoint closest = null;
        for (ControlPoint cp : cplist) {
            if (cp.capturestatus != ControlPoint.CAPTURESTATUS.OURTEAM) {
                double distance = bounds.crowDistance(bounds.latLngtoGamePoint(lastPosition), cp.position);
                if (distance < min_distance) {
                    min_distance = distance;
                    closest = cp;
                }
            }
        }
        return closest;
    }

    private void checkWinConditions() {
        boolean capturedAll = true;
        for (ControlPoint cp : cplist) {
            if (cp.capturestatus != ControlPoint.CAPTURESTATUS.OURTEAM) capturedAll = false;
        }
        if (capturedAll) doCapturedAllWinCondition();
    }

    private void doCapturedAllWinCondition() {

        gameWorldThread.stopRunning();
        speakTTS("We've won. You've captured all the points. Thank you! See the app for your reward.");
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext);
        builder.setTitle("Victory!")
                .setMessage("You have captured all the points! Do you want to restart?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameWorld.this.initializeAndStartRunning();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activityContext.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected void drawCurrentPosition() {
        if (currentPositionCircle == null) {
            currentPositionCircle = googlemap.addCircle(new CircleOptions().center(lastPosition).radius(10f));
        } else {
            currentPositionCircle.setCenter(lastPosition);
        }
    }
}
