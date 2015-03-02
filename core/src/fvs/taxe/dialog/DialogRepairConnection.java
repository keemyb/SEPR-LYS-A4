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
        if (obj instanceof Float) {
            Player currentPlayer = PlayerManager.getCurrentPlayer();
            float repairThreshold = (float) obj;
            int repairCost = connection.calculateRepairCost(repairThreshold);
            if (!Game.CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS &&
                    currentPlayer.getMoney() <= repairCost) {
                context.getTopBarController().displayFlashMessage(
                        "Not enough money to repair connection", Color.BLACK, 1000);
            } else {
                connection.repair(repairThreshold);
                currentPlayer.spendMoney(repairCost);
                System.out.println("Repaired a connection to " + String.valueOf(repairThreshold * 100) + "%");
            }
        } else {
            this.remove();
        }
    }
}
