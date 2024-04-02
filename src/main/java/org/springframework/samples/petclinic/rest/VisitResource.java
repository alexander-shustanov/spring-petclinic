package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.amplicode.rautils.patch.ObjectPatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.filter.VisitFilter;
import org.springframework.samples.petclinic.rest.mapper.VisitMapper;
import org.springframework.samples.petclinic.vet.SpecialtyRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/visits")
public class VisitResource {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final ObjectPatcher objectPatcher;

	private final PetRepository petRepository;

	private final VetRepository vetRepository;

	private final VisitService visitService;

	private final SpecialtyRepository specialtyRepository;

	public VisitResource(VisitRepository visitRepository,
						 VisitMapper visitMapper,
						 ObjectPatcher objectPatcher,
						 PetRepository petRepository,
						 VetRepository vetRepository,
						 VisitService visitService,
						 SpecialtyRepository specialtyRepository) {
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
		this.objectPatcher = objectPatcher;
		this.petRepository = petRepository;
		this.vetRepository = vetRepository;
		this.visitService = visitService;
		this.specialtyRepository = specialtyRepository;
	}

	@GetMapping
	public Page<VisitDto> getList(@ModelAttribute VisitFilter filter, Pageable pageable) {
		Specification<Visit> spec = filter.toSpecification();
		Page<Visit> visits = visitRepository.findAll(spec, pageable);
		Page<VisitDto> visitDtoPage = visits.map(visitMapper::toDto);
		return visitDtoPage;
	}

	@GetMapping("/{id}")
	public VisitDto getOne(@PathVariable Integer id) {
		Optional<Visit> visitOptional = visitRepository.findById(id);
		VisitDto visitDto = visitMapper.toDto(visitOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
		return visitDto;
	}

	@GetMapping("/by-ids")
	public List<VisitDto> getMany(@RequestParam List<Integer> ids) {
		List<Visit> visits = visitRepository.findAllById(ids);
		List<VisitDto> visitDtos = visits.stream()
			.map(visitMapper::toDto)
			.toList();
		return visitDtos;
	}

	@PatchMapping("/{id}")
	public VisitDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) {
		Visit visit = visitRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VisitDto visitDto = visitMapper.toDto(visit);
		visitDto = objectPatcher.patchAndValidate(visitDto, patchNode);
		visitMapper.updateWithNull(visitDto, visit);

		Visit resultVisit = visitRepository.save(visit);
		return visitMapper.toDto(resultVisit);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) {
		Collection<Visit> visits = visitRepository.findAllById(ids);

		for (Visit visit : visits) {
			VisitDto visitDto = visitMapper.toDto(visit);
			visitDto = objectPatcher.patchAndValidate(visitDto, patchNode);
			visitMapper.updateWithNull(visitDto, visit);
		}

		List<Visit> resultVisits = visitRepository.saveAll(visits);
		List<Integer> ids1 = resultVisits.stream()
			.map(Visit::getId)
			.toList();
		return ids1;
	}

	@DeleteMapping("/{id}")
	public VisitDto delete(@PathVariable Integer id) {
		Visit visit = visitRepository.findById(id).orElse(null);
		if (visit != null) {
			visitRepository.delete(visit);
		}
		return visitMapper.toDto(visit);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		visitRepository.deleteAllById(ids);
	}

	@PostMapping("/create")
	public VisitDto requestVisit(@RequestBody RequestVisitRequest request) {
		specialtyRepository.findById(request.specialtyId())
			.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

		Pet pet = petRepository.findById(request.petId())
			.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Pet not found"));

		Vet vet = vetRepository.findBySpecialties_IdOrderByIdAsc(request.specialtyId())
			.stream()
			.filter(v -> visitService.isDateSlotAvailable(v, request.date()))
			.findAny()
			.orElseThrow(() -> new HttpClientErrorException(HttpStatus.CONFLICT, "Unable to find vet with such specialty"));

		Visit createdVisit = visitService.createVisit(pet, vet, request.date(), request.description());
		return visitMapper.toDto(createdVisit);
	}

	public record RequestVisitRequest(LocalDate date, Integer petId, Integer specialtyId, String description) {
	}
}
