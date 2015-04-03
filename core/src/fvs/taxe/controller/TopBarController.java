package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.TaxeGame;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;
import gamelogic.player.PlayerManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TopBarController {
    public final static int CONTROLS_HEIGHT = 40;

    private Context context;
    private Color controlsColor = Color.LIGHT_GRAY;
    private TextButton endTurnButton;
    private TextButton createConnectionButton;
    private TextButton editConnectionButton;
    private Label flashMessage;

    public TopBarController(Context context) {
        this.context = context;

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                switch (state) {
                    case ANIMATING:
                        controlsColor = Color.GREEN;
                        break;
                    default:
                        controlsColor = Color.LIGHT_GRAY;
                        break;
                }
            }
        });

        createFlashActor();
    }

    private void createFlashActor() {
        flashMessage = new Label("", context.getSkin());
        flashMessage.setPosition(TaxeGame.WORLD_WIDTH / 2 - 50, TaxeGame.WORLD_HEIGHT - 24);
        context.getStage().addActor(flashMessage);
    }

    public void displayFlashMessage(String message, Color color) {
        displayFlashMessage(message, color, 1.75f);
    }

    public void displayFlashMessage(String message, Color color, float time) {
        final String saveText = String.valueOf(flashMessage.getText());
        flashMessage.clearActions();
        flashMessage.setText(message);
        flashMessage.setColor(color);
        flashMessage.setPosition(TaxeGame.WORLD_WIDTH / 2 - flashMessage.getTextBounds().width / 2, TaxeGame.WORLD_HEIGHT - 24);
        flashMessage.addAction(sequence(delay(time), fadeOut(0.25f), run(new Runnable() {
                    public void run() {
                        displayMessage(saveText, Color.BLACK);
                    }
                })));
    }

    public void displayMessage(String message, Color color) {
        flashMessage.clearActions();
        flashMessage.setText(message);
        flashMessage.setColor(color);
        flashMessage.setPosition(TaxeGame.WORLD_WIDTH / 2 - flashMessage.getTextBounds().width / 2, TaxeGame.WORLD_HEIGHT - 24);
    }

    public void drawBackground() {
        TaxeGame game = context.getTaxeGame();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(controlsColor);
        game.shapeRenderer.rect(0, TaxeGame.WORLD_HEIGHT - CONTROLS_HEIGHT, TaxeGame.WORLD_WIDTH, CONTROLS_HEIGHT);
        game.shapeRenderer.setColor(Color.BLACK);
        game.shapeRenderer.rect(0, TaxeGame.WORLD_HEIGHT - CONTROLS_HEIGHT, TaxeGame.WORLD_WIDTH, 1);
        game.shapeRenderer.end();
    }

    public void addEndTurnButton() {
        endTurnButton = new TextButton("End Turn", context.getSkin());
        endTurnButton.setPosition(TaxeGame.WORLD_WIDTH - 100.0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerManager.turnOver();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (state == GameState.NORMAL) {
                    endTurnButton.setVisible(true);
                } else {
                    endTurnButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(endTurnButton);
    }




    public void addCreateConnectionButton() {
        createConnectionButton = new TextButton("Create Connection", context.getSkin());
        createConnectionButton.setPosition(TaxeGame.WORLD_WIDTH - 250.0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        createConnectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getConnectionController().enterCreateConnectionMode();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (state == GameState.NORMAL) {
                    createConnectionButton.setVisible(true);
                } else {
                    createConnectionButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(createConnectionButton);
    }

    public void addEditConnectionButton() {
        editConnectionButton = new TextButton("Edit Connection", context.getSkin());
        editConnectionButton.setPosition(TaxeGame.WORLD_WIDTH - 380.0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        editConnectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getConnectionController().enterEditConnectionMode();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (state == GameState.NORMAL) {
                    editConnectionButton.setVisible(true);
                } else {
                    editConnectionButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(editConnectionButton);
    }

    public void clearFlashMessage() {
        displayFlashMessage("", Color.BLACK);
    }


}
