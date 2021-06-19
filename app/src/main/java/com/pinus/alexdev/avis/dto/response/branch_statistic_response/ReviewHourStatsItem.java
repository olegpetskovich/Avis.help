package com.pinus.alexdev.avis.dto.response.branch_statistic_response;

import lombok.Data;

@Data
public class ReviewHourStatsItem {
	private String date;
	private String count;
	private String impression;

	public ReviewHourStatsItem(String date, String count) {
		this.date = date;
		this.count = count;
 	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getImpression() {
		return impression;
	}

	public void setImpression(String impression) {
		this.impression = impression;
	}
}
