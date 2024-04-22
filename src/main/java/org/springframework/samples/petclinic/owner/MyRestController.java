package org.springframework.samples.petclinic.owner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/myRest")
public class MyRestController {

	@GetMapping
	void myRestMethod() {
		// httpSession
		// locale
		// principal
		// zoneId
	}
}

