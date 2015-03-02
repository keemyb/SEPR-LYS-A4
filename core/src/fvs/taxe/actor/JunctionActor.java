package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import gamelogic.map.Position;

public class JunctionActor extends Group {
    public static int width = 16;
    private static int height = 16;

    private Image defaultTexture = new Image(new Texture(Gdx.files.internal("junction-dot.png")));
    private Image brokenTexture = new Image(new Texture(Gdx.files.internal("junction-broken-dot.png")));

    public JunctionActor(Position location) {
        super();
        defaultTexture.setBounds(0, 0, width, height);
        brokenTexture.setBounds(0, 0, width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        addActor(defaultTexture);
    }

    public void setBroken() {
        removeActor(defaultTexture);
        addActor(brokenTexture);
    }

    public void setDefault() {
        removeActor(brokenTexture);
        addActor(defaultTexture);
    }

}
