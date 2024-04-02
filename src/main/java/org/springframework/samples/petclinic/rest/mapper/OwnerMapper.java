package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerMapper {
	Owner toEntity(OwnerDto ownerDto);

	OwnerDto toDto(Owner owner);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Owner partialUpdate(OwnerDto ownerDto, @MappingTarget Owner owner);

	Owner updateWithNull(OwnerDto ownerDto, @MappingTarget Owner owner);
}
