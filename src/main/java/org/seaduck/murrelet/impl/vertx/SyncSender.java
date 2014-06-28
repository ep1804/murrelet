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

import org.seaduck.murrelet.BaseSyncHandler;
import org.seaduck.murrelet.BaseSyncMessage;
import org.seaduck.murrelet.BaseSyncSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.eventbus.EventBus;

public class SyncSender extends BaseSyncSender {
	
	private Logger logger;
	private EventBus eventBus;
	private SyncVertxHandler responseHandler;
	
	public SyncSender(String busName, EventBus eventBus) {
		super(busName);
		
		this.logger = LoggerFactory.getLogger(AsyncSender.class);
		this.eventBus = eventBus;
		
		this.logger.info("Bus established: " + super.getBusName());
	}

	@Override
	public void bindResponseHandler(BaseSyncHandler handler) {
		this.responseHandler = new SyncVertxHandler(handler);
		
		this.logger.info("Message response handler is created: " + super.getBusName());		
	}

	@Override
	public void send(BaseSyncMessage message) {
		this.eventBus.send(super.getBusName(), message.getBody(), this.responseHandler);
	}

	@Override
	public void close() {
	}
}
