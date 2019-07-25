package gate;

public class Lane {

    int id = 0;
    Gate gate;

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
