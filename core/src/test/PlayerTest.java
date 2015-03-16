package test;

import gamelogic.game.Game;
import gamelogic.map.Connection;
import gamelogic.map.Map;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {
    private Game game;

    Player player1;
    Player player2;

    Station testStation1 = new Station("Test Station 1", new Position(0, 0));
    Station testStation2 = new Station("Test Station 2", new Position(10, 10));
    Station testStation3 = new Station("Test Station 3", new Position(20, 20));

    Connection player1Connection = new Connection(testStation1, testStation2, Connection.Material.BRONZE);
    Connection player2Connection = new Connection(testStation2, testStation3, Connection.Material.BRONZE);
    // Not owned by any player
    Connection freeConnection = new Connection(testStation1, testStation3, Connection.Material.BRONZE);

    @Before
    public void setUp() throws Exception {
        game = Game.getInstance();

        player1 = PlayerManager.getAllPlayers().get(0);
        player2 = PlayerManager.getAllPlayers().get(1);

        player1.addOwnedConnection(player1Connection);
        player2.addOwnedConnection(player2Connection);
    }

    @Test
    public void testPayConnectionOwnedConnection() throws Exception {
        int startingPlayer1Funds = player1.getMoney();
        int startingPlayer2Funds = player2.getMoney();

        player1.payConnectionRent(player1Connection);

        assertEquals(startingPlayer1Funds, player1.getMoney());
        assertEquals(startingPlayer2Funds, player2.getMoney());
    }

    @Test
    public void testPayConnectionNotOwnedConnection() throws Exception {
        int startingPlayer1Funds = player1.getMoney();
        int startingPlayer2Funds = player2.getMoney();

        player1.payConnectionRent(player2Connection);

        int remainingPlayer1Funds = player1.getMoney();
        int remainingPlayer2Funds = player2.getMoney();

        int deltaPlayer1Funds = startingPlayer1Funds - remainingPlayer1Funds;
        int deltaPlayer2Funds = startingPlayer2Funds - remainingPlayer2Funds;

        assertTrue(startingPlayer1Funds > remainingPlayer1Funds);
        assertTrue(remainingPlayer2Funds > startingPlayer2Funds);
        assertEquals(0, deltaPlayer1Funds + deltaPlayer2Funds);
    }

    @Test
    public void testPayConnectionFreeConnection() throws Exception {
        int startingPlayer1Funds = player1.getMoney();
        int startingPlayer2Funds = player2.getMoney();

        player1.payConnectionRent(freeConnection);

        assertEquals(startingPlayer1Funds, player1.getMoney());
        assertEquals(startingPlayer2Funds, player2.getMoney());
    }

    @Test
    public void testPlayerSpendMoney() throws Exception {
        int startingPlayer1Funds = player1.getMoney();
        int amountToSpend = 50;
        int expectedPlayer1Funds = startingPlayer1Funds - amountToSpend;

        player1.spendMoney(amountToSpend);

        assertEquals(expectedPlayer1Funds, player1.getMoney());
    }

    @Test
    public void testRemoveConnection() throws Exception {
        Map map = new Map();
        map.addConnection(player1Connection);
        map.removeConnection(player1Connection);

        assertTrue(player1.getConnectionsOwned().isEmpty());
    }
}