package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import gamelogic.resource.Train;

public class TrainButtonClicked extends ClickListener {
    private Context context;
    private Train train;

    public TrainButtonClicked(Context context, Train train) {
        this.train = train;
        this.context = context;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        context.getTrainController().selected(train);
    }
}
