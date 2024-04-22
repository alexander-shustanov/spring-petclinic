package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyOwnerService {
	List<Owner> findOwnersFromCity(String cityName) {
		// 1. Type `ownerRepository`
		// 2. Complete injecting the repo
		// 3. Type `findAllByCityName` and complete

		// Result:
		// return ownerRepository.findAllByCity(cityName);
	}

	List<OwnerFeedback> findFeedbacks(Owner owner) {
		// 1. Type `ownerFeedbackRepository`
		// 2. Complete creating and injecting new repo
		// 3. Go into this repository and declare a new method `findAllByOwner`
		// 4. Go back and proceed the statement with calling the method that just been created.

		// Result:
		// return ownerFeedbackRepository.findAllByOwner(owner);
	}
}
