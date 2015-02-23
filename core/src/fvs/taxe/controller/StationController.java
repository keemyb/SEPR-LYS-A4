package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.JunctionActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.dialog.DialogMultitrain;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.player.Player;
import gamelogic.map.Connection;
import gamelogic.map.Junction;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationController {
    public final static int CONNECTION_LINE_WIDTH = 3;
    /*
    have to use CopyOnWriteArrayList because when we iterate through our listeners and execute
    their handler's method, one case unsubscribes from the event removing itself from this list
    and this list implementation supports removing elements whilst iterating through it
    */
    private static List<StationClickListener> stationClickListeners = new CopyOnWriteArrayList<StationClickListener>();
    private Context context;
    private Tooltip tooltip;

    public StationController(Context context, Tooltip tooltip) {
        this.context = context;
        this.tooltip = tooltip;
    }

    public static void subscribeStationClick(StationClickListener listener) {
        stationClickListeners.add(listener);
    }

    public static void unsubscribeStationClick(StationClickListener listener) {
        stationClickListeners.remove(listener);
    }

    private static void stationClicked(Station station) {
        for (StationClickListener listener : stationClickListeners) {
            listener.clicked(station);
        }
    }

    private void renderStation(final Station station) {
        final StationActor stationActor = new StationActor(station);

        stationActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Game.getInstance().getState() == GameState.NORMAL) {
                    DialogMultitrain dia = new DialogMultitrain(station, context.getSkin(), context);
                    if (dia.getIsTrain()) {
                        dia.show(context.getStage());
                    }
                    else {
                        System.out.println("no trains here");
                    }
                }
                stationClicked(station);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(stationActor.getX() + 20, stationActor.getY() + 20);
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

    private void renderJunction(final Station junction) {
        final JunctionActor junctionActor = new JunctionActor(junction.getLocation());

        junctionActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stationClicked(junction);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(junctionActor.getX() + 10, junctionActor.getY() + 10);
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
                renderJunction(station);
            } else {
                renderStation(station);
            }
        }
    }

    public void renderConnections(List<Connection> connections, Color color) {
        TaxeGame game = context.getTaxeGame();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(color);

        for (Connection connection : connections) {
            Position start = connection.getStation1().getLocation();
            Position end = connection.getStation2().getLocation();
            game.shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), CONNECTION_LINE_WIDTH);
        }
        game.shapeRenderer.end();
    }

    public void displayNumberOfTrainsAtStations() {
        TaxeGame game = context.getTaxeGame();
        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);

        for (Station station : context.getGameLogic().getMap().getStations()) {
            if (trainsAtStation(station) > 0) {
                game.fontSmall.draw(game.batch, trainsAtStation(station) + "", (float) station.getLocation().getX() - 6, (float) station.getLocation().getY() + 26);
            }
        }

        game.batch.end();
    }

    private int trainsAtStation(Station station) {
        int count = 0;

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getResources()) {
                if (resource instanceof Train) {
                    if (((Train) resource).getActor() != null) {
                        if (((Train) resource).getPosition().equals(station.getLocation())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }
}
