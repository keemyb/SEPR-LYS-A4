package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.ResourceDialogButtonClicked;
import fvs.taxe.dialog.DialogResourceTrain;
import fvs.taxe.dialog.TrainClicked;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
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
            for (Train playerTrain : player.getTrains()) {
                boolean trainAtStation = false;
                for (Station station : context.getGameLogic().getMap().getStations()) {
                    if (station.getLocation().equals((playerTrain).getPosition())) {
                        trainAtStation = true;
                        break;
                    }
                }
                if (playerTrain.getActor() != null && playerTrain != train && !trainAtStation) {
                    playerTrain.getActor().setVisible(visible);
                }
            }
        }
    }

    public void selected(Train train) {
        if (Game.getInstance().getState() != GameState.NORMAL) return;
        System.out.println("train clicked");

        // current player can't be passed in as it changes so find out current player at this instant
        Player currentPlayer = PlayerManager.getCurrentPlayer();

        if (!train.isOwnedBy(currentPlayer)) {
            context.getTopBarController().displayFlashMessage("Opponent's " + train.getName() + ". Speed: " + train.getSpeed(), Color.RED, 2);
            return;
        }

        if (train.getFinalStation() == null) {
            context.getTopBarController().displayFlashMessage("Your " + train.getName() + ". Speed: " + train.getSpeed(), Color.BLACK, 2);
        } else {
            context.getTopBarController().displayFlashMessage("Your " + train.getName() + ". Speed: " + train.getSpeed() + ". Destination: " + train.getFinalStation().getName(), Color.BLACK, 2);
        } if (train.getPosition() == null){
            ResourceDialogButtonClicked listener = new ResourceDialogButtonClicked(context, currentPlayer, train);
            DialogResourceTrain dia = new DialogResourceTrain(train, context.getSkin(), train.getPosition() != null);
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        } else {context.getRouteController().beginRouting(train);}
    }
}
