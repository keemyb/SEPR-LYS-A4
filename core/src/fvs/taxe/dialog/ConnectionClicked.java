package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import gamelogic.map.Connection;

public class ConnectionClicked extends ClickListener {
    private Context context;
    private Connection connection;

    public ConnectionClicked(Context context, Connection connection) {
        this.connection = connection;
        this.context = context;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        context.getConnectionController().setSelectedStations(connection);
    }
    //@Override
    //public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    //    context.getConnectionController().highlightConnection(connection);
    //}
}
