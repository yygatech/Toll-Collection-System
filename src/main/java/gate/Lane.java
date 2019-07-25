package gate;

public class Lane {

    private int id = 0;
    private Gate gate;

    public Lane(int id, Gate gate) {
        this.id = id;
        this.gate = gate;
    }

    public int getLaneId() {
        return id;
    }

    public Gate getGate() {
        return gate;
    }

    public int getGateId() {
        return getGate().getGateId();
    }
}
