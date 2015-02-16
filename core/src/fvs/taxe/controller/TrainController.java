package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.TrainClicked;
import gamelogic.player.Player;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;
import gamelogic.resource.TrainJelliedListener;

public class TrainController {
    private Context context;

    public TrainController(Context context) {
        this.context = context;
    }

    public TrainActor renderTrain(Train train) {
        TrainActor trainActor = new TrainActor(train);
        trainActor.addListener(new TrainClicked(context, train));
        trainActor.setVisible(false);
        context.getStage().addActor(trainActor);
        return trainActor;
    }


    // Sets all trains on the map visible or invisible except one that we are routing for
    public void setTrainsVisible(Train train, boolean visible) {

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getResources()) {
                if (resource instanceof Train) {
                    boolean trainAtStation = false;
                    for (Station station : context.getGameLogic().getMap().getStations()) {
                        if (station.getLocation().equals(((Train) resource).getPosition())) {
                            trainAtStation = true;
                            break;
                        }
                    }
                    if (((Train) resource).getActor() != null && resource != train && !trainAtStation) {
                        ((Train) resource).getActor().setVisible(visible);
                    }
                }
            }
        }
    }
}
