package com.pinus.alexdev.avis.dto.response;

import com.pinus.alexdev.avis.enums.PromoTypes;

import lombok.Data;

@Data
public class QrCodesItem{
	private String qrUrl;
	private PromoTypes qrType;
	private long id;
	private String humanReadableId;
}
