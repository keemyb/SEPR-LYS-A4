package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.map.Junction;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class RouteController {
    private Context context;
    private EventReplayer eventReplayer;

    private Group routingButtons = new Group();
    private List<Station> stations = new ArrayList<>();
    private boolean isRouting = false;
    private Train train;
    private boolean canEndRouting = true;
    private float totalRouteDistance;

    public RouteController(Context context) {
        this.context = context;
        eventReplayer = context.getEventReplayer();

        StationController.subscribeStationClick(new StationClickListener() {
            @Override
            public void clicked(Station station) {
                if (isRouting) {
                    addStationToRoute(station);
                }
            }
        });

        eventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_ROUTING_DISCARD_TRAIN) {
                    discardRouting();
                } else if (event == GameEvent.CLICKED_ROUTING_DONE) {
                    doneRouting();
                } else if (event == GameEvent.CLICKED_ROUTING_CANCEL) {
                    cancelRouting();
                }
            }
        });
    }

    public void beginRouting(Train train) {
        eventReplayer.playBackReplayEvent(new ReplayEvent(GameEvent.CLICKED_BEGIN_ROUTING));

        this.train = train;
        isRouting = true;
        stations.clear();
        //initialise route distance
        totalRouteDistance = 0;
        context.getGameLogic().setState(GameState.ROUTING);
        addRoutingButtons();

        TrainController trainController = context.getTrainController();
        trainController.setTrainsVisible(train, false);
        train.getActor().setVisible(true);
    }

    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        Position lastPosition;
        if (stations.isEmpty()) {
            lastPosition = train.getPosition();
        } else {
            lastPosition = stations.get(stations.size() - 1).getLocation();
        }

        boolean hasConnection = context.getGameLogic().getMap().doesConnectionExist(lastPosition, station.getLocation());

        if (!hasConnection) {
            context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
        }
        else if (stations.contains(station)) {
            context.getTopBarController().displayFlashMessage(
                    "You can not visit the same station twice in one route.", Color.RED);
        }
        else {
            stations.add(station);

            //recalculate the total distance of the route when station/junction is added
            if (stations.size() > 1) {
                totalRouteDistance += Station.getDistance(stations.get(stations.size() - 1), stations.get(stations.size() - 2));
            }

            System.out.println("totalRouteDistance =" + totalRouteDistance);
            System.out.println("total turns = " + totalTurns());

            context.getTopBarController().displayFlashMessage(
                    "This route will take approximately " + totalTurns() + " turns to complete.", Color.BLACK, 1000);

            canEndRouting = !(station instanceof Junction);
        }
    }

    private int totalTurns() {
        //calculates how many turns it will take to complete the chosen route
        float distance = totalRouteDistance;
        float trainSpeed = train.getSpeed();
        float turns = distance / trainSpeed;
        return (int) Math.ceil(turns);
    }

    private void addRoutingButtons() {
        final TextButton doneRouting = new TextButton("Route Complete", context.getSkin());
        TextButton discard = new TextButton("Discard", context.getSkin());
        TextButton cancel = new TextButton("Cancel", context.getSkin());

        cancel.setPosition(TaxeGame.WORLD_WIDTH - 70, TaxeGame.WORLD_HEIGHT - 33);
        discard.setPosition(TaxeGame.WORLD_WIDTH - 140, TaxeGame.WORLD_HEIGHT - 33);
        doneRouting.setPosition(TaxeGame.WORLD_WIDTH - 270, TaxeGame.WORLD_HEIGHT - 33);

        doneRouting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doneRouting();
            }
        });

        discard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                discardRouting();
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelRouting();
            }
        });

        routingButtons.addActor(discard);
        routingButtons.addActor(doneRouting);
        routingButtons.addActor(cancel);

        context.getStage().addActor(routingButtons);
    }

    private void doneRouting() {
        eventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_ROUTING_DONE));
        if (!canEndRouting) {
            context.getTopBarController().displayFlashMessage("Your route must end at a station", Color.RED);
            return;
        }
        confirmed();
        endRouting();
    }

    private void discardRouting() {
        eventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_ROUTING_DISCARD_TRAIN));
        PlayerManager.getCurrentPlayer().removeTrain(train);
        endRouting();
    }

    private void cancelRouting() {
        eventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_ROUTING_CANCEL));
        endRouting();
    }

    private void confirmed() {
        train.setRoute(stations);

        TrainMoveController move = new TrainMoveController(context, train);
    }

    private void endRouting() {
        context.getGameLogic().setState(GameState.NORMAL);
        routingButtons.remove();
        isRouting = false;

        TrainController trainController = context.getTrainController();
        trainController.setTrainsVisible(train, true);
        train.getActor().setVisible(context.getGameLogic().getMap().getStationByPosition(train.getPosition()) == null);
        context.getTopBarController().clearFlashMessage();
    }

    public void drawRoute(Color color) {
        TaxeGame game = context.getTaxeGame();

        Station previousStation = null;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(color);

        for (Station station : stations) {
            if (previousStation != null) {
                game.shapeRenderer.rectLine(previousStation.getLocation().getX(), previousStation.getLocation().getY(),
                        station.getLocation().getX(), station.getLocation().getY(),
                        StationController.CONNECTION_LINE_WIDTH);
            } else {
                game.shapeRenderer.rectLine(train.getPosition().getX(), train.getPosition().getY(),
                        station.getLocation().getX(), station.getLocation().getY(),
                        StationController.CONNECTION_LINE_WIDTH);
            }
            previousStation = station;
        }

        game.shapeRenderer.end();
    }
}
