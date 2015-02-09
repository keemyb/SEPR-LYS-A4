package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import gamelogic.map.Position;
import gamelogic.map.Station;

public class StationActor extends Group {
    private static int width = 20;
    private static int height = 20;
    private Image stationDot = new Image(new Texture(Gdx.files.internal("station-dot.png")));
    private Label stationLabel;
    private Station station;
    public BitmapFont font;



    public StationActor(Station station) {
        super();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("segoe-script-bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // font size 14 pixels
        parameter.size = 14;
        font = generator.generateFont(parameter);
        generator.dispose();

        stationLabel = new Label(station.getAbbreviation(), new Label.LabelStyle(font , Color.MAROON));
        stationDot.setBounds(0, 0, width, height);
        stationLabel.setPosition(-30, 18);

        //setSize(width, height);
        setPosition(station.getLocation().getX() - width / 2, station.getLocation().getY() - height / 2);
        addActor(stationDot);
        addActor(stationLabel);
    }
}
