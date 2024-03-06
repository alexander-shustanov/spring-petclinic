ALTER TABLE vets
    ADD salary DECIMAL(19, 2);

ALTER TABLE vet_specialties
    ADD CONSTRAINT pk_vet_specialties PRIMARY KEY (specialty_id, vet_id);

ALTER TABLE pets
    ALTER COLUMN type_id DROP NOT NULL;