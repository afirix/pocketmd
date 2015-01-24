package org.coursera.android.capstone.client;

import org.coursera.android.capstone.client.service.PocketMdServiceApi;

import android.app.Application;

public class PocketMdClientApplication extends Application {

	private PocketMdServiceApi pocketMdService;

	public PocketMdServiceApi getPocketMdService() {
		return pocketMdService;
	}

	public void setPocketMdService(final PocketMdServiceApi pocketMdService) {
		this.pocketMdService = pocketMdService;
	}
}
