package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.GameScreen;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import gamelogic.game.Game;
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

    private Group routingButtons = new Group();
    private List<Station> stations = new ArrayList<>();
    private Train hoveredTrain = null;
    private boolean isRouting = false;
    private Train train;
    private boolean canEndRouting = true;
    private float totalRouteDistance;

    public RouteController(Context context) {
        this.context = context;

        StationController.subscribeStationClick(new StationClickListener() {
            @Override
            public void clicked(Station station) {
                if (isRouting) {
                    addStationToRoute(station);
                }
            }
        });

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.ROUTING_BEGIN) {
                    beginRouting((Train) object);
                } else if (event == GameEvent.ROUTING_DISCARD_TRAIN) {
                    discardTrain();
                } else if (event == GameEvent.ROUTING_DONE) {
                    doneRouting();
                } else if (event == GameEvent.ROUTING_CANCEL) {
                    cancelRouting();
                }
            }
        });
    }

    public void beginRouting(Train train) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ROUTING_BEGIN, train));

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

            context.getTopBarController().displayFlashMessage(
                    "This route will take approximately " + turnsToCompleteChosenRoute() + " turns to complete.", Color.BLACK, 1000);

            canEndRouting = !(station instanceof Junction);
        }
    }

    private int turnsToCompleteChosenRoute() {
        float distance = totalRouteDistance;
        float trainSpeed = train.getSpeed();
        float turns = distance / trainSpeed;
        return (int) Math.ceil(turns);
    }

    private void addRoutingButtons() {
        TextButton discardTrain = new TextButton("Discard Train", context.getSkin());
        TextButton routeComplete = new TextButton("Route Complete", context.getSkin());
        TextButton cancel = new TextButton("Cancel Route", context.getSkin());

        cancel.setPosition(TaxeGame.WORLD_WIDTH - cancel.getWidth() - GameScreen.BUTTON_PADDING_X, TaxeGame.WORLD_HEIGHT - 33);
        routeComplete.setPosition(cancel.getX() - routeComplete.getWidth() - GameScreen.BUTTON_PADDING_X, TaxeGame.WORLD_HEIGHT - 33);
        discardTrain.setPosition(GameScreen.BUTTON_PADDING_X, TaxeGame.WORLD_HEIGHT - 33);

        routeComplete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doneRouting();
            }
        });

        discardTrain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                discardTrain();
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelRouting();
            }
        });

        routingButtons.addActor(discardTrain);
        routingButtons.addActor(routeComplete);
        routingButtons.addActor(cancel);

        context.getStage().addActor(routingButtons);
    }

    private void doneRouting() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ROUTING_DONE));
        if (!canEndRouting) {
            context.getTopBarController().displayFlashMessage("Your route must end at a station", Color.RED);
            return;
        }
        confirmRoute();
        endRouting();
    }

    private void discardTrain() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ROUTING_DISCARD_TRAIN));
        endRouting();
        context.getTrainController().discardTrain(train);
    }

    private void cancelRouting() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ROUTING_CANCEL));
        endRouting();
    }

    private void confirmRoute() {
        if (stations.isEmpty() || stations.get(0) == train.getLocation()) {
            cancelRouting();
        } else {
            train.setRoute(stations);

            new TrainMoveController(context, train).addMoveActionsForRoute();
        }
    }

    private void endRouting() {
        context.getGameLogic().setState(GameState.NORMAL);
        routingButtons.remove();
        isRouting = false;

        TrainController trainController = context.getTrainController();
        trainController.setTrainsVisible(train, true);
        train.getActor().setVisible(context.getGameLogic().getMap().getStationByPosition(train.getPosition()) == null);
        context.getTopBarController().clearFlashMessage();
        setHoveredTrain(null);
    }

    public void drawRoute(Color color, List<Station> stations, int width) {
        TaxeGame game = context.getTaxeGame();

        Station previousStation = null;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(color);

        for (Station station : stations) {
            if (previousStation != null) {
                game.shapeRenderer.rectLine(previousStation.getLocation().getX(), previousStation.getLocation().getY(),
                        station.getLocation().getX(), station.getLocation().getY(), width);
            } else {
                game.shapeRenderer.rectLine(train.getPosition().getX(), train.getPosition().getY(),
                        station.getLocation().getX(), station.getLocation().getY(), width);
            }
            previousStation = station;
        }

        game.shapeRenderer.end();
    }

    public void drawPlannedRoute() {
        drawRoute(Color.BLACK, stations, ConnectionController.CONNECTION_LINE_WIDTH);
    }

    public void drawHoveredRoute() {
        if (hoveredTrain == null) return;
        drawRoute(Color.WHITE, hoveredTrain.getRoute(), (int) (ConnectionController.CONNECTION_LINE_WIDTH * 2));
    }

    public void setHoveredTrain(Train hoveredTrain) {
        this.hoveredTrain = hoveredTrain;
    }
}
