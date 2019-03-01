package jameswrunner.runnergame.gameworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.RunMapActivity;

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
    public static final double NAV_BEEP_PERIOD_MULTIPLIER = 2500.0 / 300.0; // millis period per meter
    StraightRunnerAI srai;
    LinkedList<ControlPoint> cplist = new LinkedList<ControlPoint>();


    ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private GameBoundaries bounds;
    private LatLng lastPosition;
    private LatLng lastLastPosition;
    private GameWorldThread gameWorldThread;
    private GameService gameService;
    private Player player;
    private GameObject navTarget;
    private Polygon gameBoundsPoly;
    private Circle gameBoundsBottomLeft;
    private long lastAnnouncementTime = 0;
    private long lastNavBeepTime = 0;

    public GameWorld(Location center, GameService gs) {
        bounds = new GameBoundaries(locationToLatLng(center), 0, GAME_HEIGHT_METERS, GAME_WIDTH_METERS);
        gameService = gs;
    }

    private void speakTTS(CharSequence speech){
        gameService.getTTSRunner().addSpeech(speech);
    }

    private void focusCameraOnBounds(){
        gameService.passMapUpdate(new RunMapActivity.MapUpdate(){
            @Override
            public void updateMap(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
                map.moveCamera(CameraUpdateFactory.zoomTo(17f));
            }
        });
    }

    public void initializeNewGame() {
        focusCameraOnBounds();
        drawGameBounds();
        try {
            generateControlPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (srai != null) {
            srai.destroy(gameService);
            srai = null;
        }
    }

    public synchronized void clearUIState() {
        clearGameBounds();
        for (GameObject go : gameObjects){
            go.clearMarkerState();
        }
    }

    public void refreshUIState() {
        focusCameraOnBounds();
        drawGameBounds();
        for (GameObject go : gameObjects){
            go.updateMarker(gameService, bounds);
        }
    }

    public void updatePlayerLocation(Location loc) {
        LatLng ll = locationToLatLng(loc);
        lastPosition = ll;
    }

    public void clearGameBounds() {
        gameBoundsPoly = null;
        gameBoundsBottomLeft = null;
    }

    public void drawGameBounds() {
        if (gameBoundsPoly != null) {
            gameBoundsPoly.remove();
        }
        gameService.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                gameBoundsPoly = map.addPolygon(
                        new PolygonOptions().addAll(bounds.getLatLngCorners())
                                .strokeColor(Color.RED));
                gameBoundsBottomLeft =map.addCircle(
                        new CircleOptions().strokeWidth(2).fillColor(Color.GREEN).radius(10).center(bounds.bottomLeft));
            }
        });
    }

    private void generateControlPoints() throws Exception {
        if (cplist.size() > 0) {
            for (ControlPoint cp : cplist) {
                cp.destroy(gameService);
            }
            cplist.clear();
        }
        List<GamePoint> controlpointpoints = new LinkedList<GamePoint>();
        //controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 2, GAME_HEIGHT_METERS * 1 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 3, GAME_HEIGHT_METERS * 2 / 6));
        //controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 3, GAME_HEIGHT_METERS * 2 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 4, GAME_HEIGHT_METERS * 3 / 6));
        //controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 4, GAME_HEIGHT_METERS * 3 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 3 / 4, GAME_HEIGHT_METERS * 3 / 6));
        //controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 2 / 3, GAME_HEIGHT_METERS * 4 / 6));
        controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 3, GAME_HEIGHT_METERS * 4 / 6));
        //controlpointpoints.add(new GamePoint(GAME_WIDTH_METERS * 1 / 2, GAME_HEIGHT_METERS * 5 / 6));

        int i = 1;
        for (GamePoint gp : controlpointpoints) {
            cplist.add(new ControlPoint(this, getNearestGamePointWalkable(gp), "Control Point Number " + i, "CP#" + i));
            i += 1;
        }
    }

    private GamePoint getNearestGamePointWalkable(GamePoint gp) throws Exception {
        LatLng ll = bounds.gamePointtoLatLng(gp);
        LatLng llwalkable = snapToRoad(gameService, ll);
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

        return new StraightRunnerAI(this, startPoint, heading, RUNNER_SPEED);
    }

    public synchronized void tickTime(final float time) {
            // Generate AI
            if (srai == null) {
                srai = generateNewRunnerAI();
                speakTTS("New AI runner generated.");
            }

            // Move AI
            srai.tick(time);
            if (!bounds.withinBounds(srai.position)) {
                srai.destroy(gameService);
                srai = null;
            } else if (bounds.crowDistance(
                    bounds.latLngtoGamePoint(lastPosition),
                    srai.position) < CATCH_RUNNER_DISTANCE_METERS) {
                srai.destroy(gameService);
                srai = null;
                speakTTS("You captured the AI runner. It ran away!");
            } else {
                srai.updateMarker(gameService, bounds);
            }

            // Check control point collisions
            for (ControlPoint cp : cplist) {
                // Give player precedence
                if (bounds.crowDistance(
                        bounds.latLngtoGamePoint(lastPosition),
                        cp.position) < CATCH_RUNNER_DISTANCE_METERS &&
                    cp.capturestatus != ControlPoint.CAPTURESTATUS.OURTEAM) {
                    cp.updateCaptureStatus(ControlPoint.CAPTURESTATUS.OURTEAM);
                    speakTTS("You've captured " + cp.name + "!");
                    refreshAnnouncement();
                } else if (srai != null && bounds.crowDistance(
                        srai.position,
                        cp.position) < CATCH_RUNNER_DISTANCE_METERS &&
                        cp.capturestatus != ControlPoint.CAPTURESTATUS.ENEMYTEAM) {
                    cp.updateCaptureStatus(ControlPoint.CAPTURESTATUS.ENEMYTEAM);
                    speakTTS("AI runner captured " + cp.name + "!");
                    refreshAnnouncement();
                }
                cp.updateMarker(gameService, bounds);
            }

            // Check nav beep
            if (srai != null) {
                double ai_distance = bounds.crowDistance(bounds.latLngtoGamePoint(lastPosition),
                        srai.position);
                gameService.getToneRunner().playTone((int) (ai_distance * NAV_BEEP_PERIOD_MULTIPLIER));
            }

            // Check announcements
            if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD){
                // Check player exiting bounds
                if (!bounds.withinBounds(bounds.latLngtoGamePoint(lastPosition))) {
                    speakTTS("You have left the boundary. Turn around!");
                }

                // Announce closest control point
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

    private void refreshAnnouncement(){
        lastAnnouncementTime = 0;
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
        builder = new AlertDialog.Builder(gameService);
        builder.setTitle("Victory!")
                .setMessage("You have captured all the points! Do you want to restart?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameWorld.this.initializeAndStartRunning();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameService.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected void drawCurrentPosition() {
        if (player == null) {
            player = new Player(this, bounds.latLngtoGamePoint(lastPosition));
        } else {
            player.position = bounds.latLngtoGamePoint(lastPosition);
        }
        player.updateMarker(gameService, bounds);
    }

    // Only called from GameObject
    public void addObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    // Only called from GameObject
    public void removeObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }

}
