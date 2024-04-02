package org.springframework.samples.petclinic.rest.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.util.StringUtils;

public record PetFilter(String nameContains, Integer ownerId) {
	public Specification<Pet> toSpecification() {
		return Specification.where(nameContainsSpec())
			.and(ownerIdSpec());
	}

	private Specification<Pet> nameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(nameContains)
			? cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Pet> ownerIdSpec() {
		return ((root, query, cb) -> ownerId != null
			? cb.equal(root.get("owner").get("id"), ownerId)
			: null);
	}
}
