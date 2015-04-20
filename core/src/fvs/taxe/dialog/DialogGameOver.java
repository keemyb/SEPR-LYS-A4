package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.controller.Context;
import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;

public class DialogGameOver extends Dialog {
    private Context context;

        public DialogGameOver(Context context, Skin skin) {
            super("Game Over", skin);
            this.context = context;

            if (EventReplayer.isReplaying()) {
                context.getEventReplayer().pause();
                setTitle("Replay Finished");
            }

            int highScore = 0;
            Player highScorePlayer = null;
            for (Player player : PlayerManager.getAllPlayers()) {
                int playerMoney = player.getMoney();

                text("Player " + player.getPlayerNumber() + " finished with " + Game.CURRENCY_SYMBOL + playerMoney);
                getContentTable().row();

                if (playerMoney > highScore) {
                    highScore = playerMoney;
                    highScorePlayer = player;
                } else if (playerMoney == highScore) highScorePlayer = null;
            }

            if (highScorePlayer != null) {
                text("Player " + highScorePlayer.getPlayerNumber() + " Wins!");
            } else {
                text("It's a Tie!");
            }

            if (!EventReplayer.isReplaying()) {
                button("View Replay", "REPLAY");
            }
            button("Main Menu", "MAIN MENU");
            button("Exit", "EXIT");
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
        protected void result(Object object) {
            if (object == "REPLAY") {
                context.getEventReplayer().start();
            } else if (object == "MAIN MENU") {
                context.backToMainMenu();
            } else if (object == "EXIT"){
                Gdx.app.exit();
            }
        }
}
