package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actor.TrainActor;
import gamelogic.game.GameState;
import gamelogic.map.Position;
import gamelogic.player.PlayerManager;
import gamelogic.recording.RecordState;
import gamelogic.recording.RecordStateManager;
import gamelogic.resource.Train;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

import java.util.List;

public class ReplayController {
    public static final float MIN_PLAYBACK_SPEED = 1f;
    public static final float MAX_PLAYBACK_SPEED = 2f;
    public static final float DEFAULT_PLAYBACK_SPEED = 1f;

    private Context context;

    private TrainController trainController;

    private RecordStateManager recordStateManager;
    private List<RecordState> recordStates;
    private RecordState currentRecordState;
    private RecordState nextRecordState;

    private float playBackSpeed = DEFAULT_PLAYBACK_SPEED;

    public ReplayController(Context context) {
        this.context = context;
        recordStateManager = new RecordStateManager(context.getGameLogic());
        trainController = new TrainController(context);
    }

    public RecordStateManager getRecordStateManager() {
        return recordStateManager;
    }

    public void startReplay() {
        recordStates = recordStateManager.getRecordStates();
        nextRecordState = recordStates.get(0);

        skipReplay();
    }

    private boolean anyMoreRecordStates() {
        return nextRecordState != null;
    }

    private boolean nextStateIsKeyFrame() {
        return nextRecordState.getTurn() != currentRecordState.getTurn();
    }

    private void loadNextState() {
        currentRecordState = nextRecordState;

        int nextRecordIndex = recordStates.indexOf(currentRecordState) + 1;
        if (nextRecordIndex < recordStates.size()) {
            nextRecordState = recordStates.get(nextRecordIndex);
        } else {
            nextRecordState = null;
        }
    }

    private void loadNextKeyframeState() {
        if (currentRecordState == null) {
            loadNextState();
        } else {
            while (anyMoreRecordStates()) {
                RecordState previousRecordState = currentRecordState;
                loadNextState();
                if (previousRecordState.getTurn() != currentRecordState.getTurn()) break;
            }
        }

    }

    public void playReplay() {
        if (anyMoreRecordStates()) {
            if (nextStateIsKeyFrame()) {
                skipReplay();
                playReplay();
            } else {
                loadNextState();
                showCurrentStateViaInterpolation();
            }
        }
    }

    public void skipReplay() {
        if (anyMoreRecordStates()) {
            loadNextKeyframeState();
            showCurrentStateAsKeyframe();

            currentRecordState.restorePlayerAttributes();
            currentRecordState.restoreConnections();
            PlayerManager.setTurnNumber(currentRecordState.getTurn());
        }
    }

    private void showCurrentStateAsKeyframe() {
        for (java.util.Map.Entry entry : currentRecordState.getTrainPositions().entrySet()) {
            Train train = (Train) entry.getKey();
            Position position = (Position) entry.getValue();

            TrainActor oldActor = train.getActor();
            if (oldActor != null) {
                oldActor.setVisible(false);
            }

            if (position == null) continue;

            train.setPosition(position);

            trainController.renderTrain(train);

            train.getActor().setPosition(position.getX(), position.getY());
            train.getActor().setVisible(true);
        }

        context.getGameLogic().setState(GameState.REPLAY_STATIC);
    }

    private void showCurrentStateViaInterpolation() {
        long delta = currentRecordState.getDelta();

        context.getGameLogic().setState(GameState.REPLAY_ANIMATING);
        for (java.util.Map.Entry entry : currentRecordState.getTrainPositions().entrySet()) {
            Train train = (Train) entry.getKey();
            Position position = (Position) entry.getValue();

            if (position == null) continue;

            addMoveAction(train, position, delta);
        }
    }

    private void addMoveAction(Train train, Position position, long delta) {
        float duration = (delta / playBackSpeed) / 1000;

        SequenceAction action = Actions.sequence();

        action.addAction(moveTo(position.getX(), position.getY(), duration));

        action.addAction(new RunnableAction() {
            public void run() {
                if (context.getGameLogic().getState() != GameState.REPLAY_STATIC) {
                    context.getGameLogic().setState(GameState.REPLAY_STATIC);
                    if (anyMoreRecordStates() && !nextStateIsKeyFrame()) {
                        loadNextState();
                        showCurrentStateViaInterpolation();
                    }
                }
            }
        });

        TrainActor trainActor = train.getActor();

        trainActor.addAction(action);

        trainActor.setVisible(true);
    }

    public void setPlayBackSpeed(float playBackSpeed) {
        this.playBackSpeed = playBackSpeed;
    }
}
