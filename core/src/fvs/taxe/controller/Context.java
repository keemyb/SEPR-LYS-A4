package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.TaxeGame;
import gamelogic.game.Game;
import gamelogic.recording.RecordStateManager;

public class Context {
    private TaxeGame taxeGame;
    private Stage stage;
    private Skin skin;
    private Game gameLogic;
    private RouteController routeController;
    private TopBarController topBarController;
    private ConnectionController connectionController;
    private ReplayController replayController;
    private ResourceController resourceController;
    private GoalController goalController;
    private TrainController trainController;

    public Context(Stage stage, Skin skin, TaxeGame taxeGame, Game gameLogic) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
        this.gameLogic = gameLogic;
        replayController = new ReplayController(this);
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

    public RecordStateManager getRecordStateManager() {
        return replayController.getRecordStateManager();
    }

    public ReplayController getReplayController() {
        return replayController;
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
}
