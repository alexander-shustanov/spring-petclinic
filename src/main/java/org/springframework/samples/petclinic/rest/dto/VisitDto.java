package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Visit}
 */
public class VisitDto implements Serializable {
	private final Integer id;
	private final LocalDate date;
	@NotBlank
	private final String description;
	private final Integer petId;
	private final Integer assignedVetId;

	public VisitDto(Integer id, LocalDate date, String description, Integer petId, Integer assignedVetId) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.petId = petId;
		this.assignedVetId = assignedVetId;
	}

	public Integer getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public Integer getPetId() {
		return petId;
	}

	public Integer getAssignedVetId() {
		return assignedVetId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VisitDto entity = (VisitDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.date, entity.date) &&
			Objects.equals(this.description, entity.description) &&
			Objects.equals(this.petId, entity.petId) &&
			Objects.equals(this.assignedVetId, entity.assignedVetId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, date, description, petId, assignedVetId);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"date = " + date + ", " +
			"description = " + description + ", " +
			"petId = " + petId + ", " +
			"assignedVetId = " + assignedVetId + ")";
	}
}
