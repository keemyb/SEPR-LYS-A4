package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.TaxeGame;
import gamelogic.game.Game;
import gamelogic.map.Connection;
import gamelogic.map.Junction;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.resource.Train;

import java.util.ArrayList;

public class Context {
    private TaxeGame taxeGame;
    private Stage stage;
    private Skin skin;
    private Game gameLogic;
    private RouteController routeController;
    private TopBarController topBarController;
    private ConnectionController connectionController;
    private ResourceController resourceController;
    private GoalController goalController;
    private TrainController trainController;
    private EventReplayer eventReplayer;

    public Context(Stage stage, Skin skin, TaxeGame taxeGame, Game gameLogic) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
        this.gameLogic = gameLogic;
        eventReplayer = new EventReplayer(this);
    }

    public void resetGame() {
        PlayerManager.reset();
        clearTrainActors();
        resetMapAttributes();
        resetPlayerAttributes();
    }

    private void clearTrainActors() {
        for (Player player : PlayerManager.getAllPlayers()) {
            for (Train train : player.getTrains()) {
                train.reset();
            }
        }
    }

    private void resetMapAttributes() {
        Map map = getGameLogic().getMap();

        for (Connection connection : new ArrayList<>(map.getConnections())) {
            if (connection.getOwner() != null) {
                map.removeConnection(connection);
            }
        }

        for (Station station : map.getStations()) {
            if (!(station instanceof Junction)) continue;

            Junction junction = (Junction) station;
            junction.reset();
        }
    }

    private void resetPlayerAttributes() {
        for (Player player : PlayerManager.getAllPlayers()) {
            player.reset();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public TaxeGame getTaxeGame() {
        return taxeGame;
    }

    public Game getGameLogic() {
        return gameLogic;
    }

    public RouteController getRouteController() {
        return routeController;
    }

    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public EventReplayer getEventReplayer() {
        return eventReplayer;
    }

    public GoalController getGoalController() {
        return goalController;
    }

    public void setGoalController(GoalController goalController) {
        this.goalController = goalController;
    }

    public TrainController getTrainController() {
        return trainController;
    }

    public void setTrainController(TrainController trainController) {
        this.trainController = trainController;
    }

    public ResourceController getResourceController() {
        return resourceController;
    }

    public void setResourceController(ResourceController resourceController) {
        this.resourceController = resourceController;
    }

    public void backToMainMenu() {
        eventReplayer.stop();
        resetGame();
        taxeGame.setScreen(new MainMenuScreen(taxeGame));
        getGameLogic().givePlayersGoalAndTrains();
    }
}
