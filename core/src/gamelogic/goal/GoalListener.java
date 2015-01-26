package gamelogic.goal;


import util.Tuple;

import java.util.List;

public interface GoalListener {
    public void stationReached(List<Tuple<String, Integer>> history);
}
