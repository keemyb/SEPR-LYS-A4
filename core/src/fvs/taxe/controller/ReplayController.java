package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.*;
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

import javax.swing.*;
import java.util.List;

public class ReplayController {
    private Context context;

    private TrainController trainController;

    private RecordStateManager recordStateManager;
    private List<RecordState> recordStates;
    private RecordState currentRecordState;
    private RecordState nextRecordState;

    private int replaySpeedFactor = 1;

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

        advanceReplay();
    }

    public void advanceReplay() {
        if (nextRecordState == null) return;

        RecordState previousRecordState = currentRecordState;
        currentRecordState = nextRecordState;

        int nextRecordIndex = recordStates.indexOf(currentRecordState) + 1;
        if (nextRecordIndex >= recordStates.size()) return;
        nextRecordState = recordStates.get(nextRecordIndex);

        currentRecordState.restorePlayerAttributes();
        currentRecordState.restoreConnections();
        PlayerManager.setTurnNumber(currentRecordState.getTurn());

        if (previousRecordState == null
                || currentRecordState.getTurn() > previousRecordState.getTurn()) {
            showNewTurnRecordState();
        } else {
            showSameTurnRecordState();
        }
    }

    private void showNewTurnRecordState() {
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

    private void showSameTurnRecordState() {
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
        float duration = (float) (delta * replaySpeedFactor) / 1000;

        SequenceAction action = Actions.sequence();

        action.addAction(moveTo(position.getX(), position.getY(), duration));

        action.addAction(new RunnableAction() {
            public void run() {
                context.getGameLogic().setState(GameState.REPLAY_STATIC);
            }
        });

        TrainActor trainActor = train.getActor();

        trainActor.addAction(action);

        trainActor.setVisible(true);
    }
}
