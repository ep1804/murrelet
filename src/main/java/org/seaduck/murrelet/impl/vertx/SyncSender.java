/**
 * Copyright 2014 https:/github.com/seaduck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.seaduck.murrelet.impl.vertx;

import java.util.UUID;

import org.seaduck.murrelet.BaseSyncHandler;
import org.seaduck.murrelet.BaseSyncMessage;
import org.seaduck.murrelet.BaseSyncSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.eventbus.EventBus;

public class SyncSender extends BaseSyncSender {
	
	private Logger logger;
	private EventBus eventBus;
	private UUID correlationId;
	private VertxSyncMessageHandler responseHandler;
	
	public SyncSender(String busName, EventBus eventBus) {
		super(busName);
		
		this.logger = LoggerFactory.getLogger(AsyncSender.class);
		this.eventBus = eventBus;
		
		this.logger.info("Bus established with name: " + super.getBusName());

	}

	@Override
	public void bindResponseHandler(BaseSyncHandler handler) {
		this.correlationId = UUID.randomUUID();
		this.responseHandler = new VertxSyncMessageHandler(handler);
		this.eventBus.registerHandler(this.correlationId.toString(), this.responseHandler);
		
		this.logger.info("Message response handler is bound to the bus: " + super.getBusName());		
	}

	@Override
	public void send(BaseSyncMessage message) {
		message.setCorrelatoinId(this.correlationId);
		this.eventBus.send(super.getBusName(), message.getBytes());
		
		//TODO message sending should cover correlation ID
		
		System.out.println("message.corr: " + message.getCorrelationId().toString());
		
	}

	@Override
	public void close() {
		this.eventBus.unregisterHandler(super.getBusName(), this.responseHandler);
		
		this.logger.info("Message response handler is removed from the bus: " + super.getBusName());
	}
}
