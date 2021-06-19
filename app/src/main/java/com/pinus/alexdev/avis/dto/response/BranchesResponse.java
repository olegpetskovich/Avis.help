package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class BranchesResponse{
	private String address;
	private String contact;
	private String name;
	private String logoUrl;
	private int id;
	private String phone;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean clicked) {
		isClicked = clicked;
	}

	public int getItemPosition() {
		return itemPosition;
	}

	public void setItemPosition(int itemPosition) {
		this.itemPosition = itemPosition;
	}

	//поля для RecyclerView
	private boolean isClicked;
	private int itemPosition;
}