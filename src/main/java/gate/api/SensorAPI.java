package gate.api;

import gate.Gate;

public class SensorAPI {

    private Gate gate;

    public SensorAPI(Gate gate) {
        this.gate = gate;
    }

    public Gate getGate() {
        return gate;
    }
}
