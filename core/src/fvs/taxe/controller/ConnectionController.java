package fvs.taxe.controller;

import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.resource.Train;

public class ConnectionController {
    private ConnectionController() {}

    public static void payRent(Train train, Connection connection) {
        Player payer = train.getPlayer();
        payer.payConnectionRent(connection);
    }
}
