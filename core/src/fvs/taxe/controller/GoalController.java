package fvs.taxe.controller;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import fvs.taxe.GameScreen;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.dialog.DialogDiscardGoal;
import fvs.taxe.dialog.DiscardClicked;
import fvs.taxe.dialog.GoalClicked;
import gamelogic.game.GameEvent;
import gamelogic.goal.Goal;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

public class GoalController {
	private static final float MY_GOALS_Y = (float) TaxeGame.WORLD_HEIGHT
			- TopBarController.CONTROLS_HEIGHT - 10f;
	private static final float SPACE_BETWEEN_GOALS_X = GameScreen.BUTTON_PADDING_X;
	private static final float SPACE_BETWEEN_GOALS_Y = 30f;
	private static final float SPACE_AFTER_HEADER_Y = 50f;

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
				if (event == GameEvent.SELECTED_GOAL) {
					Goal goal = (Goal) object;
					selectedGoal(goal);
				} else if (event == GameEvent.SELECTED_GOAL_DISCARD) {
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

	public void refresh() {
		drawHeaderText();
		drawPlayerGoals();
	}

	public void drawHeaderText() {
		TaxeGame game = context.getTaxeGame();

		game.batch.begin();
		game.fontSmall.setColor(Color.BLACK);

		game.fontSmall.draw(game.batch, "My Goals: ", SPACE_BETWEEN_GOALS_X,
				MY_GOALS_Y);

		game.batch.end();
	}

	public void drawPlayerGoals() {
		float x = SPACE_BETWEEN_GOALS_X;
		float y = MY_GOALS_Y - SPACE_AFTER_HEADER_Y;

		goalButtons.remove();
		goalButtons.clear();

		for (Goal goal : playerGoals()) {

			DiscardClicked discardListener = new DiscardClicked(context, goal);

			TextButton discardButton = new TextButton(" X ", context.getSkin());
			discardButton.setPosition(x, y);
			discardButton.setColor(Color.RED);
			discardButton.addListener(discardListener);

			TextButton button = new TextButton(goal.toString(),
					context.getSkin());
			button.setPosition(x + discardButton.getWidth() + 5, y);

			GoalClicked listener = new GoalClicked(context, goal, this);
			button.addListener(listener);

			goalButtons.addActor(button);
			goalButtons.addActor(discardButton);
			y -= SPACE_BETWEEN_GOALS_Y;
		}

		context.getStage().addActor(goalButtons);
	}

	public void discardGoal(Goal goal) {
		for (Player player : PlayerManager.getAllPlayers()) {
			if (player.getGoals().contains(goal)) {
				EventReplayer.saveReplayEvent(new ReplayEvent(
						GameEvent.SELECTED_GOAL_DISCARD, goal));
				player.discardGoal(goal);
			}
		}
	}

	public void selectedGoal(Goal goal) {
		EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_GOAL,
				goal));
		new DialogDiscardGoal(context, goal).show(context.getStage());
	}

	public Tooltip getOriginTip() {
		return originTip;
	}

	public Tooltip getDestTip() {
		return destTip;
	}
}
