package com.maurosalani.push_notification;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maurosalani.push_notification.dto.Subscription;
import com.maurosalani.push_notification.dto.SubscriptionEndpoint;

@RestController
public class PushController {

	private final ServerKeys serverKeys;

	private final SubscriptionsHandler subscriptionsHandler;

	public PushController(ServerKeys serverKeys) {
		this.serverKeys = serverKeys;
		subscriptionsHandler = SubscriptionsHandler.getInstance(serverKeys);
		Logger.getLogger(PushController.class.getName()).info("PushController has correctly been created");
	}

	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return this.serverKeys.getPublicKeyUncompressed();
	}

	@GetMapping(path = "/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return this.serverKeys.getPublicKeyBase64();
	}

	@PostMapping("/subscribe")
	@ResponseStatus(HttpStatus.CREATED)
	public void subscribe(@RequestBody Subscription subscription) {
		boolean isSubscribed = subscriptionsHandler.isSubscribed(new SubscriptionEndpoint(subscription.getEndpoint()));
		if (!isSubscribed) {
			Logger.getLogger(PushController.class.getName())
					.info("Username: " + subscription.getUsername() + " subscribed: " + subscription.getEndpoint());
			subscriptionsHandler.subscribeUser(subscription);
		} else {
			Logger.getLogger(PushController.class.getName())
					.info(isSubscribed + " = IsSubscribed: " + subscription.getEndpoint());
		}
	}

	@PostMapping("/unsubscribe")
	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription) {
		Logger.getLogger(PushController.class.getName()).info("Unsubscription: " + subscription.getEndpoint());
		subscriptionsHandler.unsubscribeUser(subscription);
	}

	@PostMapping("/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
		boolean isSubscribed = subscriptionsHandler.isSubscribed(subscription);
		return isSubscribed;
	}

}
