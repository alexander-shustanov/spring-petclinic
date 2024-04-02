package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetTypeMapper {
	PetType toEntity(PetTypeDto petTypeDto);

	PetTypeDto toDto(PetType petType);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	PetType partialUpdate(PetTypeDto petTypeDto, @MappingTarget PetType petType);

	PetType updateWithNull(PetTypeDto petTypeDto, @MappingTarget PetType petType);
}
