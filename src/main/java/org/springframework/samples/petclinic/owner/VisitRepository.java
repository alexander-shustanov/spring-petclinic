package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.samples.petclinic.vet.Vet;

import java.time.LocalDate;

public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {
	boolean existsByAssignedVetAndDate(Vet assignedVet, LocalDate date);
}
