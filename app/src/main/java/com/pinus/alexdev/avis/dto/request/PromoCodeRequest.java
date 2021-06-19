package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class PromoCodeRequest{
	private String iconId;
	private int timesUsedMax;
	private int daysToExpire;
	private String name;
	private String description;

	public PromoCodeRequest(String iconId, int timesUsedMax, int daysToExpire, String name, String description) {
		this.iconId = iconId;
		this.timesUsedMax = timesUsedMax;
		this.daysToExpire = daysToExpire;
		this.name = name;
		this.description = description;
	}
}

