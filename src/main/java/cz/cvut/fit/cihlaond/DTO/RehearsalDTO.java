package cz.cvut.fit.cihlaond.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Objects;

public class RehearsalDTO {
    private final int id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Integer rehearsalOfBandId;

    @JsonIgnore
    private final Long version;

    public RehearsalDTO(int id, LocalDateTime startTime, LocalDateTime endTime, Integer rehearsalOfBandId, Long version) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rehearsalOfBandId = rehearsalOfBandId;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getRehearsalOfBandId() {
        return rehearsalOfBandId;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RehearsalDTO that = (RehearsalDTO) o;
        return id == that.id &&
                startTime.equals(that.startTime) &&
                endTime.equals(that.endTime) &&
                rehearsalOfBandId.equals(that.rehearsalOfBandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, rehearsalOfBandId);
    }
}
