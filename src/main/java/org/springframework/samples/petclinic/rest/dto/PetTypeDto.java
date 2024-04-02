package org.springframework.samples.petclinic.rest.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.PetType}
 */
public class PetTypeDto implements Serializable {
	private final Integer id;
	private final String name;

	public PetTypeDto(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PetTypeDto entity = (PetTypeDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.name, entity.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"name = " + name + ")";
	}
}
