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
        private int price;
        private float hardness;
        Material(String name, int price, float hardness) {
            this.name = name;
            this.price = price;
            this.hardness = hardness;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public float getHardness() {
            return hardness;
        }
    }
}
