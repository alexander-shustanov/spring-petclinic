package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.SpecialtyRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/specialties")
public class SpecialtyResource {

	private final SpecialtyRepository specialtyRepository;

	private final SpecialtyMapper specialtyMapper;

	private final ObjectPatcher objectPatcher;

	public SpecialtyResource(SpecialtyRepository specialtyRepository,
							 SpecialtyMapper specialtyMapper,
							 ObjectPatcher objectPatcher) {
		this.specialtyRepository = specialtyRepository;
		this.specialtyMapper = specialtyMapper;
		this.objectPatcher = objectPatcher;
	}

	@GetMapping
	public Page<SpecialtyDto> getList(Pageable pageable) {
		Page<Specialty> specialties = specialtyRepository.findAll(pageable);
		Page<SpecialtyDto> specialtyDtoPage = specialties.map(specialtyMapper::toDto);
		return specialtyDtoPage;
	}

	@GetMapping("/{id}")
	public SpecialtyDto getOne(@PathVariable Integer id) {
		Optional<Specialty> specialtyOptional = specialtyRepository.findById(id);
		SpecialtyDto specialtyDto = specialtyMapper.toDto(specialtyOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return specialtyDto;
	}

	@GetMapping("/by-ids")
	public List<SpecialtyDto> getMany(@RequestParam List<Integer> ids) {
		List<Specialty> specialties = specialtyRepository.findAllById(ids);
		List<SpecialtyDto> specialtyDtos = specialties.stream()
			.map(specialtyMapper::toDto)
			.toList();
		return specialtyDtos;
	}

	@PostMapping
	public SpecialtyDto create(@RequestBody SpecialtyDto dto) {
		Specialty specialty = specialtyMapper.toEntity(dto);
		Specialty resultSpecialty = specialtyRepository.save(specialty);
		return specialtyMapper.toDto(resultSpecialty);
	}

	@PatchMapping("/{id}")
	public SpecialtyDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		Specialty specialty = specialtyRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		SpecialtyDto specialtyDto = specialtyMapper.toDto(specialty);
		specialtyDto = objectPatcher.patchAndValidate(specialtyDto, patchNode);
		specialtyMapper.updateWithNull(specialtyDto, specialty);

		Specialty resultSpecialty = specialtyRepository.save(specialty);
		return specialtyMapper.toDto(resultSpecialty);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<Specialty> specialties = specialtyRepository.findAllById(ids);

		for (Specialty specialty : specialties) {
			SpecialtyDto specialtyDto = specialtyMapper.toDto(specialty);
			specialtyDto = objectPatcher.patchAndValidate(specialtyDto, patchNode);
			specialtyMapper.updateWithNull(specialtyDto, specialty);
		}

		List<Specialty> resultSpecialties = specialtyRepository.saveAll(specialties);
		List<Integer> ids1 = resultSpecialties.stream()
			.map(Specialty::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public SpecialtyDto delete(@PathVariable Integer id) {
		Specialty specialty = specialtyRepository.findById(id).orElse(null);
		if (specialty != null) {
			specialtyRepository.delete(specialty);
		}
		return specialtyMapper.toDto(specialty);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		specialtyRepository.deleteAllById(ids);
	}
}
