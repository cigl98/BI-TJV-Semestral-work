package cz.cvut.fit.cihlaond.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Rehearsal {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band rehearsalOfBand;

    public Rehearsal() {
    }

    public Rehearsal(LocalDateTime startTime, LocalDateTime endTime, Band rehearsalOfBand) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.rehearsalOfBand = rehearsalOfBand;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Band getRehearsalOfBand() {
        return rehearsalOfBand;
    }

    public void setRehearsalOfBand(Band rehearsalOfBand) {
        this.rehearsalOfBand = rehearsalOfBand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rehearsal rehearsal = (Rehearsal) o;
        return id == rehearsal.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
