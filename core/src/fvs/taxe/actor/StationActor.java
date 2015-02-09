package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    public StationActor(Station station) {
        super();
        stationLabel = new Label(station.getAbbreviation(), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        stationDot.setBounds(0, 0, width, height);
        stationLabel.setPosition(-22, 18);

        //setSize(width, height);
        setPosition(station.getLocation().getX() - width / 2, station.getLocation().getY() - height / 2);
        addActor(stationDot);
        addActor(stationLabel);
    }
}
