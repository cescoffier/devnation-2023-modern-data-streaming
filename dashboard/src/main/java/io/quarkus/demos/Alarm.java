package io.quarkus.demos;

public class Alarm {

    private AlarmType type;
    private String machineId;
    private long timestamp;
    
    public AlarmType getType() {
        return type;
    }
    public void setType(AlarmType type) {
        this.type = type;
    }
    public String getMachineId() {
        return machineId;
    }
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    
    
}
