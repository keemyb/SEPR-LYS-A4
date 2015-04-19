package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.controller.Context;
import gamelogic.game.GameEvent;
import gamelogic.map.Connection;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;
import gamelogic.replay.ReplayListener;

public class DialogRemoveConnection extends Dialog {

    private Context context;
    private Connection connection;

    public DialogRemoveConnection(Context context, Connection connection) {
        super(connection.toString(), context.getSkin());

        this.context = context;
        this.connection = connection;

        text("Would you like to destroy your connection?");

        button("Yes", "YES");
        button("No", "NO");

        EventReplayer.subscribeReplayEvent(new ReplayListener() {
            @Override
            public void replay(GameEvent event, Object object) {
                if (event == GameEvent.CLICKED_CHOOSE_REMOVE_CONNECTION ||
                        event == GameEvent.CLICKED_CHOOSE_REMOVE_CONNECTION_CANCEL) {
                    result("NO");
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
    protected void result(Object obj) {
        if (obj == "YES") {
            context.getConnectionController().removeConnection(connection);
        } else {
            context.getEventReplayer().saveReplayEvent(new ReplayEvent(GameEvent.CLICKED_CHOOSE_REMOVE_CONNECTION_CANCEL));
            this.remove();
        }
    }
}
