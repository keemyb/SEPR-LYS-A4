package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import gamelogic.Game;
import gamelogic.GameState;
import gamelogic.map.Position;
import gamelogic.resource.Train;

public class TrainActor extends Image {
    public static int width = 36;
    public static int height = 36;
    public Train train;
    private Rectangle bounds;
    private float previousX;
    private Drawable leftDrawable;
    private Drawable rightDrawable;

    public TrainActor(Train train) {
        super(new Texture(Gdx.files.internal(train.getLeftImage())));
        leftDrawable = getDrawable();
        rightDrawable = new Image(new Texture(Gdx.files.internal(train.getRightImage()))).getDrawable();

        Position position = train.getPosition();

        train.setActor(this);
        this.train = train;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        previousX = getX();
    }

    @Override
    public void act(float delta) {
        if (Game.getInstance().getState() == GameState.ANIMATING) {
            super.act(delta);
            updateBounds();
            updateFacingDirection();
            train.setPosition(new Position(getX() + width / 2, getY() + height / 2));
        }
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public void updateFacingDirection() {
        if (previousX < getX()) setDrawable(rightDrawable);
        else if (previousX > getX()) setDrawable(leftDrawable);
        previousX = getX();
    }

    public Rectangle getBounds() {
        return bounds;
    }


}

