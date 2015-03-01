package fvs.taxe.controller;

import fvs.taxe.TaxeGame;
import gamelogic.game.GameState;
import gamelogic.map.Connection;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionController {
    Context context;
    Map map;

    Queue<Station> selectedStations = new ConcurrentLinkedQueue<>();
    Connection selectedConnection;


    public ConnectionController(Context context) {
        this.context = context;
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


    public void selectStation(Station station) {
        GameState state = context.getGameLogic().getState();
        if (!state.equals(GameState.CONNECTION_CREATE)
                && !state.equals(GameState.CONNECTION_EDIT)) {
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
        } else {
            selectedConnection = map.getConnectionBetween(selectedStations.poll(), selectedStations.poll());
        }
    }

    private void createConnection() {
        if (selectedConnection == null) {
            context.getTopBarController().displayFlashMessage(
                    "You have not selected two stations", Color.BLACK, 1000);
        } else if (map.prospectiveConnectionIsValid(selectedConnection)) {
            map.addConnection(selectedConnection);
            PlayerManager.getCurrentPlayer().addOwnedConnection(selectedConnection);
            PlayerManager.getCurrentPlayer().spendMoney(selectedConnection.calculateCost());
        } else {
            context.getTopBarController().displayFlashMessage(
                    "Cannot Create a Connection Here", Color.BLACK, 1000);
        }
    }

    private void upgradeConnection() {
        if (checkConnectionStatus()) {
            System.out.println("Upgraded to GOLD");
            selectedConnection.upgrade(Connection.Material.GOLD);
            PlayerManager.getCurrentPlayer().spendMoney(selectedConnection.calculateUpgradeCost(Connection.Material.GOLD));
            clearSelected();
        }
    }

    private void repairConnection() {
        if (checkConnectionStatus()) {
            System.out.println("Repaired connection");
            selectedConnection.repair();
            PlayerManager.getCurrentPlayer().spendMoney(selectedConnection.calculateRepairCost());
            clearSelected();
        }
    }

    private void removeConnection() {
        if (checkConnectionStatus()) {
            map.removeConnection(selectedConnection);
            clearSelected();
        }
    }

    public boolean checkConnectionStatus() {
        if (selectedConnection == null) {
            context.getTopBarController().displayFlashMessage(
                    "No connection selected", Color.BLACK, 1000);
            return false;
        } else if (selectedConnection.getOwner() == null) {
            context.getTopBarController().displayFlashMessage(
                    "Cannot Remove Connection selected", Color.BLACK, 1000);
            return false;
        } else if (selectedConnection.getOwner() != PlayerManager.getCurrentPlayer()) {
            context.getTopBarController().displayFlashMessage(
                    "You do not own this connection", Color.BLACK, 1000);
            return false;
        }
        return true;
    }

    public void clearSelected() {
        selectedStations.clear();
        selectedConnection = null;
    }

    private void endConnectionModifications() {
        context.getGameLogic().setState(GameState.NORMAL);
        context.getTopBarController().clearFlashMessage();
    }

    public void enterCreateConnectionMode() {
        context.getGameLogic().setState(GameState.CONNECTION_CREATE);
    }
    public void enterEditConnectionMode() {
        context.getGameLogic().setState(GameState.CONNECTION_EDIT);
    }
}
