package org.springframework.samples.petclinic.owner;

import jakarta.persistence.*;
import org.springframework.samples.petclinic.model.BaseEntity;

@Entity
@Table(name = "owner_feedback")
public class OwnerFeedback extends BaseEntity {

	@Column(name = "text")
	private String text;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
