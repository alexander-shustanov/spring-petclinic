CREATE TABLE vets_specialties (
  specialties_id INTEGER NOT NULL,
   vet_id INTEGER NOT NULL,
   CONSTRAINT pk_vets_specialties PRIMARY KEY (specialties_id, vet_id)
);

ALTER TABLE vets_specialties ADD CONSTRAINT fk_vetspe_on_specialty FOREIGN KEY (specialties_id) REFERENCES specialties (id);

ALTER TABLE vets_specialties ADD CONSTRAINT fk_vetspe_on_vet FOREIGN KEY (vet_id) REFERENCES vets (id);
