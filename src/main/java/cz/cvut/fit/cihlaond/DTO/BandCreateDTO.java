package cz.cvut.fit.cihlaond.DTO;

import java.util.List;
import java.util.Objects;

public class BandCreateDTO {
    private final String name;
    private final List<Integer> playerIds;

    public BandCreateDTO(String name, List<Integer> playerIds) {
        this.name = name;
        this.playerIds = playerIds;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPlayerIds() {
        return playerIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BandCreateDTO that = (BandCreateDTO) o;
        return name.equals(that.name) &&
                playerIds.equals(that.playerIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, playerIds);
    }
}
