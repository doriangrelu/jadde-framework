package fr.jadde.test.fmk.bundle.validation.mock.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class MyEntity {

    @JsonProperty("firstname")
    @NotNull
    @Size(min = 1, max = 150)
    @Pattern(regexp = "^[a-z]+$")
    private String firstname;

    @JsonProperty("lastname")
    @NotNull
    @Size(min = 1, max = 150)
    private String lastname;


    public String firstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String lastname() {
        return this.lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final MyEntity entity)) return false;
        if (!this.firstname.equals(entity.firstname)) return false;
        return this.lastname.equals(entity.lastname);
    }

    @Override
    public int hashCode() {
        int result = this.firstname.hashCode();
        result = 31 * result + this.lastname.hashCode();
        return result;
    }
}
