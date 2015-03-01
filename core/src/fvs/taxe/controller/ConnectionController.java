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


    public void enterCreateConnectionMode() {
        context.getGameLogic().setState(GameState.CONNECTION_CREATE);
    }
    public void enterEditConnectionMode() {
        context.getGameLogic().setState(GameState.CONNECTION_EDIT);
    }
}
