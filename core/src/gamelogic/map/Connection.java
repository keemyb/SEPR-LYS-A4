package gamelogic.map;

/**
 * This class represents connection between two stations.
 */
public class Connection {
    private Station station1;
    private Station station2;
    private float length;
    private Material material;

    public Connection(Station station1, Station station2, Material material) {
        this.station1 = station1;
        this.station2 = station2;
        this.material = material;

        float station1X = station1.getLocation().getX();
        float station1Y = station1.getLocation().getY();
        float station2X = station2.getLocation().getX();
        float station2Y = station2.getLocation().getY();
        float dx = station1X - station2X;
        float dy = station1Y - station2Y;
        length = (float) Math.sqrt(dx * dx + dy * dy);
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

    public Station getStation1() {
        return this.station1;
    }

    public Station getStation2() {
        return this.station2;
    }

    public enum Material {
        GOLD("Gold", 50, 0, 1),
        SILVER("Silver", 30, 0.2f, 0.8f),
        BRONZE("Bronze", 10, 0.1f, 0.5f);

        private String name;
        private int costPerUnitLength;
        private float rentPayablePerUnitLength;
        private float hardness;
        Material(String name, int costPerUnitLength, float rentPayablePerUnitLength, float strength) {
            this.name = name;
            this.costPerUnitLength = costPerUnitLength;
            this.rentPayablePerUnitLength = rentPayablePerUnitLength;
            this.hardness = strength;
        }

        public int calculateTotalCost(float length) {
            return (int) Math.ceil(length) * costPerUnitLength;
        }

        public int calculateRentPayable(float length) {
            return (int) (Math.ceil(length) * rentPayablePerUnitLength);
        }

        public String getName() {
            return name;
        }

        public int getCostPerUnitLength() {
            return costPerUnitLength;
        }

        public float getHardness() {
            return hardness;
        }
    }
}
