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
            button("Repair", GameEvent.SELECTED_CONNECTION_REPAIR);
        }
        if (connection.isUpgradable(Connection.Material.SILVER) ||
                connection.isUpgradable(Connection.Material.GOLD)) {
            button("Upgrade", GameEvent.SELECTED_CONNECTION_UPGRADE);
        }

        button("Remove", GameEvent.SELECTED_CONNECTION_REMOVE);
        button("Cancel", GameEvent.SELECTED_CONNECTION_CANCEL);

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.SELECTED_CONNECTION_REPAIR ||
                        event == GameEvent.SELECTED_CONNECTION_UPGRADE ||
                        event == GameEvent.SELECTED_CONNECTION_REMOVE ||
                        event == GameEvent.SELECTED_CONNECTION_CANCEL) {
                    result(GameEvent.SELECTED_CONNECTION_CANCEL);
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
            case SELECTED_CONNECTION_CANCEL:
                EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.SELECTED_CONNECTION_CANCEL));
                this.remove();
                break;
            case SELECTED_CONNECTION_REPAIR:
                context.getConnectionController().showRepairConnectionDialog();
                break;
            case SELECTED_CONNECTION_UPGRADE:
                context.getConnectionController().showUpgradeConnectionDialog();
                break;
            case SELECTED_CONNECTION_REMOVE:
                context.getConnectionController().showRemoveConnectionDialog();
                break;
            default:
                break;
        }
    }
}