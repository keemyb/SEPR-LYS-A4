package fvs.taxe.controller;

import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.TrainClicked;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

public class TrainController {
    private Context context;

    public TrainController(Context context) {
        this.context = context;
        gamelogic.player.PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                System.out.println("----------------------");
                for (Player player : PlayerManager.getAllPlayers()){
                    for (Train train : player.getTrains()){
                        setTrainLocation(train);

                    }
                }
            }
        });
    }

    public TrainActor renderTrain(Train train) {
        TrainActor trainActor = new TrainActor(train);
        trainActor.addListener(new TrainClicked(context, train));
        trainActor.setVisible(false);
        context.getStage().addActor(trainActor);
        return trainActor;
    }

    public void setTrainLocation (Train train) {
        System.out.println(train.getName() + " Is At Station: " + train.isAtStation());
        if (!train.isAtStation()) {
            train.setLocation(null);
        } else {
            for (Station station : context.getGameLogic().getMap().getStations()) {
                if (train.getPosition() == station.getLocation()) {
                    train.setLocation(station);
                    break;
                }
            }
        }
    }

    // Sets all trains on the map visible or invisible except one that we are routing for
    public void setTrainsVisible(Train train, boolean visible) {

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getTrains()) {
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
