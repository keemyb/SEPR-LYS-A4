package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.Tooltip;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.GoalController;
import gamelogic.goal.Goal;
import gamelogic.map.Station;

public class GoalClicked extends ClickListener {
    private Context context;
    private Goal goal;

    private Tooltip originTooltip;
    private Tooltip destTooltip;

    private Station origin;
    private Station destination;

    public GoalClicked(Context context, Goal goal, GoalController goalController) {
        this.goal = goal;
        this.context = context;

        originTooltip = goalController.getOriginTip();
        destTooltip = goalController.getDestTip();

        origin = goal.getOrigin();
        destination = goal.getDestination();
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        context.getGoalController().selectedGoal(goal);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        originTooltip.setPosition(origin.getLocation().getX() - (originTooltip.getWidth() / 2),
                origin.getLocation().getY() + 12);
        originTooltip.show(origin.getName());

        destTooltip.setPosition(destination.getLocation().getX() - (destTooltip.getWidth() / 2),
                destination.getLocation().getY() + 12);
        destTooltip.show(destination.getName());
    }

    @Override
    public void exit(InputEvent event, float x, float y,
                     int pointer, Actor toActor) { //return to normal
        originTooltip.hide();
        destTooltip.hide();
    }
}
