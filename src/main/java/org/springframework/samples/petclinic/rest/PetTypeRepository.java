package org.springframework.samples.petclinic.rest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.samples.petclinic.owner.PetType;

public interface PetTypeRepository extends JpaRepository<PetType, Integer>, JpaSpecificationExecutor<PetType> {
}
