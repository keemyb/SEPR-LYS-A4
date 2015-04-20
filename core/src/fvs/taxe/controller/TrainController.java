package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import fvs.taxe.StationClickListener;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.ResourceDialogButtonClicked;
import fvs.taxe.dialog.DialogResourceTrain;
import fvs.taxe.dialog.TrainClicked;
import gamelogic.game.Game;
import gamelogic.game.GameEvent;
import gamelogic.game.GameState;
import gamelogic.map.Junction;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerChangedListener;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;

public class TrainController {
    private Context context;
    private Train placedTrain;

    private StationClickListener stationClickListener = new StationClickListener() {
        @Override
        public void clicked(Station station) {
            selectStationToPlace(station);
        }
    };

    public TrainController(Context context) {
        this.context = context;

        gamelogic.player.PlayerManager.subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                for (Player player : PlayerManager.getAllPlayers()) {
                    for (Train train : player.getTrains()) {
                        setTrainLocation(train);

                    }
                }
            }
        });

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_TRAIN) {
                    Train train = (Train) object;
                    selected(train);
                } else if (event == GameEvent.CLICKED_CHOOSE_PLACE_TRAIN) {
                    Train train = (Train) object;
                    placeTrain(train);
                } else if (event == GameEvent.CLICKED_PLACE_TRAIN_CANCEL) {
                    cancelPlaceTrain();
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
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_TRAIN, train));

        if (Game.getInstance().getState() != GameState.NORMAL) return;

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
        }

        if (train.getPosition() == null){
            ResourceDialogButtonClicked listener = new ResourceDialogButtonClicked(context, currentPlayer, train);
            DialogResourceTrain dia = new DialogResourceTrain(train, context, train.getPosition() != null);
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        } else {
            context.getRouteController().beginRouting(train);
        }
    }

    public void placeTrain(Train train) {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_PLACE_TRAIN, train));

        placedTrain = train;

        Pixmap pixmap = new Pixmap(Gdx.files.internal(TrainManager.getCursorImageFileName(train)));
        Gdx.input.setCursorImage(pixmap, 8, 10);
        pixmap.dispose();

        Game.getInstance().setState(GameState.PLACING);
        setTrainsVisible(null, false);

        StationController.subscribeStationClick(stationClickListener);
    }

    private void selectStationToPlace(Station station) {
        if (station instanceof Junction) {
            context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);
            return;
        }

        placedTrain.setPosition(station.getLocation());
        placedTrain.addToHistory(station, PlayerManager.getTurnNumber());
        placedTrain.setAtStation(true);
        placedTrain.setLocation(station);

        finishPlaceTrain(true);
    }

    private void finishPlaceTrain(boolean placed) {
        Gdx.input.setCursorImage(null, 0, 0);

        if (placed) {
            TrainActor trainActor = renderTrain(placedTrain);
            placedTrain.setActor(trainActor);
        }
        setTrainsVisible(null, true);
        placedTrain = null;

        StationController.unsubscribeStationClick(stationClickListener);
        Game.getInstance().setState(GameState.NORMAL);
    }

    public void cancelPlaceTrain() {
        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_PLACE_TRAIN_CANCEL));
        finishPlaceTrain(false);
    }
}
