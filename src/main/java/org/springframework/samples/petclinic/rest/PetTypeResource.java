package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.mapper.PetTypeMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/types")
public class PetTypeResource {

	private final PetTypeRepository petTypeRepository;

	private final PetTypeMapper petTypeMapper;

	private final ObjectPatcher objectPatcher;

	public PetTypeResource(PetTypeRepository petTypeRepository,
						   PetTypeMapper petTypeMapper,
						   ObjectPatcher objectPatcher) {
		this.petTypeRepository = petTypeRepository;
		this.petTypeMapper = petTypeMapper;
		this.objectPatcher = objectPatcher;
	}

	@GetMapping
	public Page<PetTypeDto> getList(Pageable pageable) {
		Page<PetType> petTypes = petTypeRepository.findAll(pageable);
		Page<PetTypeDto> petTypeDtoPage = petTypes.map(petTypeMapper::toDto);
		return petTypeDtoPage;
	}

	@GetMapping("/{id}")
	public PetTypeDto getOne(@PathVariable Integer id) {
		Optional<PetType> petTypeOptional = petTypeRepository.findById(id);
		PetTypeDto petTypeDto = petTypeMapper.toDto(petTypeOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return petTypeDto;
	}

	@GetMapping("/by-ids")
	public List<PetTypeDto> getMany(@RequestParam List<Integer> ids) {
		List<PetType> petTypes = petTypeRepository.findAllById(ids);
		List<PetTypeDto> petTypeDtos = petTypes.stream()
			.map(petTypeMapper::toDto)
			.toList();
		return petTypeDtos;
	}

	@PostMapping
	public PetTypeDto create(@RequestBody PetTypeDto dto) {
		PetType petType = petTypeMapper.toEntity(dto);
		PetType resultPetType = petTypeRepository.save(petType);
		return petTypeMapper.toDto(resultPetType);
	}

	@PatchMapping("/{id}")
	public PetTypeDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		PetType petType = petTypeRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		PetTypeDto petTypeDto = petTypeMapper.toDto(petType);
		petTypeDto = objectPatcher.patchAndValidate(petTypeDto, patchNode);
		petTypeMapper.updateWithNull(petTypeDto, petType);

		PetType resultPetType = petTypeRepository.save(petType);
		return petTypeMapper.toDto(resultPetType);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<PetType> petTypes = petTypeRepository.findAllById(ids);

		for (PetType petType : petTypes) {
			PetTypeDto petTypeDto = petTypeMapper.toDto(petType);
			petTypeDto = objectPatcher.patchAndValidate(petTypeDto, patchNode);
			petTypeMapper.updateWithNull(petTypeDto, petType);
		}

		List<PetType> resultPetTypes = petTypeRepository.saveAll(petTypes);
		List<Integer> ids1 = resultPetTypes.stream()
			.map(PetType::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public PetTypeDto delete(@PathVariable Integer id) {
		PetType petType = petTypeRepository.findById(id).orElse(null);
		if (petType != null) {
			petTypeRepository.delete(petType);
		}
		return petTypeMapper.toDto(petType);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		petTypeRepository.deleteAllById(ids);
	}
}
