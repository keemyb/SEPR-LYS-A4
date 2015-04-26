package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.List;

public class DialogTrainClicked extends Dialog {

    private Context context;
    private Train train;

    public DialogTrainClicked(Train train, Context context) {
        super(train.getName() + " to " + train.getRoute().get(train.getRoute().size() - 1).getName(), context.getSkin());

        this.context = context;
        this.train = train;

        text("Would you like to re-route your Train?");

        button("Yes", GameEvent.ROUTING_BEGIN);
        button("No", GameEvent.CANCEL_REROUTE_TRAIN_DIALOG);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.ROUTING_BEGIN ||
                        event == GameEvent.CANCEL_REROUTE_TRAIN_DIALOG) {
                    result(GameEvent.CANCEL_REROUTE_TRAIN_DIALOG);
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
        if (object == GameEvent.CANCEL_REROUTE_TRAIN_DIALOG) {
            EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CANCEL_REROUTE_TRAIN_DIALOG));
            this.remove();
        } else {
            context.getRouteController().beginRouting(train);
        }
    }
}
