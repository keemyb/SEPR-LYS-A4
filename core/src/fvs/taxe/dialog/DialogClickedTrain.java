package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.TrainController;
import gamelogic.game.GameEvent;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

public class DialogClickedTrain extends Dialog {
    private TrainController trainController;

    private Train train;

    public DialogClickedTrain(Train train, Context context) {
        super(train.toString(), context.getSkin());
        trainController = context.getTrainController();

        this.train = train;

        text("What do you want to do with this train?");

        button("Place at a station", GameEvent.SELECTED_TRAIN_PLACE_TRAIN);

        button("Discard", GameEvent.SELECTED_TRAIN_DISCARD_TRAIN);
        button("Cancel", GameEvent.SELECTED_TRAIN_CANCEL);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.SELECTED_TRAIN_PLACE_TRAIN ||
                        event == GameEvent.SELECTED_TRAIN_DISCARD_TRAIN ||
                        event == GameEvent.SELECTED_TRAIN_CANCEL) {
                    result(GameEvent.SELECTED_TRAIN_CANCEL);
                }
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }

    @Override
    public void hide() {
        hide(null);
    }

    @Override
    protected void result(Object object) {
        if (object == GameEvent.SELECTED_TRAIN_CANCEL) {
            EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_TRAIN_CANCEL));
            this.remove();
        } else if (object == GameEvent.SELECTED_TRAIN_PLACE_TRAIN){
            trainController.placeTrain(train);
        } else if (object == GameEvent.SELECTED_TRAIN_DISCARD_TRAIN){
            trainController.discardTrain(train);
        }
    }
}
