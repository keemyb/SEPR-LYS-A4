package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.actor.JunctionActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.dialog.*;
import gamelogic.game.Game;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.map.Connection;
import gamelogic.map.Junction;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionController {
    Context context;
    Map map;
    TaxeGame game;

    Queue<Station> selectedStations = new ConcurrentLinkedQueue<>();
    Connection selectedConnection;

    private TextButton createConnection;
    private TextButton done;
    private Group connectionButtons = new Group();

    private float relativeStationHighlightSize = 1.7f;

    private Color selectedStationColour = new Color(255/255.0f, 137/255.0f, 0f, 0.70f);
    private Color validTemporaryConnectionColour = new Color(0.2f, 1f, 0.2f, 1f);
    private Color invalidTemporaryConnectionColour = new Color(1f, 0f, 0f, 1f);
    private Color existingConnectionColour = new Color(0f, 0f, 1f, 1f);

    private static final int MAX_NEW_CONNECTIONS_PER_TURN = 1;
    private int numberOfNewConnectionsThisTurn = 0;

    public ConnectionController(Context context) {
        this.context = context;
        game = context.getTaxeGame();
        createConnection = new TextButton("Create Connection", context.getSkin());
        done = new TextButton("Done", context.getSkin());

        map = context.getGameLogic().getMap();

        StationController.subscribeStationClick(new StationClickListener() {
            @Override
            public void clicked(Station station) {
                selectStation(station);
            }
        });

        createConnection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createConnection();
            }
        });

        done.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endConnectionModifications();
            }
        });

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_ADD_CONNECTION_MODE) {
                    enterCreateConnectionMode();
                } else if (event == GameEvent.CLICKED_ADD_CONNECTION_MODE_DONE) {
                    endConnectionModifications();
                } else if (event == GameEvent.CLICKED_CONNECTION_BUTTON) {
                    Connection connection = (Connection) object;
                    setSelectedConnection(connection);
                } else if (event == GameEvent.CLICKED_STATION) {
                    Station station = (Station) object;
                    selectStation(station);
                } else if (event == GameEvent.CLICKED_NEW_CONNECTION) {
                    createConnection();
                } else if (event == GameEvent.CLICKED_CHOOSE_NEW_CONNECTION_MATERIAL) {
                    List<Object> connectionAndMaterial = (List) object;
                    Connection connection = (Connection) connectionAndMaterial.get(0);
                    Connection.Material material = (Connection.Material) connectionAndMaterial.get(1);
                    createConnection(connection, material);
                } else if (event == GameEvent.CLICKED_UPGRADE_CONNECTION) {
                    showUpgradeConnectionDialog();
                } else if (event == GameEvent.CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL) {
                    List<Object> connectionAndMaterial = (List) object;
                    Connection connection = (Connection) connectionAndMaterial.get(0);
                    Connection.Material material = (Connection.Material) connectionAndMaterial.get(1);
                    upgradeConnection(connection, material);
                } else if (event == GameEvent.CLICKED_REPAIR_CONNECTION) {
                    showRepairConnectionDialog();
                } else if (event == GameEvent.CLICKED_CHOOSE_REPAIR_CONNECTION_AMOUNT) {
                    List<Object> connectionAndRepairAmount = (List) object;
                    Connection connection = (Connection) connectionAndRepairAmount.get(0);
                    float repairAmount = (float) connectionAndRepairAmount.get(1);
                    repairConnection(connection, repairAmount);
                } else if (event == GameEvent.CLICKED_REMOVE_CONNECTION) {
                    showRemoveConnectionDialog();
                } else if (event == GameEvent.CLICKED_CHOOSE_REMOVE_CONNECTION) {
                    Connection connection = (Connection) object;
                    removeConnection(connection);
                }
            }
        });

        PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                numberOfNewConnectionsThisTurn = 0;
            }
        });
    }

    public static void payRent(Train train, Connection connection) {
        Player payer = train.getPlayer();
        payer.payConnectionRent(connection);
    }

    public static void damageConnection(Train train, Connection connection) {
        connection.inflictDamage(train);
    }

    public static void visitedConnection(Train train, Connection visited) {
        payRent(train, visited);
        damageConnection(train, visited);
    }

    private void showOptionButtons() {
        GameState state = context.getGameLogic().getState();

        connectionButtons.clearChildren();
        done.setPosition(TaxeGame.WORLD_WIDTH - 70, TaxeGame.WORLD_HEIGHT - 33);
        connectionButtons.addActor(done);

        if (state.equals(GameState.CONNECTION_CREATE) && (selectedConnection != null)) {
            if (map.prospectiveConnectionIsValid(selectedConnection)) {
                createConnection.setPosition(TaxeGame.WORLD_WIDTH - 230, TaxeGame.WORLD_HEIGHT - 33);
                connectionButtons.addActor(createConnection);
            }
        }

        context.getStage().addActor(connectionButtons);
    }

    private void selectStation(Station station) {
        GameState state = context.getGameLogic().getState();
        if (!state.equals(GameState.CONNECTION_CREATE)) {
            return;
        }
        if (selectedStations.contains(station)) {
            return;
        }

        if (selectedStations.size() == 2) {
            selectedStations.remove();
        }
        selectedStations.add(station);
        selectConnection();
    }

    private void selectConnection() {
        if (selectedStations.size() != 2) return;

        GameState state = context.getGameLogic().getState();

        if (state.equals(GameState.CONNECTION_CREATE)) {
            selectedConnection = new Connection(selectedStations.poll(), selectedStations.poll(), Connection.Material.BRONZE);
            showOptionButtons();
        } else {
            selectedConnection = map.getConnectionBetween(selectedStations.poll(), selectedStations.poll());
            new DialogChooseConnectionAction(selectedConnection, context).show(context.getStage());
        }
    }

    private void createConnection() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_NEW_CONNECTION));
        if (selectedConnection == null) {
            context.getTopBarController().displayFlashMessage(
                    "You have not selected two stations", Color.BLACK, 1000);
        } else if (map.prospectiveConnectionIsValid(selectedConnection)) {
            new DialogCreateConnection(selectedConnection, context.getSkin(), context).show(context.getStage());
        } else {
            context.getTopBarController().displayFlashMessage(
                    "Cannot Create a Connection Here", Color.BLACK, 1000);
        }
    }

    public void showUpgradeConnectionDialog() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_UPGRADE_CONNECTION));
        new DialogUpgradeConnection(selectedConnection, context.getSkin(), context).show(context.getStage());
    }

    public void showRepairConnectionDialog() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_REPAIR_CONNECTION));
        new DialogRepairConnection(selectedConnection, context.getSkin(), context).show(context.getStage());
    }

    public void showRemoveConnectionDialog() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_REMOVE_CONNECTION));
        new DialogRemoveConnection(context, selectedConnection).show(context.getStage());
    }

    private void clearSelected() {
        selectedStations.clear();
        selectedConnection = null;
    }

    public void drawSelectedStations() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(selectedStationColour);

        int width;

        for (Station station : selectedStations) {
            if (station instanceof Junction) {
                width = (int) (JunctionActor.width * relativeStationHighlightSize) / 2;
            } else {
                width = (int) (StationActor.width * relativeStationHighlightSize) / 2;
            }
            game.shapeRenderer.circle(station.getLocation().getX(),
                    station.getLocation().getY(), width);
        }

        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawSelectedConnection() {
        if (selectedConnection == null) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (context.getGameLogic().getState().equals(GameState.CONNECTION_CREATE)) {
            if (map.prospectiveConnectionIsValid(selectedConnection)) {
                game.shapeRenderer.setColor(validTemporaryConnectionColour);
            } else {
                game.shapeRenderer.setColor(invalidTemporaryConnectionColour);
            }
        } else {
            game.shapeRenderer.setColor(existingConnectionColour);
        }

        game.shapeRenderer.rectLine(selectedConnection.getStation1().getLocation().getX(),
                selectedConnection.getStation1().getLocation().getY(),
                selectedConnection.getStation2().getLocation().getX(),
                selectedConnection.getStation2().getLocation().getY(),
                StationController.CONNECTION_LINE_WIDTH);

        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void endConnectionModifications() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_ADD_CONNECTION_MODE_DONE));
        clearSelected();
        context.getGameLogic().setState(GameState.NORMAL);
        connectionButtons.remove();
        context.getTopBarController().clearFlashMessage();
    }

    public void enterCreateConnectionMode() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_ADD_CONNECTION_MODE));
        if (numberOfNewConnectionsThisTurn == MAX_NEW_CONNECTIONS_PER_TURN) {
            String message = "You can only make " + MAX_NEW_CONNECTIONS_PER_TURN + " connection";
            if (MAX_NEW_CONNECTIONS_PER_TURN > 1) {
                message += "s";
            }
            message += " per turn";
            context.getTopBarController().displayFlashMessage(message, Color.BLACK, 1000);
        } else {
            clearSelected();
            context.getGameLogic().setState(GameState.CONNECTION_CREATE);
            showOptionButtons();
        }
    }

    public void setSelectedConnection(gamelogic.map.Connection connection){
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CONNECTION_BUTTON, connection));
        Station station1 = connection.getStation1();
        Station station2 = connection.getStation2();
        if (selectedStations.size() == 2) {
            selectedStations.remove();
        }
        selectedStations.add(station1);
        selectedStations.add(station2);
        selectConnection();
    }
    public void highlightConnection(Connection connection) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(validTemporaryConnectionColour);

        game.shapeRenderer.rectLine(connection.getStation1().getLocation().getX(),
                connection.getStation1().getLocation().getY(),
                connection.getStation2().getLocation().getX(),
                connection.getStation2().getLocation().getY(),
                StationController.CONNECTION_LINE_WIDTH);

        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void createConnection(Connection connection, Connection.Material material) {
        Player currentPlayer = PlayerManager.getCurrentPlayer();
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ADD_CONNECTION, new ArrayList<>(
                Arrays.asList(currentPlayer, connection, material)
        )));
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_NEW_CONNECTION_MATERIAL, new ArrayList<>(
                Arrays.asList(connection, material)
        )));

        connection.upgrade(material);
        int connectionCost = connection.calculateCost();
        if (!Game.CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS &&
                currentPlayer.getMoney() <= connectionCost) {
            context.getTopBarController().displayFlashMessage(
                    "Not enough money to create connection", Color.BLACK, 1000);
        } else {
            numberOfNewConnectionsThisTurn++;
            context.getGameLogic().getMap().addConnection(connection);
            currentPlayer.addOwnedConnection(connection);
            currentPlayer.spendMoney(connection.calculateCost());
            System.out.println("Purchased a " + material + " connection");
        }

        endConnectionModifications();
    }

    public void repairConnection(Connection connection, float repairThreshold) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_REPAIR_CONNECTION_AMOUNT, new ArrayList<>(
                Arrays.asList(connection, repairThreshold)
        )));

        Player currentPlayer = PlayerManager.getCurrentPlayer();
        int repairCost = connection.calculateRepairCost(repairThreshold);
        if (!Game.CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS &&
                currentPlayer.getMoney() <= repairCost) {
            context.getTopBarController().displayFlashMessage(
                    "Not enough money to repair connection", Color.BLACK, 1000);
        } else {
            connection.repair(repairThreshold);
            currentPlayer.spendMoney(repairCost);
            System.out.println("Repaired a connection to " + String.valueOf(repairThreshold * 100) + "%");
        }

        endConnectionModifications();
    }

    public void upgradeConnection(Connection connection, Connection.Material material) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL, new ArrayList<>(
                Arrays.asList(connection, material)
        )));

        Player currentPlayer = PlayerManager.getCurrentPlayer();
        int upgradeCost = connection.calculateUpgradeCost(material);
        if (!Game.CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS &&
                currentPlayer.getMoney() <= upgradeCost) {
            context.getTopBarController().displayFlashMessage(
                    "Not enough money to upgrade connection", Color.BLACK, 1000);
        } else {
            connection.upgrade(material);
            currentPlayer.spendMoney(upgradeCost);
            System.out.println("Upgraded a connection to " + material);
        }

        endConnectionModifications();
    }

    public void removeConnection(Connection connection) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_REMOVE_CONNECTION, connection));

        // Added twice for some reason on replay
        while (map.getConnections().contains(connection)) {
            map.removeConnection(connection);
        }

        endConnectionModifications();
    }

    public String getConnectionHealthString(Connection connection) {
        String connectionString = connection.getStation1().getName() + " to " + connection.getStation2().getName();
        String currentHealth = String.valueOf((int) (connection.getHealth() * 100)) + '%';
        connectionString += " ( Health: " + currentHealth + " ) ";
        return connectionString;
    }
}
