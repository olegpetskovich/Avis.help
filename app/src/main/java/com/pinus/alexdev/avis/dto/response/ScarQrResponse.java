package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class ScarQrResponse{
	public enum Message { NEW, IN_PROGRESS, CLOSED}
	private boolean success;
	private Message message;
}
