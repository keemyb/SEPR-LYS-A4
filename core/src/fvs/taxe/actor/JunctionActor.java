package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gamelogic.map.IPositionable;

public class JunctionActor extends Image {
    private static int width = 16;
    private static int height = 16;

    public JunctionActor(IPositionable location) {
        super(new Texture(Gdx.files.internal("junction-dot.png")));

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }
}
