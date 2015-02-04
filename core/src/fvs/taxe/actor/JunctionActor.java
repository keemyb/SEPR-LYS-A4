package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import gamelogic.map.Position;

public class JunctionActor extends Image {
    private static int width = 16;
    private static int height = 16;

    private static Texture defaultTexture = new Texture(Gdx.files.internal("junction-dot.png"));
    private static Texture brokenTexture = new Texture(Gdx.files.internal("junction-broken-dot.png"));

    public JunctionActor(Position location) {
        super(defaultTexture);

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }

    public void setBroken() {
        this.setDrawable(new TextureRegionDrawable(new TextureRegion(brokenTexture)));
    }

    public void setDefault() {
        this.setDrawable(new TextureRegionDrawable(new TextureRegion(defaultTexture)));
    }

}
