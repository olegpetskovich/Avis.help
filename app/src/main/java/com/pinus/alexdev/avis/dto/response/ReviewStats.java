package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class ReviewStats{
	private int newAuthReviewCount;
	private int newConversationCount;
	private int newNotAuthReviewCount;

	public int getNewAuthReviewCount() {
		return newAuthReviewCount;
	}

	public void setNewAuthReviewCount(int newAuthReviewCount) {
		this.newAuthReviewCount = newAuthReviewCount;
	}

	public int getNewConversationCount() {
		return newConversationCount;
	}

	public void setNewConversationCount(int newConversationCount) {
		this.newConversationCount = newConversationCount;
	}

	public int getNewNotAuthReviewCount() {
		return newNotAuthReviewCount;
	}

	public void setNewNotAuthReviewCount(int newNotAuthReviewCount) {
		this.newNotAuthReviewCount = newNotAuthReviewCount;
	}
}
