package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gamelogic.goal.Goal;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

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
            context.getGoalController().removeGoal(goal);
        }
    }
}
