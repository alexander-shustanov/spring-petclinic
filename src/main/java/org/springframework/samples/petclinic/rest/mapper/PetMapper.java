package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.rest.dto.PetDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetMapper {
	@Mapping(source = "ownerId", target = "owner.id")
	@Mapping(source = "typeId", target = "type.id")
	Pet toEntity(PetDto petDto);

	@InheritInverseConfiguration(name = "toEntity")
	PetDto toDto(Pet pet);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "ownerId", target = "owner")
	@Mapping(source = "typeId", target = "type")
	Pet partialUpdate(PetDto petDto, @MappingTarget Pet pet);

	default PetType createPetType(Integer typeId) {
		if (typeId == null) {
			return null;
		}
		PetType petType = new PetType();
		petType.setId(typeId);
		return petType;
	}

	default Owner createOwner(Integer ownerId) {
		if (ownerId == null) {
			return null;
		}
		Owner owner = new Owner();
		owner.setId(ownerId);
		return owner;
	}

	@InheritConfiguration(name = "partialUpdate")
	Pet updateWithNull(PetDto petDto, @MappingTarget Pet pet);
}
