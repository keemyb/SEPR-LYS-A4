package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gamelogic.game.Game;
import gamelogic.game.GameEvent;
import gamelogic.map.Connection;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

public class DialogCreateConnection extends Dialog {

    private Context context;
    private Connection connection;

    public DialogCreateConnection(Connection connection, Skin skin, Context context) {
        super(connection.toString(), skin);

        this.context = context;
        this.connection = connection;

        text("What material would you like to build your connection with?");

        for (Connection.Material material : Connection.Material.values()) {
            int connectionCost = material.calculateTotalCost(connection.getLength());
            String materialName = material.getName();
            String buttonString = materialName + ": " + Game.CURRENCY_SYMBOL + connectionCost;
            button(buttonString, material);
        }

        button("Cancel", "CANCEL");

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.NEW_CONNECTION_MODE_SELECT_MATERIAL ||
                        event == GameEvent.NEW_CONNECTION_MODE_SELECT_MATERIAL_CANCEL) {
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
            EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.NEW_CONNECTION_MODE_SELECT_MATERIAL_CANCEL));
            this.remove();
        } else {
            context.getConnectionController().createConnection(connection, (Connection.Material) object);
        }
    }
}
