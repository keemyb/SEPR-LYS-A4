package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import gamelogic.resource.Train;

public class TrainClicked extends ClickListener {
    private Context context;
    private Train train;

    public TrainClicked(Context context, Train train) {
        this.train = train;
        this.context = context;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        context.getTrainController().selected(train);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        System.out.println("in");
        context.getRouteController().setHoveredTrain(train);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        System.out.println("out");
        context.getRouteController().setHoveredTrain(null);
    }
}
