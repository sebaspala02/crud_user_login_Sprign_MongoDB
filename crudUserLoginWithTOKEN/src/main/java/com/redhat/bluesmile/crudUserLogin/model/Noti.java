package com.redhat.bluesmile.crudUserLogin.model;

public class Noti {

	private int notificationType;

	private String email;

	private String notification;

	public Noti() {
	}

	public Noti(int notificationType, String email, String notification) {
		super();
		this.notificationType = notificationType;
		this.email = email;
		this.notification = notification;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

}
