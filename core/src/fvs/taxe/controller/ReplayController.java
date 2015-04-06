package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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

        System.out.println("Start Replay");

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
        // Keyframe State stub
        System.out.println("Replay Keyframe");

        for (java.util.Map.Entry entry : currentRecordState.getTrainPositions().entrySet()) {
            Train train = (Train) entry.getKey();
            Position position = (Position) entry.getValue();

            if (position == null) continue;
            System.out.println("Set Position");

            train.setPosition(position);

            trainController.renderTrain(train);

            trainController.setTrainsVisible(train, true);
        }

        context.getGameLogic().setState(GameState.REPLAY_STATIC);
    }

    private void showSameTurnRecordState() {
        // Interpolation state stub
        System.out.println("Replay Interpolation");

        long delta = currentRecordState.getDelta();

        for (java.util.Map.Entry entry : currentRecordState.getTrainPositions().entrySet()) {
            Train train = (Train) entry.getKey();
            Position position = (Position) entry.getValue();

            if (position == null) continue;

            addMoveAction(train, position, delta);

            trainController.renderTrain(train);

            trainController.setTrainsVisible(train, true);
        }

        context.getGameLogic().setState(GameState.REPLAY_ANIMATING);
    }

    private void addMoveAction(Train train, Position position, long delta) {
        float duration = (float) (delta * replaySpeedFactor);

        SequenceAction action = Actions.sequence();

        action.addAction(moveTo(position.getX(), position.getY(), duration));

        train.getActor().addAction(action);
    }
}
