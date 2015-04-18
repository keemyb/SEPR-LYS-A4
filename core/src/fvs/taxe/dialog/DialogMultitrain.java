package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gamelogic.player.Player;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

public class DialogMultitrain extends Dialog {

    private Context context;
    private boolean isTrain = false;

    public DialogMultitrain(Station station, Skin skin, Context context) {
        super(station.getName(), skin);

        this.context = context;

        text("Choose which train you would like");
        System.out.println("Trains at station: " + station.getTrainsAtStation());

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getTrains()) {
                if (!(resource instanceof Train) || ((Train) resource).getPosition() == null) continue;
                Train train = (Train) resource;
                Position trainPosition = train.getPosition();
                System.out.println("train: " + trainPosition.getX() + ", " + trainPosition.getY());
                System.out.println("station: " + station.getLocation().getX() + ", " + station.getLocation().getY());
                if (trainPosition.equals(station.getLocation())) {
                    String destination = "";
                    if (train.getFinalStation() != null) {
                        destination = " to " + (train.getFinalStation().getName());
                    }
                    if (train.isOwnedBy(PlayerManager.getCurrentPlayer())){
                        button(train.getName() + destination, train);
                    }else{
                        button(train.getName() + destination + " (Opponent's)", train);
                    }
                    getButtonTable().row();
                    isTrain = true;
                }
                else {
                    System.out.println("not equal");
                }
            }
        }

        button("Cancel", "CANCEL");
        if (!isTrain) {
            hide();
        }
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
        if (obj == "CANCEL") {
            this.remove();
        } else {
            //Simulate click on train
            TrainClicked clicker = new TrainClicked(context, (Train) obj);
            clicker.clicked(null, 0, 0);
        }
    }

    public boolean getIsTrain() {
        return isTrain;
    }
}
