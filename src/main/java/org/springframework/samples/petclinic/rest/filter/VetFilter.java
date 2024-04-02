package org.springframework.samples.petclinic.rest.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.util.StringUtils;

public record VetFilter(String firstNameContains, String lastNameContains, Integer specialtiesId) {
	public Specification<Vet> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameContainsSpec())
			.and(specialtiesIdSpec());
	}

	private Specification<Vet> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> lastNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
			? cb.like(cb.lower(root.get("lastName")), "%" + lastNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> specialtiesIdSpec() {
		return ((root, query, cb) -> specialtiesId != null
			? cb.equal(root.get("specialties").get("id"), specialtiesId)
			: null);
	}
}
