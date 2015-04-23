package gamelogic.map;

import com.badlogic.gdx.graphics.Color;
import gamelogic.player.Player;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents connection between two stations.
 */
public class Connection {
    public static final List<Float> repairThresholds = new ArrayList<>(Arrays.asList(0.33f, 0.66f, 1f));

    private Station station1;
    private Station station2;
    private float length;
    private float health = 1;
    private Material material;
    private Player owner = null;

    public Connection(Station station1, Station station2, Material material) {
        this.station1 = station1;
        this.station2 = station2;
        this.material = material;

        length = Station.getDistance(station1, station2);
    }

    public void upgrade(Material to) {
        if (isUpgradable(to)) {
            material = to;
            repair(1);
        }
    }

    public void repair(float to) {
        health = to;
    }

    public boolean isUpgradable(Material to) {
        return material.isUpgradable(to);
    }

    public int calculateUpgradeCost(Material to) {
        return calculateRepairCost(1) + material.calculateUpgradeCost(to, length);
    }

    public Color getColour () {
        return material.getColour();
    }

    public int calculateCost() {
        return material.calculateTotalCost(length);
    }

    public int calculateRepairCost(float to) {
        return (int) (material.calculateRepairCost(length) * (to - health));
    }

    public void inflictDamage(Train train) {
        float damageToInflict = material.calculateDamageInflicted(train);
        health -= damageToInflict;
        if (health <= 0) health = 0;
    }

    public int calculateAdjustedTrainSpeed(Train train) {
        /* We always want the train to be atleast this fast
        (as a % of it's usual speed) */
        float lowerBound = 0.2f;

        int trainSpeed = train.getSpeed();
        float variableSpeedScale = (1f - lowerBound) * health;
        return (int) ((float) trainSpeed * (lowerBound + variableSpeedScale));
    }

    public int getRentPayable() {
        return (int) (material.calculateRentPayable(length) * health);
    }

    public boolean hasCommonStation(Connection connection) {
        if (connection.getStation1().equals(station1)) return true;
        if (connection.getStation1().equals(station2)) return true;
        if (connection.getStation2().equals(station1)) return true;
        if (connection.getStation2().equals(station2)) return true;
        return false;
    }

    public void setOwner(Player player) {
        owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public Station getStation1() {
        return this.station1;
    }

    public Station getStation2() {
        return this.station2;
    }

    public float getLength() {
        return length;
    }

    public float getHealth() {
        return health;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "Connection from " + getStation1().getName() + " to " + getStation2().getName();
    }

    public enum Material {
        GOLD("Gold", 8, 0.6f, 1, new Color(255/255f, 139/255f, 25/255f, 1f)),
        SILVER("Silver", 3, 0.45f, 0.85f, Color.GRAY),
        BRONZE("Bronze", 1, 0.20f, 0.65f, new Color(153/255f, 73/255f, 40/255f, 1f));

        private String name;
        private int costPerUnitLength;
        private float rentPayablePerUnitLength;
        private float strength;
        private Color color;
        Material(String name, int costPerUnitLength, float rentPayablePerUnitLength, float strength, Color color) {
            this.name = name;
            this.costPerUnitLength = costPerUnitLength;
            this.rentPayablePerUnitLength = rentPayablePerUnitLength;
            this.strength = strength;
            this.color = color;
        }

        private boolean isUpgradable(Material to) {
            switch (this) {
                case GOLD:
                    return false;
                case SILVER:
                    return to.equals(GOLD);
                case BRONZE:
                    return !to.equals(BRONZE);
            }
            return false;
        }

        public Color getColour () {
            return color;
        }

        private int calculateRepairCost(float length) {
            return (int) (length * costPerUnitLength * 0.75);
        }

        public int calculateTotalCost(float length) {
            return (int) Math.ceil(length) * costPerUnitLength;
        }

        private int calculateRentPayable(float length) {
            return (int) (Math.ceil(length) * rentPayablePerUnitLength);
        }

        private int calculateUpgradeCost(Material to, float length) {
            return (int) ((to.costPerUnitLength - costPerUnitLength) * length);
        }

        private float calculateDamageInflicted(Train train) {
            return (1f - strength) * (train.getSpeed() / 115f) * 0.75f; //115, fastest train speed
        }

        public String getName() {
            return name;
        }
    }
}
