package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetMapper {
	Vet toEntity(VetDto vetDto);

	@Mapping(target = "specialtyIds", expression = "java(specialtiesToSpecialtyIds(vet.getSpecialties()))")
	VetDto toDto(Vet vet);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Vet partialUpdate(VetDto vetDto, @MappingTarget Vet vet);

	default List<Integer> specialtiesToSpecialtyIds(List<Specialty> specialties) {
		return specialties.stream().map(Specialty::getId).collect(Collectors.toList());
	}

	Vet updateWithNull(VetDto vetDto, @MappingTarget Vet vet);
}
