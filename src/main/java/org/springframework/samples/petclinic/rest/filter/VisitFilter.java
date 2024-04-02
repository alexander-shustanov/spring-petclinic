package org.springframework.samples.petclinic.rest.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;

public record VisitFilter(Integer petId, Integer assignedVetId, LocalDate dateGte, LocalDate dateLte) {
	public Specification<Visit> toSpecification() {
		return Specification.where(petIdSpec())
			.and(assignedVetIdSpec())
			.and(dateGteSpec())
			.and(dateLteSpec());
	}

	private Specification<Visit> petIdSpec() {
		return ((root, query, cb) -> petId != null
			? cb.equal(root.get("pet").get("id"), petId)
			: null);
	}

	private Specification<Visit> assignedVetIdSpec() {
		return ((root, query, cb) -> assignedVetId != null
			? cb.equal(root.get("assignedVet").get("id"), assignedVetId)
			: null);
	}

	private Specification<Visit> dateGteSpec() {
		return ((root, query, cb) -> dateGte != null
			? cb.greaterThanOrEqualTo(root.get("date"), dateGte)
			: null);
	}

	private Specification<Visit> dateLteSpec() {
		return ((root, query, cb) -> dateLte != null
			? cb.lessThanOrEqualTo(root.get("date"), dateLte)
			: null);
	}
}
