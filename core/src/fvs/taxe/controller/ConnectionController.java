package fvs.taxe.controller;

import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.resource.Train;

public class ConnectionController {
    Context context;

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
