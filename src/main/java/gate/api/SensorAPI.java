package gate.api;

import gate.Gate;
import org.json.JSONObject;

public class SensorAPI {

    private Gate gate;

    public SensorAPI(Gate gate) {
        this.gate = gate;
    }

    public Gate getGate() {
        return gate;
    }

    // TODO: real implementation
    public void detect(JSONObject vehicle) {
        gate.receiveFrSensor(vehicle);
    }
}
