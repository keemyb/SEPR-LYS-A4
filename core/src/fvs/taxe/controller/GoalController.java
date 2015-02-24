package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import gamelogic.goal.Goal;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;

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
			y -= 30;

			TextButton button = new TextButton(goal.toString(),
					context.getSkin());
			button.setPosition(x, y);

			final Station origin = goal.getOrigin();
			final Station dest = goal.getDestination();

			button.addListener(new ClickListener() {

				@Override
				public void enter(InputEvent event, float x, float y,
						int pointer, Actor fromActor) {
					originTip.setPosition(origin.getLocation().getX()-(originTip.getWidth()/2), origin
							.getLocation().getY()+12);
					originTip.show(origin.getName());
					
					destTip.setPosition(dest.getLocation().getX()-(destTip.getWidth()/2), dest.getLocation()
							.getY()+12);
					destTip.show(dest.getName());
					
					
				}

				@Override
				public void exit(InputEvent event, float x, float y,
						int pointer, Actor toActor) { //return to normal
					originTip.hide();
					destTip.hide();
				}
			});
			goalButtons.addActor(button);
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
}
