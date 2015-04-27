package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.GameScreen;
import fvs.taxe.TaxeGame;
import fvs.taxe.dialog.ConnectionClicked;
import fvs.taxe.dialog.DiscardClicked;
import fvs.taxe.dialog.TrainButtonClicked;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;
import gamelogic.goal.Goal;
import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.List;

public class ResourceController {
    private static final float MY_TRAINS_Y = (float) TaxeGame.WORLD_HEIGHT - 175.0f;
    private static final float MY_TRACKS_Y = (float) TaxeGame.WORLD_HEIGHT - 475.0f;
    private static final float SPACE_BETWEEN_RESOURCES_X = GameScreen.BUTTON_PADDING_X;
    private static final float SPACE_BETWEEN_RESOURCES_Y = 30f;
    private static final float SPACE_AFTER_HEADER_RESOURCES_Y = 50f;

    private Context context;
    private Group resourceButtons = new Group();

    public ResourceController(final Context context) {
        this.context = context;

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                refresh();
            }
        });

        PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                refresh();
            }
        });


        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.ADD_GOAL) {
                    handleNewGoal(object);
                } else if (event == GameEvent.ADD_TRAIN) {
                    handleNewTrain(object);
                } else if (event == GameEvent.ADD_CONNECTION) {
                    handleNewConnection(object);
                }
            }
        });
    }

    private void handleNewGoal(Object object) {
        List<Object> playerAndGoal = (List<Object>) object;
        Player player = (Player) playerAndGoal.get(0);
        Goal goal = (Goal) playerAndGoal.get(1);

        goal.reset();
        goal.updateGoal();

        if (!player.getGoals().contains(goal)) {
            player.addGoal(goal);
        }
    }

    private void handleNewTrain(Object object) {
        List<Object> playerAndTrain = (List<Object>) object;
        Player player = (Player) playerAndTrain.get(0);
        Train train = (Train) playerAndTrain.get(1);

        player.removeTrain(train);
        player.addTrain(train);
    }

    private void handleNewConnection(Object object) {
        List<Object> playerAndConnection = (List<Object>) object;
        Player player = (Player) playerAndConnection.get(0);
        Connection connection = (Connection) playerAndConnection.get(1);
        Connection.Material material = (Connection.Material) playerAndConnection.get(2);

        connection.setMaterial(material);
        connection.repair(1);

        context.getGameLogic().getMap().addConnection(connection);
        player.getConnectionsOwned().add(connection);
    }

    public void refresh() {
        drawHeaderText();
        drawPlayerResources();
    }

    private void drawHeaderText() {
        TaxeGame game = context.getTaxeGame();

        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);

        boolean unplacedTrains = false;

        Player currentPlayer = PlayerManager.getCurrentPlayer();
        for (Train train : currentPlayer.getTrains()) {
            if (train.getPosition() == null) {
                unplacedTrains = true;
                break;
            }
        }

        if (unplacedTrains) {
            game.fontSmall.draw(game.batch, "My Unplaced Trains:", SPACE_BETWEEN_RESOURCES_X, MY_TRAINS_Y);
        }
        if (!PlayerManager.getCurrentPlayer().getConnectionsOwned().isEmpty()) {
            game.fontSmall.draw(game.batch, "My Tracks:", SPACE_BETWEEN_RESOURCES_X, MY_TRACKS_Y);
        }

        game.batch.end();
    }

    private void drawPlayerResources() {
        float x = SPACE_BETWEEN_RESOURCES_X;
        float y;

        Player player = PlayerManager.getCurrentPlayer();

        resourceButtons.remove();
        resourceButtons.clear();

        y = MY_TRAINS_Y - SPACE_AFTER_HEADER_RESOURCES_Y;
        for (Train train : player.getTrains()) {
            // don't show a button for trains that have been placed
            if (train.getPosition() != null) {
                continue;
            }

            DiscardClicked discardListener =  new DiscardClicked(context, train);
            
            TextButton discardButton = new TextButton(" X ", context.getSkin());
            discardButton.setPosition(x, y);
            discardButton.setColor(Color.RED);
            discardButton.addListener(discardListener);
            
            TrainButtonClicked listener = new TrainButtonClicked(context, train);

            TextButton button = new TextButton(train.getName(), context.getSkin());
            button.setPosition(x+discardButton.getWidth()+5, y);
            button.addListener(listener);

            
            
            resourceButtons.addActor(button);
            resourceButtons.addActor(discardButton);

            y -= SPACE_BETWEEN_RESOURCES_Y;
        }

        y = MY_TRACKS_Y - SPACE_AFTER_HEADER_RESOURCES_Y;
        for (Connection connection : player.getConnectionsOwned()) {
            ConnectionClicked listener = new ConnectionClicked(context, connection);

            TextButton button = new TextButton(context.getConnectionController().getConnectionHealthString(connection), context.getSkin());
            button.setPosition(x, y);
            button.addListener(listener);

            resourceButtons.addActor(button);
            y -= SPACE_BETWEEN_RESOURCES_Y;
        }

        context.getStage().addActor(resourceButtons);
    }
}
