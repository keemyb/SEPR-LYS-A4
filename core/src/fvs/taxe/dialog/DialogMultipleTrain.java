package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.player.Player;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;

import java.util.List;

public class DialogMultipleTrain extends Dialog {

    private Context context;

    public DialogMultipleTrain(Station station, Context context) {
        super(station.getName(), context.getSkin());

        this.context = context;

        text("Please select a train");

        Player currentPlayer = PlayerManager.getCurrentPlayer();

        List<Train> trainsAtStation = station.getTrainsAtStation();

        for (Train train : trainsAtStation) {
            String destination = "";

            if (train.getFinalStation() != null) {
                destination = " to " + (train.getFinalStation().getName());
            }

            if (train.isOwnedBy(currentPlayer)){
                button(train.getName() + destination, train);
            } else {
                button(train.getName() + destination + " (Opponent's)", train);
            }

            getButtonTable().row();
        }

        button("Cancel", GameEvent.CLICKED_CANCEL_MULTIPLE_TRAIN);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_TRAIN ||
                        event == GameEvent.CLICKED_CANCEL_MULTIPLE_TRAIN) {
                    result(GameEvent.CLICKED_CANCEL_MULTIPLE_TRAIN);
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
        if (object == GameEvent.CLICKED_CANCEL_MULTIPLE_TRAIN) {
            EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CANCEL_MULTIPLE_TRAIN));
            this.remove();
        } else {
            Train train = (Train) object;
            context.getTrainController().selected(train);
        }
    }
}
