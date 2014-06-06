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

import org.seaduck.murrelet.BaseAsyncHandler;
import org.seaduck.murrelet.BaseAsyncReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.eventbus.EventBus;

public class AsyncReceiver extends BaseAsyncReceiver {

	private Logger logger;
	private EventBus eventBus;
	private VertxMessageHandler handler;
	
	public AsyncReceiver(String busName, EventBus eventBus) {
		super(busName);
		
		this.logger = LoggerFactory.getLogger(AsyncSender.class);
		this.eventBus = eventBus;
		
		this.logger.info("Bus established with name: " + super.getBusName());
	}

	@Override
	public void bindHandler(BaseAsyncHandler handler) {
		this.handler = new VertxMessageHandler(handler);
		this.eventBus.registerHandler(super.getBusName(), this.handler);
		
		this.logger.info("Message handler is bound to the bus: " + super.getBusName());
	}

	@Override
	public void close() {
		this.eventBus.unregisterHandler(super.getBusName(), this.handler);
		
		this.logger.info("Message handler is removed from the bus: " + super.getBusName());
	}
	
}
