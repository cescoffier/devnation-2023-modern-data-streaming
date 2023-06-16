package io.quarkus.demos;

import java.time.LocalDateTime;

public class MessageStats {

    private Long id;
    private LocalDateTime time;
    private Double avaragePerSecond;
    private Long index;

    
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    public Double getAvaragePerSecond() {
        return avaragePerSecond;
    }
    
    public void setAvaragePerSecond(Double avaragePerSecond) {
        this.avaragePerSecond = avaragePerSecond;
    }
    
    public Long getIndex() {
        return index;
    }
    
    public void setIndex(Long totalCount) {
        this.index = totalCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    

}
