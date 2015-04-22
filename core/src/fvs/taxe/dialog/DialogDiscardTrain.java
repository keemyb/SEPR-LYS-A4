package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

public class DialogDiscardTrain extends Dialog {

    private Context context;
    private Train train;

    public DialogDiscardTrain(Context context, Train train) {
        super(train.toString(), context.getSkin());

        this.context = context;
        this.train = train;

        text("Would you like to discard this train?");

        button("Yes", GameEvent.SELECTED_TRAIN_DISCARD_TRAIN);
        button("No", GameEvent.SELECTED_TRAIN_CANCEL);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.SELECTED_TRAIN_DISCARD_TRAIN ||
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
    protected void result(Object obj) {
        if (obj == GameEvent.SELECTED_TRAIN_DISCARD_TRAIN) {
            context.getTrainController().discardTrain(train);
        } else {
            context.getEventReplayer();
			EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_TRAIN_CANCEL));
            this.remove();
        }
    }
}
