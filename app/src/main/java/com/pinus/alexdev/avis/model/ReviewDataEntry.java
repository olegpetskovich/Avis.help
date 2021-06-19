package com.pinus.alexdev.avis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

//@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewDataEntry {
	private String value;
	private int y;
	private long id;

	public ReviewDataEntry(String value, int y) {
		this.value = value;
		this.y = y;
 	}
}
