package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import fvs.taxe.Button;
import fvs.taxe.StationClickListener;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TrainController;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.player.Player;
import gamelogic.map.Junction;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;

public class DialogButtonClicked implements ResourceDialogClickListener {
    private Context context;
    private Player currentPlayer;
    private Train train;

    public DialogButtonClicked(Context context, Player player, Train train) {
        this.currentPlayer = player;
        this.train = train;
        this.context = context;
    }

    @Override
    public void clicked(Button button) {
        switch (button) {
            case TRAIN_DISCARD:
                currentPlayer.removeResource(train);
                break;
            case TRAIN_PLACE:
                Pixmap pixmap = new Pixmap(Gdx.files.internal(TrainManager.getCursorImageFileName(train)));
                Gdx.input.setCursorImage(pixmap, 8, 10);
                pixmap.dispose();

                Game.getInstance().setState(GameState.PLACING);
                TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);

                StationController.subscribeStationClick(new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        if (station instanceof Junction) {
                            context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);
                            return;
                        }

                        train.setPosition(station.getLocation());
                        train.addToHistory(station, PlayerManager.getTurnNumber());

                        Gdx.input.setCursorImage(null, 0, 0);

                        TrainController trainController = new TrainController(context);
                        TrainActor trainActor = trainController.renderTrain(train);
                        trainController.setTrainsVisible(null, true);
                        train.setActor(trainActor);

                        StationController.unsubscribeStationClick(this);
                        Game.getInstance().setState(GameState.NORMAL);
                    }
                });

                break;
            case TRAIN_ROUTE:
                context.getRouteController().begin(train);

                break;
        }
    }
}
