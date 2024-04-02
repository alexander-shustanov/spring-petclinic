package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Owner}
 */
public class OwnerDto implements Serializable {
	private final Integer id;
	@NotBlank
	private final String firstName;
	@NotBlank
	private final String lastName;
	@NotBlank
	private final String address;
	@NotBlank
	private final String city;
	@Digits(integer = 10, fraction = 0)
	@NotBlank
	private final String telephone;

	public OwnerDto(Integer id, String firstName, String lastName, String address, String city, String telephone) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
	}

	public Integer getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getTelephone() {
		return telephone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OwnerDto entity = (OwnerDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.firstName, entity.firstName) &&
			Objects.equals(this.lastName, entity.lastName) &&
			Objects.equals(this.address, entity.address) &&
			Objects.equals(this.city, entity.city) &&
			Objects.equals(this.telephone, entity.telephone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, address, city, telephone);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"firstName = " + firstName + ", " +
			"lastName = " + lastName + ", " +
			"address = " + address + ", " +
			"city = " + city + ", " +
			"telephone = " + telephone + ")";
	}
}
