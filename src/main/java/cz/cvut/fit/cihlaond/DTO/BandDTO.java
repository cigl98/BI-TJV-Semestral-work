package cz.cvut.fit.cihlaond.DTO;

import java.util.List;
import java.util.Objects;

public class BandDTO {
    private final int id;
    private final String name;
    private final List<Integer> playerIds;

    public BandDTO(int id, String name, List<Integer> playerIds) {
        this.id = id;
        this.name = name;
        this.playerIds = playerIds;
    }

    public int getId() {
        return id;
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
        BandDTO bandDTO = (BandDTO) o;
        return id == bandDTO.id &&
                name.equals(bandDTO.name) &&
                playerIds.equals(bandDTO.playerIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, playerIds);
    }
}
