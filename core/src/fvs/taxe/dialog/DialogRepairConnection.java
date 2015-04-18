package fvs.taxe.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gamelogic.game.Game;
import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;

public class DialogRepairConnection extends Dialog {

    private Context context;
    private Connection connection;

    public DialogRepairConnection(Connection connection, Skin skin, Context context) {
        super(connection.toString(), skin);

        this.context = context;
        this.connection = connection;

        float currentHealth = connection.getHealth();

        text("What level would you like to restore your connection to? \n" +
                "Current health: " + String.valueOf((int) (currentHealth * 100)) + '%');

        for (float repairThreshold : Connection.repairThresholds) {
            if (repairThreshold > currentHealth) {
                int repairCost = connection.calculateRepairCost(repairThreshold);
                String thresholdString = String.valueOf((int) (repairThreshold * 100)) + '%';
                String buttonString = thresholdString + ": " + Game.CURRENCY_SYMBOL + repairCost;
                button(buttonString, repairThreshold);
            }
        }

        button("Cancel", "CANCEL");
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
        if (!(obj instanceof Float)) return;

        context.getConnectionController().repairConnection(connection, (float) obj);
    }
}
