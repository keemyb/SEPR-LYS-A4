package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;

public class DialogEndGame extends Dialog {

    private Context context;

    public DialogEndGame(Context context) {
        super("EndGame",context.getSkin());

        this.context = context;

        text("Are you sure you want to end game? \n All progress will be lost.");

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
            context.backToMainMenu();
        } else {
            this.remove();
        }
    }
}
