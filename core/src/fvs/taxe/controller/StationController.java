package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.JunctionActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.DialogMultipleTrain;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.map.Junction;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationController {
    /*
    have to use CopyOnWriteArrayList because when we iterate through our listeners and execute
    their handler's method, one case unsubscribes from the event removing itself from this list
    and this list implementation supports removing elements whilst iterating through it
    */
    private static List<StationClickListener> stationClickListeners = new CopyOnWriteArrayList<>();
    private Context context;
    private Tooltip tooltip;

    public StationController(final Context context, Tooltip tooltip) {
        this.context = context;
        this.tooltip = tooltip;

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.SELECTED_STATION) {
                    Station station = (Station) object;
                    clickedStation(station);
                }
            }
        });

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.BROKEN_JUNCTION) {
                    Junction junction = (Junction) object;
                    context.getGameLogic().getMap().breakJunction(junction);
                }
            }
        });
    }

    public static void subscribeStationClick(StationClickListener listener) {
        stationClickListeners.add(listener);
    }

    public static void unsubscribeStationClick(StationClickListener listener) {
        stationClickListeners.remove(listener);
    }

    private void clickedStation(Station station) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_STATION, station));

        // Saving the state in case it is changed by a listener.
        GameState gameState = context.getGameLogic().getState();

        for (StationClickListener listener : stationClickListeners) {
            listener.clicked(station);
        }

        if (gameState == GameState.NORMAL) {
            Player currentPlayer = PlayerManager.getCurrentPlayer();

            List<Train> trainsAtStation = station.getTrainsAtStation();
            int numberOfTrains = trainsAtStation.size();

            if (numberOfTrains == 1) {
                Train onlyTrain = trainsAtStation.get(0);
                if (onlyTrain.isOwnedBy(currentPlayer)){
                    context.getRouteController().beginRouting(onlyTrain);
                } else {
                    context.getTopBarController().displayFlashMessage("Opponent's " + onlyTrain.getName() + ". Speed: " + onlyTrain.getSpeed(), Color.RED, 2);
                }
            } else if (numberOfTrains > 1) {
                new DialogMultipleTrain(station, context).show(context.getStage());
            }
        }

    }

    private void renderStation(final Station station) {
        final StationActor stationActor = new StationActor(station);

        stationActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickedStation(station);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(station.getLocation().getX() -(tooltip.getWidth()/2), station.getLocation().getY() + 12);
                tooltip.show(station.getName());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                tooltip.hide();
            }
        });

        station.setActor(stationActor);

        context.getStage().addActor(stationActor);
    }

    private void renderJunction(final Junction junction) {
        final JunctionActor junctionActor = new JunctionActor(junction.getLocation());

        junctionActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickedStation(junction);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(junction.getLocation().getX() -(tooltip.getWidth()/2), junction.getLocation().getY() + 10);
                tooltip.show(junction.getName());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                tooltip.hide();
            }
        });

        junction.setActor(junctionActor);

        context.getStage().addActor(junctionActor);
    }

    public void renderStations() {
        List<Station> stations = context.getGameLogic().getMap().getStations();

        for (Station station : stations) {
            if (station instanceof Junction) {
                renderJunction((Junction) station);
            } else {
                renderStation(station);
            }
        }
    }

    public void displayNumberOfTrainsAtStations() {
        TaxeGame game = context.getTaxeGame();
        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);

        for (Station station : context.getGameLogic().getMap().getStations()) {
            if (trainsAtStation(station) > 0) {
                game.fontSmall.draw(game.batch, trainsAtStation(station) + "", station.getLocation().getX() - 6, station.getLocation().getY() + 26);
            }
        }

        game.batch.end();
    }

    private int trainsAtStation(Station station) {
        int count = 0;

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Train train : player.getTrains()) {
                TrainActor trainActor = train.getActor();
                if (trainActor == null) continue;

                if (train.getPosition().equals(station.getLocation())) {
                    count++;
                }
            }
        }

        return count;
    }
}
