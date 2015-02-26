package fvs.taxe.controller;

import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;

import java.util.List;

public class ConnectionController {
    private static List<Player> players = PlayerManager.getAllPlayers();

    private ConnectionController() {}

    private static void payRent(Train train, Connection connection) {
        Player payer = train.getPlayer();
        for (Player payee : players) {
            if (payer.equals(payee)) continue;
            if (payee.ownsConnection(connection)) {
                payer.payConnectionRent(payee, connection);
                return;
            }
        }
    }
}
