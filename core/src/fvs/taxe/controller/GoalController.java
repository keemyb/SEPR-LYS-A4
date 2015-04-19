package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.dialog.DialogGoal;
import fvs.taxe.dialog.GoalClicked;
import gamelogic.game.GameEvent;
import gamelogic.goal.Goal;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

import java.util.ArrayList;
import java.util.List;

public class GoalController {
    private Context context;
    private Group goalButtons = new Group();

	private Tooltip originTip;
    private Tooltip destTip;
    
    public GoalController(Context context) {
        this.context = context;
        originTip = new Tooltip(context.getSkin());
        destTip = new Tooltip(context.getSkin());
        context.getStage().addActor(originTip);
        context.getStage().addActor(destTip);

		EventReplayer.subscribeReplayEvent(new ReplayListener() {
			@Override
			public void replay(GameEvent event, Object object) {
				if (event == GameEvent.CLICKED_GOAL_BUTTON) {
					Goal goal = (Goal) object;
					selectedGoal(goal);
				} else if (event == GameEvent.CLICKED_DISCARD_GOAL) {
					Goal goal = (Goal) object;
					discardGoal(goal);
				}
			}
		});
    }

    private List<Goal> playerGoals() {
		ArrayList<Goal> goals = new ArrayList<Goal>();
		Player currentPlayer = PlayerManager.getCurrentPlayer();

		for (Goal goal : currentPlayer.getGoals()) {
			if (goal.getComplete()) {
				continue;
			}

			goals.add(goal);
		}

		return goals;
	}

    public void showCurrentPlayerGoals() {

		goalButtons.remove();
		goalButtons.clear();

		float top = (float) TaxeGame.WORLD_HEIGHT;
		float x = 10.0f;
		float y = top - 10.0f - TopBarController.CONTROLS_HEIGHT;

		y -= 15;

		for (Goal goal : playerGoals()) {
			TextButton button = new TextButton(goal.toString(), context.getSkin());
			button.setPosition(x, y);

			GoalClicked listener = new GoalClicked(context, goal, this);
			button.addListener(listener);

			goalButtons.addActor(button);
			y -= 30;
		}

		context.getStage().addActor(goalButtons);
	}
    
    public void showCurrentPlayerHeader() {
		TaxeGame game = context.getTaxeGame();

		float top = (float) TaxeGame.WORLD_HEIGHT;
		float x = 10.0f;
		float y = top - 10.0f - TopBarController.CONTROLS_HEIGHT;

		game.batch.begin();
		game.fontSmall.setColor(Color.BLACK);
		game.fontSmall.draw(game.batch, playerGoalHeader(), x, y);
		game.batch.end();
	}

    private String playerGoalHeader() {
        return "Player " + PlayerManager.getCurrentPlayer().getPlayerNumber() + " Goals:";
    }

    public void discardGoal(Goal goal) {
        for (Player player : PlayerManager.getAllPlayers()) {
            if (player.getGoals().contains(goal)) {
				EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_DISCARD_GOAL, goal));
                player.discardGoal(goal);
            }
        }
    }

	public void selectedGoal(Goal goal) {
		EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_GOAL_BUTTON, goal));
		new DialogGoal(context, goal).show(context.getStage());
	}

	public Tooltip getOriginTip() {
		return originTip;
	}

	public Tooltip getDestTip() {
		return destTip;
	}
}
