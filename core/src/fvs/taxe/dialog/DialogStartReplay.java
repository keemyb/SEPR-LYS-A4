package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.GameScreen;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.TaxeGame;
import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;

public class DialogStartReplay extends Dialog {
    private TaxeGame game;
    private Game gameLogic;

        public DialogStartReplay(TaxeGame game, Game gameLogic, Skin skin) {
            super("GAME OVER, do you want to view the replay?", skin);
            this.game = game;
            this.gameLogic = gameLogic;

            int highscore = 0;
            int playernum = 0;
            for (Player player : PlayerManager.getAllPlayers()) {
                int playerMoney = player.getMoney();

                text("Player " + player.getPlayerNumber() + " has " + Game.CURRENCY_SYMBOL + playerMoney);
                getContentTable().row();

                if (playerMoney > highscore) {
                    highscore = playerMoney;
                    playernum = player.getPlayerNumber();
                } else if (playerMoney == highscore) playernum = 0;
            }
            if (playernum != 0) {
                text("PLAYER " + playernum + " WINS!");
            } else {
                text("NO WINNER");
            }

            button("Yes", "REPLAY");
            button("No", "EXIT");
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
            if (obj == "REPLAY") {
                gameLogic.startReplay();
            } else {
                Gdx.app.exit();
            }
        }
}
