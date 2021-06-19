package com.pinus.alexdev.avis.dto.request;

import lombok.Data;
import okhttp3.MultipartBody;

@Data
public class BranchRequest{
	private String address;
	private String phone;
	private String contact;
	private String name;

	public BranchRequest(String address, String contact, String name, String phone) {
		this.address = address;
		this.contact = contact;
		this.name = name;
		this.phone = phone;
	}
}
