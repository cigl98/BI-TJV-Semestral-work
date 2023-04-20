package cz.cvut.fit.cihlaond.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Band {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String name;

    @ManyToMany
    @JoinTable(name = "band_players",
               joinColumns = @JoinColumn(name = "band_id"),
               inverseJoinColumns = @JoinColumn(name = "player_id")
        )
    private List<Player> players;

    @Version
    private Long version;

    public Band() {
    }

    public Band(String name, List<Player> players) {
        this.name = name;
        this.players = players;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Band band = (Band) o;
        return id == band.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

