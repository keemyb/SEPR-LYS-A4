package fvs.taxe.controller;

import fvs.taxe.actor.JellyActor;
import gamelogic.map.Jelly;

public class JellyController {

    private Context context;

    public JellyController(Context context) {
        this.context = context;
    }

    public void renderJellies() {
        for (Jelly j : context.getGameLogic().getMap().getJellies()) {
            JellyActor actor = new JellyActor(j);
            actor.setPosition(j.getPosition().getX() - JellyActor.width / 2, j.getPosition().getY() - JellyActor.height / 2);
            j.setActor(actor);
            context.getStage().addActor(actor);
            j.startMoving();
        }
    }

}
