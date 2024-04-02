package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.vet.Specialty;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpecialtyMapper {
	Specialty toEntity(SpecialtyDto specialtyDto);

	SpecialtyDto toDto(Specialty specialty);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Specialty partialUpdate(SpecialtyDto specialtyDto, @MappingTarget Specialty specialty);

	Specialty updateWithNull(SpecialtyDto specialtyDto, @MappingTarget Specialty specialty);
}
