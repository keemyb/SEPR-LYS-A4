
package gamelogic.recording;
import gamelogic.map.Position;
import gamelogic.resource.Train;

import java.util.HashMap;

/**
 * Stores the state of the game at every turn
 *
 */

// Add a hashmap for random events as well such as junction failures.

public class RecordState {
    public HashMap turnHashMap;
    public HashMap trainLocationHashMap;
    private HashMap trainUsedHashMap;
    private int playerID; // Player for which we are recording the states

    private RecordState(int playerNumber ) {
        HashMap<Integer, Integer> turnHashMap = new HashMap<>();
        HashMap<Integer, Position> trainLocationHashMap = new HashMap<>();
        HashMap<Integer, Train> trainUsedHashMap = new HashMap<>();
        this.playerID = playerNumber;
    }



    public HashMap getTurnHashMap() {
        return turnHashMap;
    }

    public void setTurnHashMap(int timeStamp, int turnNumber) {
        this.turnHashMap.put(timeStamp, turnNumber);
    }

    public HashMap getTrainLocationHashMap() {
        return trainLocationHashMap;
    }

    public void setTrainLocationHashMap(int turnNumber, Position trainPosition) {
        this.trainLocationHashMap.put(turnNumber, trainPosition);
    }

    public HashMap getTrainUsedHashMap() {
        return trainUsedHashMap;
    }

    public void setTrainUsedHashMap(int turnNumber, Train trainUsed) {
        this.trainUsedHashMap.put(turnNumber, trainUsed);
    }

    public int getPlayerID() {
        return playerID;
    }


}

