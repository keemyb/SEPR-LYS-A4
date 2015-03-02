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

public class DialogUpgradeConnection extends Dialog {

    private Context context;
    private Connection connection;

    public DialogUpgradeConnection(Connection connection, Skin skin, Context context) {
        super(connection.toString(), skin);

        this.context = context;
        this.connection = connection;

        text("What material would you like to your connection to?");

        for (Connection.Material material : Connection.Material.values()) {
            if (connection.isUpgradable(material)) {
                int upgradeCost = connection.calculateUpgradeCost(material);
                String materialName = material.getName();
                String buttonString = materialName + ": " + Game.CURRENCY_SYMBOL + upgradeCost;
                button(buttonString, material);
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
        if (obj instanceof Connection.Material) {
            Player currentPlayer = PlayerManager.getCurrentPlayer();
            Connection.Material material = (Connection.Material) obj;
            connection.upgrade(material);
            int upgradeCost = connection.calculateCost();
            if (!Game.CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS &&
                    currentPlayer.getMoney() <= upgradeCost) {
                context.getTopBarController().displayFlashMessage(
                        "Not enough money to upgrade connection", Color.BLACK, 1000);
            } else {
                context.getGameLogic().getMap().addConnection(connection);
                currentPlayer.spendMoney(connection.calculateCost());
                System.out.println("Upgraded a connection to " + material);
            }
        } else {
            this.remove();
        }
    }
}
