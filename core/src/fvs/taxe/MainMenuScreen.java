package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class MainMenuScreen extends ScreenAdapter {
    private TaxeGame game;
    private OrthographicCamera camera;
    private Rectangle playBounds;
    private Rectangle exitBounds;
    private Vector3 touchPoint;
    private Sprite mapSprite;
    private Viewport viewport;


    public MainMenuScreen(TaxeGame game) {
        this.game = game;


        playBounds = new Rectangle(TaxeGame.WORLD_WIDTH / 2 - 200, 350, 400, 100);
        exitBounds = new Rectangle(TaxeGame.WORLD_WIDTH / 2 - 200, 200, 400, 100);
        touchPoint = new Vector3();
        mapSprite = new Sprite(new Texture(Gdx.files.internal("game-map.png")));
        mapSprite.setPosition(0, 0);

        camera = new OrthographicCamera();
        viewport = new StretchViewport(TaxeGame.WORLD_WIDTH, TaxeGame.WORLD_HEIGTH, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);


    }

    public void update() {
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new GameScreen(game));
                return;
            }
            if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.exit();
            }
        }
    }

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw transparent map in the background

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        Color c = game.batch.getColor();
        game.batch.setColor(c.r, c.g, c.b, (float) 0.3);
        game.batch.draw(mapSprite, 0, 0, TaxeGame.WORLD_WIDTH, TaxeGame.WORLD_HEIGTH);
        game.batch.setColor(c);
        game.batch.end();

        //Draw rectangles, did not use TextButtons because it was easier not to
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(Color.GREEN);
        game.shapeRenderer.rect(playBounds.getX(), playBounds.getY(), playBounds.getWidth(), playBounds.getHeight());
        game.shapeRenderer.setColor(Color.RED);
        game.shapeRenderer.rect(exitBounds.getX(), exitBounds.getY(), exitBounds.getWidth(), exitBounds.getHeight());
        game.shapeRenderer.end();

        //Draw text into rectangles
        game.batch.begin();
        String startGameString = "Start game";
        game.font.draw(game.batch, startGameString, playBounds.getX() + playBounds.getWidth() / 2 - game.font.getBounds(startGameString).width / 2,
                playBounds.getY() + playBounds.getHeight() / 2 + game.font.getBounds(startGameString).height / 2); // center the text
        String exitGameString = "Exit";
        game.font.draw(game.batch, exitGameString, exitBounds.getX() + exitBounds.getWidth() / 2 - game.font.getBounds(exitGameString).width / 2,
                exitBounds.getY() + exitBounds.getHeight() / 2 + game.font.getBounds(exitGameString).height / 2); // center the text

        game.batch.end();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

}