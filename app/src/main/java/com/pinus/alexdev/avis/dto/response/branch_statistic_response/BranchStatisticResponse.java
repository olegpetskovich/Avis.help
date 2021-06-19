package com.pinus.alexdev.avis.dto.response.branch_statistic_response;

import com.pinus.alexdev.avis.dto.response.NPSResponse;

import java.util.List;

import lombok.Data;

@Data
public class BranchStatisticResponse {
	private Review review;
	private NPSResponse nps;
	private String numberOfBranchChats;

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public NPSResponse getNps() {
		return nps;
	}

	public void setNps(NPSResponse nps) {
		this.nps = nps;
	}

	public String getNumberOfBranchChats() {
		return numberOfBranchChats;
	}

	public void setNumberOfBranchChats(String numberOfBranchChats) {
		this.numberOfBranchChats = numberOfBranchChats;
	}
}