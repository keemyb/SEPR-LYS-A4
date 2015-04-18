package fvs.taxe.dialog;

import fvs.taxe.Button;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.player.Player;
import gamelogic.replay.ReplayEvent;
import gamelogic.resource.Train;

public class ResourceDialogButtonClicked implements ResourceDialogClickListener {
    private Context context;
    private Player currentPlayer;
    private Train train;

    public ResourceDialogButtonClicked(Context context, Player player, Train train) {
        this.currentPlayer = player;
        this.train = train;
        this.context = context;
    }

    @Override
    public void clicked(Button button) {
        switch (button) {
            case TRAIN_DISCARD:
                context.getEventReplayer().saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_PLACE_TRAIN_DISCARD));
                currentPlayer.removeTrain(train);
                break;
            case TRAIN_PLACE:
                context.getTrainController().placeTrain(train);
                break;
            case TRAIN_ROUTE:
                context.getRouteController().beginRouting(train);
                break;
        }
    }
}
