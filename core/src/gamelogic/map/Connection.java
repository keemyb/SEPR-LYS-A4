package gamelogic.map;

/**
 * This class represents connection between two stations.
 */
public class Connection {
    private Station station1;
    private Station station2;
    private Material material;

    public Connection(Station station1, Station station2, Material material) {
        this.station1 = station1;
        this.station2 = station2;
        this.material = material;
    }

    public Station getStation1() {
        return this.station1;
    }

    public Station getStation2() {
        return this.station2;
    }

    public enum Material {
        GOLD("Gold", 50, 1),
        SILVER("Silver", 30, 0.8f),
        BRONZE("Bronze", 10, 0.5f);

        private String name;
        private int costPerUnitLength;
        private float hardness;
        Material(String name, int costPerUnitLength, float hardness) {
            this.name = name;
            this.costPerUnitLength = costPerUnitLength;
            this.hardness = hardness;
        }

        public int calculateTotalCost(float length) {
            return (int) Math.ceil(length) * costPerUnitLength;
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
