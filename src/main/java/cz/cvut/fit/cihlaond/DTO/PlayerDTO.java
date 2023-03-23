package cz.cvut.fit.cihlaond.DTO;

import java.util.Objects;

public class PlayerDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String instrument;

    public PlayerDTO(int id, String firstName, String lastName, String instrument) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.instrument = instrument;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getInstrument() {
        return instrument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerDTO playerDTO = (PlayerDTO) o;
        return id == playerDTO.id &&
                firstName.equals(playerDTO.firstName) &&
                lastName.equals(playerDTO.lastName) &&
                instrument.equals(playerDTO.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, instrument);
    }
}

