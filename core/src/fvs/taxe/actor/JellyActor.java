package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.map.Jelly;
import gamelogic.map.Position;

public class JellyActor extends Image {
    public static int width = 40;
    public static int height = 30;
    public Jelly jelly;
    private Rectangle bounds;

    public JellyActor(Jelly jelly) {
        super(new Texture(Gdx.files.internal("jelly 1.png")));

        Position position = jelly.getPosition();

        jelly.setActor(this);
        this.jelly = jelly;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);

        setTouchable(Touchable.disabled);
    }

    @Override
    public void act(float delta) {
        if (Game.getInstance().getState() == GameState.ANIMATING) {
            super.act(delta);
            updateBounds();
            jelly.setPosition(new Position(getX() + width / 2, getY() + height / 2));
        }
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }


}

