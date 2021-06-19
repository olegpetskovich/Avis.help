package com.pinus.alexdev.avis.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class StatsResponse{
	private long reviews;
	private List<ReviewItem> review;
	private float stars;
	private int conversations;
}