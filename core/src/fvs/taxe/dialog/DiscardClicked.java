package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.controller.Context;
import gamelogic.goal.Goal;
import gamelogic.resource.Train;

public class DiscardClicked extends ClickListener {
	private Context context;
    private Train train;
    private Goal goal;

    public DiscardClicked(Context context, Train train) {
        this.train = train;
        this.context = context;
        this.goal = null;
    }
    
    public DiscardClicked(Context context, Goal goal) {
    	this.train = null;
        this.context = context;
        this.goal = goal;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	if(train != null) {
    	DialogDiscardTrain dialogDiscardTrain = new DialogDiscardTrain(context, train);
        dialogDiscardTrain.show(context.getStage());
    	} else if (goal != null) {
    		DialogDiscardGoal dialogDiscardGoal = new DialogDiscardGoal(context, goal);
    		dialogDiscardGoal.show(context.getStage());
    	}
    }
}

