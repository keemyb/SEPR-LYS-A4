package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceTrain extends Dialog {
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceTrain(Train train, Skin skin, boolean trainPlaced) {
        super(train.toString(), skin);

        text("What do you want to do with this train?");

        if (!trainPlaced) {
            button("Place at a station", "PLACE");
        } else {
            button("Choose a route", "ROUTE");
        }

        button("Discard", "DISCARD");
        button("Cancel", "CLOSE");



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

    private void clicked(Button button) {
        for (ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        if (obj == "CLOSE") {
            this.remove();
        } else if (obj == "DISCARD") {
            clicked(Button.TRAIN_DISCARD);
        } else if (obj == "PLACE") {
            clicked(Button.TRAIN_PLACE);
        } else if (obj == "ROUTE") {
            clicked(Button.TRAIN_ROUTE);
        }
    }
}
