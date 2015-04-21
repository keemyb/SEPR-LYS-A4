package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.goal.Goal;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

public class DialogGoal extends Dialog {

    private Context context;
    private Goal goal;

    public DialogGoal(Context context, Goal goal) {
        super(goal.toString(), context.getSkin());

        this.context = context;
        this.goal = goal;

        text("Would you like to discard this goal?");

        button("Yes", "YES");
        button("No", "NO");

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.SELECTED_GOAL_DISCARD ||
                        event == GameEvent.SELECTED_GOAL_CANCEL) {
                    result("NO");
                }
            }
        });
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
        if (obj == "YES") {
            context.getGoalController().discardGoal(goal);
        } else {
            context.getEventReplayer().saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_GOAL_CANCEL));
            this.remove();
        }
    }
}
