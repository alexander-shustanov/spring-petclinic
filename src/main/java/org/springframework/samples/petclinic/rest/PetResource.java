package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.filter.PetFilter;
import org.springframework.samples.petclinic.rest.mapper.PetMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/pets")
public class PetResource {

	private final PetRepository petRepository;

	private final PetMapper petMapper;

	private final ObjectPatcher objectPatcher;

	public PetResource(PetRepository petRepository,
					   PetMapper petMapper,
					   ObjectPatcher objectPatcher) {
		this.petRepository = petRepository;
		this.petMapper = petMapper;
		this.objectPatcher = objectPatcher;
	}

	@GetMapping
	public Page<PetDto> getList(@ModelAttribute PetFilter filter, Pageable pageable) {
		Specification<Pet> spec = filter.toSpecification();
		Page<Pet> pets = petRepository.findAll(spec, pageable);
		Page<PetDto> petDtoPage = pets.map(petMapper::toDto);
		return petDtoPage;
	}

	@GetMapping("/{id}")
	public PetDto getOne(@PathVariable Integer id) {
		Optional<Pet> petOptional = petRepository.findById(id);
		PetDto petDto = petMapper.toDto(petOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return petDto;
	}

	@GetMapping("/by-ids")
	public List<PetDto> getMany(@RequestParam List<Integer> ids) {
		List<Pet> pets = petRepository.findAllById(ids);
		List<PetDto> petDtos = pets.stream()
			.map(petMapper::toDto)
			.toList();
		return petDtos;
	}

	@PostMapping
	public PetDto create(@RequestBody PetDto dto) {
		Pet pet = petMapper.toEntity(dto);
		Pet resultPet = petRepository.save(pet);
		return petMapper.toDto(resultPet);
	}

	@PatchMapping("/{id}")
	public PetDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		Pet pet = petRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		PetDto petDto = petMapper.toDto(pet);
		petDto = objectPatcher.patchAndValidate(petDto, patchNode);
		petMapper.updateWithNull(petDto, pet);

		Pet resultPet = petRepository.save(pet);
		return petMapper.toDto(resultPet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<Pet> pets = petRepository.findAllById(ids);

		for (Pet pet : pets) {
			PetDto petDto = petMapper.toDto(pet);
			petDto = objectPatcher.patchAndValidate(petDto, patchNode);
			petMapper.updateWithNull(petDto, pet);
		}

		List<Pet> resultPets = petRepository.saveAll(pets);
		List<Integer> ids1 = resultPets.stream()
			.map(Pet::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public PetDto delete(@PathVariable Integer id) {
		Pet pet = petRepository.findById(id).orElse(null);
		if (pet != null) {
			petRepository.delete(pet);
		}
		return petMapper.toDto(pet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		petRepository.deleteAllById(ids);
	}
}
