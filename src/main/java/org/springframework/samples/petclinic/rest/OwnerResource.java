package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.filter.OwnerFilter;
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/owners")
public class OwnerResource {

	private final OwnerRepository ownerRepository;

	private final OwnerMapper ownerMapper;

	private final ObjectPatcher objectPatcher;

	public OwnerResource(OwnerRepository ownerRepository,
						 OwnerMapper ownerMapper,
						 ObjectPatcher objectPatcher) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
		this.objectPatcher = objectPatcher;
	}

	@GetMapping
	public Page<OwnerDto> getList(@ModelAttribute OwnerFilter filter, Pageable pageable) {
		Specification<Owner> spec = filter.toSpecification();
		Page<Owner> owners = ownerRepository.findAll(spec, pageable);
		Page<OwnerDto> ownerDtoPage = owners.map(ownerMapper::toDto);
		return ownerDtoPage;
	}

	@GetMapping("/{id}")
	public OwnerDto getOne(@PathVariable Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);
		OwnerDto ownerDto = ownerMapper.toDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return ownerDto;
	}

	@GetMapping("/by-ids")
	public List<OwnerDto> getMany(@RequestParam List<Integer> ids) {
		List<Owner> owners = ownerRepository.findAllById(ids);
		List<OwnerDto> ownerDtos = owners.stream()
			.map(ownerMapper::toDto)
			.toList();
		return ownerDtos;
	}

	@PostMapping
	public OwnerDto create(@RequestBody @Valid OwnerDto dto) {
		Owner owner = ownerMapper.toEntity(dto);
		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toDto(resultOwner);
	}

	@PatchMapping("/{id}")
	public OwnerDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		Owner owner = ownerRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		OwnerDto ownerDto = ownerMapper.toDto(owner);
		ownerDto = objectPatcher.patchAndValidate(ownerDto, patchNode);
		ownerMapper.updateWithNull(ownerDto, owner);

		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toDto(resultOwner);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<Owner> owners = ownerRepository.findAllById(ids);

		for (Owner owner : owners) {
			OwnerDto ownerDto = ownerMapper.toDto(owner);
			ownerDto = objectPatcher.patchAndValidate(ownerDto, patchNode);
			ownerMapper.updateWithNull(ownerDto, owner);
		}

		List<Owner> resultOwners = ownerRepository.saveAll(owners);
		List<Integer> ids1 = resultOwners.stream()
			.map(Owner::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public OwnerDto delete(@PathVariable Integer id) {
		Owner owner = ownerRepository.findById(id).orElse(null);
		if (owner != null) {
			ownerRepository.delete(owner);
		}
		return ownerMapper.toDto(owner);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		ownerRepository.deleteAllById(ids);
	}
}
