ALTER TABLE visits
    ADD assigned_vet_id INTEGER;

ALTER TABLE visits
    ADD CONSTRAINT FK_VISITS_ON_ASSIGNED_VET FOREIGN KEY (assigned_vet_id) REFERENCES vets (id);