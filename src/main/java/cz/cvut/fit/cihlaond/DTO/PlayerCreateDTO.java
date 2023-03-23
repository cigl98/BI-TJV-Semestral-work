package cz.cvut.fit.cihlaond.DTO;

import java.util.Objects;

public class PlayerCreateDTO {
    private final String firstName;
    private final String lastName;
    private final String instrument;

    public PlayerCreateDTO(String firstName, String lastName, String instrument) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.instrument = instrument;
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
        PlayerCreateDTO that = (PlayerCreateDTO) o;
        return firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                instrument.equals(that.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, instrument);
    }
}

