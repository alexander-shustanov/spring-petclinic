package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.filter.VetFilter;
import org.springframework.samples.petclinic.rest.mapper.VetMapper;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/vets")
public class VetResource {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	private final ObjectPatcher objectPatcher;

	public VetResource(VetRepository vetRepository,
					   VetMapper vetMapper,
					   ObjectPatcher objectPatcher) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
		this.objectPatcher = objectPatcher;
	}

	@GetMapping
	public Page<VetDto> getList(@ModelAttribute VetFilter filter, Pageable pageable) {
		Specification<Vet> spec = filter.toSpecification();
		Page<Vet> vets = vetRepository.findAll(spec, pageable);
		Page<VetDto> vetDtoPage = vets.map(vetMapper::toDto);
		return vetDtoPage;
	}

	@GetMapping("/{id}")
	public VetDto getOne(@PathVariable Integer id) {
		Optional<Vet> vetOptional = vetRepository.findById(id);
		VetDto vetDto = vetMapper.toDto(vetOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return vetDto;
	}

	@GetMapping("/by-ids")
	public List<VetDto> getMany(@RequestParam List<Integer> ids) {
		List<Vet> vets = vetRepository.findAllById(ids);
		List<VetDto> vetDtos = vets.stream()
			.map(vetMapper::toDto)
			.toList();
		return vetDtos;
	}

	@PostMapping
	public VetDto create(@RequestBody @Valid VetDto dto) {
		Vet vet = vetMapper.toEntity(dto);
		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toDto(resultVet);
	}

	@PatchMapping("/{id}")
	public VetDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		Vet vet = vetRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VetDto vetDto = vetMapper.toDto(vet);
		vetDto = objectPatcher.patchAndValidate(vetDto, patchNode);
		vetMapper.updateWithNull(vetDto, vet);

		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toDto(resultVet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<Vet> vets = vetRepository.findAllById(ids);

		for (Vet vet : vets) {
			VetDto vetDto = vetMapper.toDto(vet);
			vetDto = objectPatcher.patchAndValidate(vetDto, patchNode);
			vetMapper.updateWithNull(vetDto, vet);
		}

		List<Vet> resultVets = vetRepository.saveAll(vets);
		List<Integer> ids1 = resultVets.stream()
			.map(Vet::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public VetDto delete(@PathVariable Integer id) {
		Vet vet = vetRepository.findById(id).orElse(null);
		if (vet != null) {
			vetRepository.delete(vet);
		}
		return vetMapper.toDto(vet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		vetRepository.deleteAllById(ids);
	}
}
