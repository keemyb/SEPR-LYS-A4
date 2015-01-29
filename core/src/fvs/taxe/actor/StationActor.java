package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gamelogic.map.Position;

public class StationActor extends Image {
    private static int width = 20;
    private static int height = 20;

    public StationActor(Position location) {
        super(new Texture(Gdx.files.internal("station-dot.png")));

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }
}
