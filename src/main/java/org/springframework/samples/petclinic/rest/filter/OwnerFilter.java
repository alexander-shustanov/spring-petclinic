package org.springframework.samples.petclinic.rest.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.util.StringUtils;

public record OwnerFilter(String firstNameContains, String lastNameContains, String telephoneStarts) {
	public Specification<Owner> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameContainsSpec())
			.and(telephoneStartsSpec());
	}

	private Specification<Owner> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Owner> lastNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
			? cb.like(cb.lower(root.get("lastName")), "%" + lastNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Owner> telephoneStartsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(telephoneStarts)
			? cb.like(root.get("telephone"), telephoneStarts + "%")
			: null);
	}
}
