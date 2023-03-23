package cz.cvut.fit.cihlaond.DTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class RehearsalCreateDTO {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Integer rehearsalOfBandId;

    public RehearsalCreateDTO(LocalDateTime startTime, LocalDateTime endTime, Integer rehearsalOfBandId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.rehearsalOfBandId = rehearsalOfBandId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RehearsalCreateDTO that = (RehearsalCreateDTO) o;
        return startTime.equals(that.startTime) &&
                endTime.equals(that.endTime) &&
                rehearsalOfBandId.equals(that.rehearsalOfBandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, rehearsalOfBandId);
    }
}
