package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.map.Connection;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

public class DialogChooseConnectionAction extends Dialog {

    private Context context;

    public DialogChooseConnectionAction(Connection connection, Context context) {
        super(context.getConnectionController().getConnectionHealthString(connection), context.getSkin());

        this.context = context;

        text("What would you like to do with this connection?");

        if (connection.getHealth() < 1) {
            button("Repair", GameEvent.CLICKED_REPAIR_CONNECTION);
        }
        if (connection.isUpgradable(Connection.Material.SILVER) ||
                connection.isUpgradable(Connection.Material.GOLD)) {
            button("Upgrade", GameEvent.CLICKED_UPGRADE_CONNECTION);
        }

        button("Remove", GameEvent.CLICKED_REMOVE_CONNECTION);
        button("Cancel", GameEvent.CLICKED_CONNECTION_BUTTON_CANCEL);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_REPAIR_CONNECTION ||
                        event == GameEvent.CLICKED_UPGRADE_CONNECTION ||
                        event == GameEvent.CLICKED_REMOVE_CONNECTION ||
                        event == GameEvent.CLICKED_CONNECTION_BUTTON_CANCEL) {
                    result(GameEvent.CLICKED_CONNECTION_BUTTON_CANCEL);
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
        GameEvent gameEvent = (GameEvent) object;
        switch (gameEvent) {
            case CLICKED_CONNECTION_BUTTON_CANCEL:
                EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CONNECTION_BUTTON_CANCEL));
                this.remove();
                break;
            case CLICKED_REPAIR_CONNECTION:
                context.getConnectionController().showRepairConnectionDialog();
                break;
            case CLICKED_UPGRADE_CONNECTION:
                context.getConnectionController().showUpgradeConnectionDialog();
                break;
            case CLICKED_REMOVE_CONNECTION:
                context.getConnectionController().showRemoveConnectionDialog();
                break;
            default:
                break;
        }
    }
}