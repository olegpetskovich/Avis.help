package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class ReviewItem{
	private String lastSendTime;
	private long dateViewed;
	private String executionStatus;
	private String message;
	private boolean isViewed;
	private long dateCreated;
	private String commentBy;
	private String phone;
	private String commentDate;
	private boolean isAnswered;
	private boolean isChatViewed;
	private int impression;
	private String qrType;
	private String comment;
	private long id;


}
