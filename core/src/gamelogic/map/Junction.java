package gamelogic.map;

import fvs.taxe.actor.JunctionActor;

/**
 * Junction is a special type of station that can fail. While junction is in failed state, trains can't pass through it.
 * Also, junction can't be a final destination on a train route.
 */
public class Junction extends Station {

    private int failureDuration = 0;

    public Junction(String name, String abbreviation, Position location) {
        super(name, abbreviation, location);
    }

    public void decrementFailureDuration() {
        if (failureDuration > 0) {
            failureDuration--;
            if (failureDuration == 0)
                ((JunctionActor) this.getActor()).setDefault();
        }
    }

    public boolean isPassable() {
        return failureDuration == 0;
    }

    public void setFailureDuration(int failureDuration) {
        this.failureDuration = failureDuration;
        if (failureDuration > 0)
            ((JunctionActor) this.getActor()).setBroken();
    }

}
