package gamelogic.map;

import gamelogic.player.Player;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;

/**
 * This class represents connection between two stations.
 */
public class Connection {
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
        repair();
        if (isUpgradable(to)) material = to;
    }

    public void repair() {
        health = 1;
    }

    public boolean isUpgradable(Material to) {
        return material.isUpgradable(to);
    }

    public int calculateUpgradeCost(Material to) {
        return calculateRepairCost() + material.calculateUpgradeCost(to, length);
    }

    public int calculateCost() {
        return material.calculateTotalCost(length);
    }

    public int calculateRepairCost() {
        return (int) (material.calculateRepairCost(length) * (1f - health));
    }

    public void inflictDamage(Train train) {
        float damageToInflict = material.calculateDamageInflicted(train);
        health -= damageToInflict;
        if (health <= 0) health = 0;
    }

    public int calculateAdjustedTrainSpeed(Train train) {
        /* We always want the train to be atleast this fast
        (as a % of it's usual speed) */
        float lowerBound = 0.5f;

        int trainSpeed = train.getSpeed();
        float variableSpeedScale = (1f - lowerBound) * health;
        return (int) ((float) trainSpeed * (lowerBound + variableSpeedScale));
    }

    public int getRentPayable() {
        return material.calculateRentPayable(length);
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

    @Override
    public String toString() {
        return "Connection from " + getStation1().getName() + " to " + getStation2().getName();
    }

    public enum Material {
        GOLD("Gold", 50, 0.3f, 1),
        SILVER("Silver", 30, 0.2f, 0.8f),
        BRONZE("Bronze", 10, 0.1f, 0.5f);

        private String name;
        private int costPerUnitLength;
        private float rentPayablePerUnitLength;
        private float strength;
        Material(String name, int costPerUnitLength, float rentPayablePerUnitLength, float strength) {
            this.name = name;
            this.costPerUnitLength = costPerUnitLength;
            this.rentPayablePerUnitLength = rentPayablePerUnitLength;
            this.strength = strength;
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

        private int calculateRepairCost(float length) {
            return (int) (length * (1f - strength) * Math.log10(costPerUnitLength));
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
            return (1f - strength) * (float) train.getSpeed() / TrainManager.getFastestTrainSpeed();
        }

        public String getName() {
            return name;
        }
    }
}
