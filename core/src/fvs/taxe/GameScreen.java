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
import fvs.taxe.dialog.DialogEndGame;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;
import gamelogic.game.TurnListener;
import gamelogic.map.Map;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;

/**
 * This class represents screen shown while players are playing against each other.
 */
public class GameScreen extends ScreenAdapter {
    public static final float ANIMATION_TIME = 1.05f;
    final private TaxeGame game;
    private Stage stage;
    private Texture mapTexture;
    private Game gameLogic;
    private Skin skin;
    private Map map;
    private float timeAnimated = 0;
    private Tooltip tooltip;
    private Context context;

    private StationController stationController;
    private TopBarController topBarController;
    private ResourceController resourceController;
    private GoalController goalController;
    private RouteController routeController;

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

        stationController = new StationController(context, tooltip);
        topBarController = new TopBarController(context);
        resourceController = new ResourceController(context);
        goalController = new GoalController(context);
        routeController = new RouteController(context);

        context.setRouteController(routeController);
        context.setTopBarController(topBarController);

        PlayerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                map.handleJunctionFailures();
                gameLogic.setState(GameState.ANIMATING);
                topBarController.displayFlashMessage("Time is passing...", Color.BLACK, 0.9f);
                if (map.getLastBroken() != null) {
                    topBarController.displayFlashMessage("There is a junction failure at " + map.getLastBroken().getName(), Color.RED, 0.9f);
                }
            }
        });
        gameLogic.subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (PlayerManager.getTurnNumber() == gameLogic.totalTurns && state == GameState.NORMAL) {
                    DialogEndGame dia = new DialogEndGame(GameScreen.this.game, skin);
                    dia.show(stage);
                }
                if (gameLogic.getState() != GameState.ANIMATING) {
                    topBarController.displayMessage("Player " + PlayerManager.getCurrentPlayer().getPlayerNumber() + ": " + Game.CURRENCY_SYMBOL + PlayerManager.getCurrentPlayer().getMoney(), Color.BLACK);
                }
            }
        });
        PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                topBarController.displayMessage("Player " + PlayerManager.getCurrentPlayer().getPlayerNumber() + ": " + Game.CURRENCY_SYMBOL + PlayerManager.getCurrentPlayer().getMoney(), Color.BLACK);
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

        stationController.renderConnections(map.getConnections(), Color.GRAY);

        if (gameLogic.getState() == GameState.ROUTING) {
            routeController.drawRoute(Color.BLACK);
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

        resourceController.drawHeaderText();
        goalController.showCurrentPlayerGoals();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        game.batch.begin();
        float x = 10, y = 80;
        for (Player p: PlayerManager.getAllPlayers()) {
            game.fontSmall.draw(game.batch, "Player " + p.getPlayerNumber() + ": " + Game.CURRENCY_SYMBOL + p.getMoney(), x, y);
            y -= 20;
        }
        int turn = PlayerManager.getTurnNumber() + 1;
        if (turn > gameLogic.totalTurns) turn = gameLogic.totalTurns;
        game.fontSmall.draw(game.batch, "Turn " + turn + "/" + gameLogic.totalTurns, x, y);
        game.batch.end();
    }

    @Override
    public void show() {
        stationController.renderStations();
        topBarController.addEndTurnButton();
        resourceController.drawPlayerResources(PlayerManager.getCurrentPlayer());
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