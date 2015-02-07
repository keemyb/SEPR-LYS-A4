package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import gamelogic.GameState;
import gamelogic.map.Junction;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class RouteController {
    private Context context;
    private Group routingButtons = new Group();
    private List<Position> positions;
    private boolean isRouting = false;
    private Train train;
    private boolean canEndRouting = true;
    //added to calculate total number of turns to complete route
    private float totalRouteDistance;

    public RouteController(Context context) {
        this.context = context;

        StationController.subscribeStationClick(new StationClickListener() {
            @Override
            public void clicked(Station station) {
                if (isRouting) {
                    addStationToRoute(station);
                }
            }
        });
    }

    public void begin(Train train) {
        this.train = train;
        isRouting = true;
        positions = new ArrayList<Position>();
        //initialise route distance
        totalRouteDistance = 0;
        positions.add(train.getPosition());
        context.getGameLogic().setState(GameState.ROUTING);
        addRoutingButtons();


        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, false);
        train.getActor().setVisible(true);
    }

    private float getDistance(Position a, Position b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        Position lastPosition = positions.get(positions.size() - 1);
        Station lastStation = context.getGameLogic().getMap().getStationFromPosition(lastPosition);

        boolean hasConnection = context.getGameLogic().getMap().doesConnectionExist(lastPosition, station.getLocation());

        if (!hasConnection) {
            context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
        }
        else if (positions.contains(station.getLocation())) {
            context.getTopBarController().displayFlashMessage("You can not visit the same station twice in one route.", Color.RED);
        }
        else {
            positions.add(station.getLocation());

            //recalculate the total distance of the route when station/junction is added
            totalRouteDistance += getDistance(positions.get(positions.size() - 1), positions.get(positions.size() - 2));

            System.out.println("totalRouteDistance =" + totalRouteDistance);
            System.out.println("total turns = " + totalTurns());

            context.getTopBarController().displayFlashMessage("This route will take approximately " + totalTurns() + " turns to complete.", Color.BLACK, 1000);



            canEndRouting = !(station instanceof Junction);
        }
    }

    private int totalTurns() {
        //calculates how many turns it will take to complete the chosen route
        float distance = totalRouteDistance;
        float trainSpeed = train.getSpeed();
        float turns = distance / trainSpeed;
        return (int) Math.ceil(turns);
    }

    private void addRoutingButtons() {
        TextButton doneRouting = new TextButton("Route Complete", context.getSkin());
        TextButton cancel = new TextButton("Cancel", context.getSkin());

        doneRouting.setPosition(TaxeGame.WORLD_WIDTH - 250, TaxeGame.WORLD_HEIGHT - 33);
        cancel.setPosition(TaxeGame.WORLD_WIDTH - 100, TaxeGame.WORLD_HEIGHT - 33);

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endRouting();
            }
        });

        doneRouting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!canEndRouting) {
                    context.getTopBarController().displayFlashMessage("Your route must end at a station", Color.RED);
                    return;
                }

                confirmed();
                endRouting();
            }
        });

        routingButtons.addActor(doneRouting);
        routingButtons.addActor(cancel);

        context.getStage().addActor(routingButtons);
    }

    private void confirmed() {
        train.setRoute(positions);

        TrainMoveController move = new TrainMoveController(context, train);
    }

    private void endRouting() {
        context.getGameLogic().setState(GameState.NORMAL);
        routingButtons.remove();
        isRouting = false;

        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, true);
        train.getActor().setVisible(context.getGameLogic().getMap().getStationFromPosition(train.getPosition()) == null);
        context.getTopBarController().clearFlashMessage();
    }

    public void drawRoute(Color color) {
        TaxeGame game = context.getTaxeGame();

        Position previousPosition = null;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(color);

        for (Position position : positions) {
            if (previousPosition != null) {
                game.shapeRenderer.rectLine(previousPosition.getX(), previousPosition.getY(), position.getX(),
                        position.getY(), StationController.CONNECTION_LINE_WIDTH);
            }

            previousPosition = position;
        }

        game.shapeRenderer.end();
    }
}
