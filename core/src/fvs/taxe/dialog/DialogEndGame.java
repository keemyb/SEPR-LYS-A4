package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.TaxeGame;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;

public class DialogEndGame extends Dialog {
    private TaxeGame game;

    public DialogEndGame(TaxeGame game, Skin skin) {
        super("GAME OVER", skin);
        this.game = game;

        int highscore = 0;
        int playernum = 0;
        for (Player player : PlayerManager.getAllPlayers()) {
            int playerScore = player.getScore();

            text("Player " + player.getPlayerNumber() + " scored " + playerScore + " points");
            getContentTable().row();

            if (playerScore > highscore) {
                highscore = playerScore;
                playernum = player.getPlayerNumber();
            } else if (playerScore == highscore) playernum = 0;
        }
        if (playernum != 0) {
            text("PLAYER " + playernum + " WINS!");
        } else {
            text("NO WINNER");
        }

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
    protected void result(Object obj) {
        if (obj == "EXIT") {
            Gdx.app.exit();
        } else {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
