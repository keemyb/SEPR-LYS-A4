package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.TaxeGame;
import fvs.taxe.dialog.ConnectionClicked;
import fvs.taxe.dialog.TrainClicked;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

public class ResourceController {
    private Context context;
    private Group resourceButtons = new Group();

    public ResourceController(final Context context) {
        this.context = context;

        PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                drawPlayerResources(PlayerManager.getCurrentPlayer());
            }
        });
    }

    public void drawHeaderText() {
        TaxeGame game = context.getTaxeGame();

        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);
        if (Game.getInstance().getState() == GameState.CONNECTION_EDIT) {
            game.fontSmall.draw(game.batch, "My Tracks:", 10.0f, (float) TaxeGame.WORLD_HEIGHT - 250.0f);
        }else {
            game.fontSmall.draw(game.batch, "Unplaced Resources:", 10.0f, (float) TaxeGame.WORLD_HEIGHT - 250.0f);
        }
        game.batch.end();
    }

    public void drawPlayerResources(Player player) {
        System.out.println("4");

        float top = (float) TaxeGame.WORLD_HEIGHT;
        float x = 10.0f;
        float y = top - 250.0f;
        y -= 50;

        resourceButtons.remove();
        resourceButtons.clear();
        if (Game.getInstance().getState() != GameState.CONNECTION_EDIT) {
            for (final Resource resource : player.getTrains()) {

                if (resource instanceof Train) {
                    Train train = (Train) resource;

                    // don't show a button for trains that have been placed
                    if (train.getPosition() != null) {
                        continue;
                    }

                    TrainClicked listener = new TrainClicked(context, train);

                    TextButton button = new TextButton(resource.toString(), context.getSkin());
                    button.setPosition(x, y);
                    button.addListener(listener);

                    resourceButtons.addActor(button);

                    y -= 30;
                }
            }
        }
        if (Game.getInstance().getState() == GameState.CONNECTION_EDIT){
            resourceButtons.clear();
            if (player.getConnectionsOwned() != null) {
                for (Connection connection : player.getConnectionsOwned()) {
                    TextButton button = new TextButton(connection.toString(), context.getSkin());
                    button.setPosition(x, y);

                    ConnectionClicked listener = new ConnectionClicked(context, connection);
                    button.addListener(listener);

                    resourceButtons.addActor(button);
                    y -= 30;
                }
            }

        }

        context.getStage().addActor(resourceButtons);
    }
}
