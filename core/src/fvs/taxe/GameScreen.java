package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import fvs.taxe.controller.*;
import fvs.taxe.dialog.DialogGameOver;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;
import gamelogic.game.TurnListener;
import gamelogic.map.Map;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;

/**
 * This class represents screen shown while players are playing against each other.
 */
public class GameScreen extends ScreenAdapter {
    public static final float ANIMATION_TIME = 1.05f;
    public static final int BUTTON_PADDING_X = 10;
    final private TaxeGame game;
    private Stage stage;
    private Texture mapTexture;
    private Game gameLogic;
    private Skin skin;
    private Map map;
    private float timeAnimated = 0;
    private Tooltip tooltip;
    private Context context;

    private ConnectionController connectionController;
    private GoalController goalController;
    private ResourceController resourceController;
    private RouteController routeController;
    private StationController stationController;
    private TopBarController topBarController;
    private TrainController trainController;

    public GameScreen(TaxeGame game) {
        this.game = game;
        stage = new Stage(new StretchViewport(TaxeGame.WORLD_WIDTH, TaxeGame.WORLD_HEIGHT));
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        gameLogic = Game.getInstance();
        context = new Context(stage, skin, game, gameLogic);
        Gdx.input.setInputProcessor(stage);

        mapTexture = new Texture(Gdx.files.internal("game-map.png"));
        map = gameLogic.getMap();

        tooltip = new Tooltip(skin);
        stage.addActor(tooltip);

        connectionController = new ConnectionController(context);
        goalController = new GoalController(context);
        resourceController = new ResourceController(context);
        routeController = new RouteController(context);
        stationController = new StationController(context, tooltip);
        topBarController = new TopBarController(context);
        trainController = new TrainController(context);

        context.setConnectionController(connectionController);
        context.setGoalController(goalController);
        context.setResourceController(resourceController);
        context.setRouteController(routeController);
        context.setTopBarController(topBarController);
        context.setTrainController(trainController);

        PlayerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                map.handleJunctionFailures();
                gameLogic.setState(GameState.ANIMATING);
                if (map.getLastBroken() != null) {
                    topBarController.displayFlashMessage("There is a junction failure at " + map.getLastBroken().getName(), Color.RED, 0.9f);
                }else {
                    topBarController.displayFlashMessage("Time is passing...", Color.BLACK, 0.9f);
                }

            }
        });

        gameLogic.subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (PlayerManager.getTurnNumber() == gameLogic.totalTurns && state == GameState.NORMAL) {
                    DialogGameOver dialogGameOver = new DialogGameOver(context, skin);
                    dialogGameOver.show(stage);
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(mapTexture, 0, 0, TaxeGame.WORLD_WIDTH, TaxeGame.WORLD_HEIGHT);
        game.batch.end();

        topBarController.drawBackground();

        routeController.drawHoveredRoute();
        connectionController.renderConnections();

        if (gameLogic.getState() == GameState.ROUTING) {
            routeController.drawPlannedRoute();
        }

        if (gameLogic.getState() == GameState.ANIMATING) {
            timeAnimated += delta;
            if (timeAnimated >= ANIMATION_TIME) {
                gameLogic.setState(GameState.NORMAL);
                timeAnimated = 0;
            }
        }

        if (gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING) {
            stationController.displayNumberOfTrainsAtStations();
        }

        if (gameLogic.getState() == GameState.CONNECTION_CREATE) {
            connectionController.drawSelectedStations();
            connectionController.drawSelectedConnection();
        }

        resourceController.refresh();
        goalController.refresh();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        game.batch.begin();
        float x = 10, y = 80;
        Player currentPlayer = PlayerManager.getCurrentPlayer();
        Color originalColor = game.fontSmall.getColor();
        for (Player player: PlayerManager.getAllPlayers()) {
            if (player.equals(currentPlayer)) {
                game.fontSmall.setColor(Color.WHITE);
            } else {
                game.fontSmall.setColor(Color.BLACK);
            }
            game.fontSmall.draw(game.batch, "Player " + player.getPlayerNumber() + ": " + Game.CURRENCY_SYMBOL + player.getMoney() + "  ", x, y);
            y -= 20;
        }
        game.fontSmall.setColor(originalColor);

        int turn = PlayerManager.getTurnNumber() + 1;
        if (turn > gameLogic.totalTurns) turn = gameLogic.totalTurns;
        game.fontSmall.draw(game.batch, "Turn " + turn + "/" + gameLogic.totalTurns, x, y);
        game.batch.end();
    }

    @Override
    public void show() {
        stationController.renderStations();
        resourceController.refresh();
    }

    @Override
    public void dispose() {
        mapTexture.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

}