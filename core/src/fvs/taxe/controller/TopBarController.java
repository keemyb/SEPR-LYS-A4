package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.TaxeGame;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.replay.ReplayModeListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TopBarController {
    public final static int CONTROLS_HEIGHT = 40;
    public final static int SPEED_SLIDER_SEGMENTS = 8;

    private Context context;
    private Color controlsColor = Color.LIGHT_GRAY;
    private TextButton endTurnButton;
    private TextButton playReplayButton;
    private TextButton pauseReplayButton;
    private Slider replaySpeedSlider;
    private TextButton createConnectionButton;
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

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_END_TURN) {
                    PlayerManager.turnOver();
                }
            }
        });

        EventReplayer.subscribeReplayModeEvent(new ReplayModeListener() {
            @Override
            public void changed(boolean isReplaying) {
                refreshButtons(isReplaying);
            }
        });

        addPauseReplayButton();
        addPlayReplayButton();
        addReplaySlider();
        addEndTurnButton();
        addCreateConnectionButton();
        refreshButtons(EventReplayer.isReplaying());

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

    public void refreshButtons(boolean isReplaying) {
        pauseReplayButton.setVisible(isReplaying);
        playReplayButton.setVisible(isReplaying);
        replaySpeedSlider.setVisible(isReplaying);
        createConnectionButton.setVisible(!isReplaying);
        endTurnButton.setVisible(!isReplaying);
    }

    private void addEndTurnButton() {
        endTurnButton = new TextButton("End Turn", context.getSkin());
        endTurnButton.setPosition(TaxeGame.WORLD_WIDTH - 100.0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_END_TURN));
                PlayerManager.turnOver();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (EventReplayer.isReplaying()) return;

                if (state == GameState.NORMAL) {
                    endTurnButton.setVisible(true);
                } else {
                    endTurnButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(endTurnButton);
    }

    private void addPauseReplayButton() {
        pauseReplayButton = new TextButton("Pause", context.getSkin());
        pauseReplayButton.setPosition(50f, TaxeGame.WORLD_HEIGHT - 33.0f);
        pauseReplayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getEventReplayer().pause();
            }
        });

        pauseReplayButton.setVisible(EventReplayer.isReplaying());

        context.getStage().addActor(pauseReplayButton);
    }

    private void addPlayReplayButton() {
        playReplayButton = new TextButton("Play", context.getSkin());
        playReplayButton.setPosition(0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        playReplayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getEventReplayer().play();
            }
        });

        playReplayButton.setVisible(EventReplayer.isReplaying());

        context.getStage().addActor(playReplayButton);
    }

    private void addReplaySlider() {
        replaySpeedSlider = new Slider(EventReplayer.MIN_PLAYBACK_SPEED,
                EventReplayer.MAX_PLAYBACK_SPEED,
                (EventReplayer.MAX_PLAYBACK_SPEED - EventReplayer.MIN_PLAYBACK_SPEED) / SPEED_SLIDER_SEGMENTS,
                false, context.getSkin());
        replaySpeedSlider.setPosition(110, TaxeGame.WORLD_HEIGHT - 33.0f);

        replaySpeedSlider.setVisible(EventReplayer.isReplaying());

        replaySpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EventReplayer.setPlayBackSpeed(replaySpeedSlider.getValue());
            }
        });

        context.getStage().addActor(replaySpeedSlider);
    }

    private void addCreateConnectionButton() {
        createConnectionButton = new TextButton("Add Track", context.getSkin());
        createConnectionButton.setPosition(TaxeGame.WORLD_WIDTH - 200.0f, TaxeGame.WORLD_HEIGHT - 33.0f);
        createConnectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getConnectionController().enterCreateConnectionMode();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (EventReplayer.isReplaying()) return;

                if (state == GameState.NORMAL) {
                    createConnectionButton.setVisible(true);
                } else {
                    createConnectionButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(createConnectionButton);
    }

    public void clearFlashMessage() {
        displayFlashMessage("", Color.BLACK);
    }
}
