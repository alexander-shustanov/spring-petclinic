package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link org.springframework.samples.petclinic.vet.Vet}
 */
public class VetDto implements Serializable {
	private final Integer id;
	@NotBlank
	private final String firstName;
	@NotBlank
	private final String lastName;
	private final BigDecimal salary;
	private final List<Integer> specialtyIds;

	public VetDto(Integer id, String firstName, String lastName, BigDecimal salary, List<Integer> specialtyIds) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.specialtyIds = specialtyIds;
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

	public BigDecimal getSalary() {
		return salary;
	}

	public List<Integer> getSpecialtyIds() {
		return specialtyIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VetDto entity = (VetDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.firstName, entity.firstName) &&
			Objects.equals(this.lastName, entity.lastName) &&
			Objects.equals(this.salary, entity.salary) &&
			Objects.equals(this.specialtyIds, entity.specialtyIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, salary, specialtyIds);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"firstName = " + firstName + ", " +
			"lastName = " + lastName + ", " +
			"salary = " + salary + ", " +
			"specialtyIds = " + specialtyIds + ")";
	}
}
