package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.GameScreen;
import fvs.taxe.TaxeGame;
import gamelogic.game.Game;

public class DialogStartGame extends Dialog {
    private TaxeGame game;

    public DialogStartGame(TaxeGame game, Skin skin) {
        super("Select Game Length:", skin);
        this.game = game;

        button("15 Turns", 15);
        button("30 Turns", 30);
        button("45 Turns", 45);
        button("60 Turns", 60);
    }

    @Override
    public Dialog show(Stage stage) {
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) * 0.67));
        return this;
    }

    @Override
    public void hide() {
        hide(null);
    }

    @Override
    protected void result(Object obj) {
        Game.getInstance().setTotalTurns((int) obj);
        game.setScreen(new GameScreen(game));
    }
}
