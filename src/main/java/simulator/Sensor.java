package simulator;

import gate.SensorAPI;

public class Sensor {

    SensorAPI api;
    int localId;
    int globalId;

    public SensorAPI getAPI() {
        return api;
    }

    public int getLocalId() {
        return localId;
    }

    public int getGlobalId() {
        return globalId;
    }

    Sensor(SensorAPI api, int localId, int globalId) {
        this.api = api;
        this.localId = localId;
        this.globalId = globalId;
    }
}
