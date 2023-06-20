package io.quarkus.demos;

public class ImageProcessedStats {
    
    long timestamp;
    long totalProcessed;
    double averageProcessed;
    
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getTotalProcessed() {
        return totalProcessed;
    }
    public void setTotalProcessed(long totalProcessed) {
        this.totalProcessed = totalProcessed;
    }
    public double getAverageProcessed() {
        return averageProcessed;
    }
    public void setAverageProcessed(double averageProcessed) {
        this.averageProcessed = averageProcessed;
    }

    
}
