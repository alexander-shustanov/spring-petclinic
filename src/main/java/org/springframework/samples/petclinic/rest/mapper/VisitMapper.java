package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.vet.Vet;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitMapper {
	@Mapping(source = "assignedVetId", target = "assignedVet.id")
	@Mapping(source = "petId", target = "pet.id")
	Visit toEntity(VisitDto visitDto);

	@InheritInverseConfiguration(name = "toEntity")
	VisitDto toDto(Visit visit);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "assignedVetId", target = "assignedVet")
	@Mapping(source = "petId", target = "pet")
	Visit partialUpdate(VisitDto visitDto, @MappingTarget Visit visit);

	default Pet createPet(Integer petId) {
		if (petId == null) {
			return null;
		}
		Pet pet = new Pet();
		pet.setId(petId);
		return pet;
	}

	default Vet createVet(Integer assignedVetId) {
		if (assignedVetId == null) {
			return null;
		}
		Vet vet = new Vet();
		vet.setId(assignedVetId);
		return vet;
	}

	@InheritConfiguration(name = "partialUpdate")
	Visit updateWithNull(VisitDto visitDto, @MappingTarget Visit visit);
}
