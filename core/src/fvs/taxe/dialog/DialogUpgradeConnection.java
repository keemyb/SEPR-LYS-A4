package fvs.taxe.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gamelogic.game.Game;
import gamelogic.game.GameEvent;
import gamelogic.map.Connection;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

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

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL ||
                        event == GameEvent.CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL_CANCEL) {
                    result("CANCEL");
                }
            }
        });
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
        if (object == "CANCEL") {
            EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL_CANCEL));
            this.remove();
        } else {
            context.getConnectionController().upgradeConnection(connection, (Connection.Material) object);
        }
    }
}
