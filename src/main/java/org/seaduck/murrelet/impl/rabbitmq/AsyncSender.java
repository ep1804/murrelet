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

package org.seaduck.murrelet.impl.rabbitmq;

import java.io.IOException;

import org.seaduck.murrelet.BaseAsyncMessage;
import org.seaduck.murrelet.BaseAsyncSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AsyncSender extends BaseAsyncSender {

	private Logger logger;
	private Connection connection;
	private Channel channel;

	public AsyncSender(String busName, String host) {
		super(busName);
		
		this.logger = LoggerFactory.getLogger(AsyncSender.class);
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();			
		} catch (IOException e) {
			this.logger.error("Error in opening connection.");
			e.printStackTrace();
		}
		
		this.logger.info("Bus established: " + super.getBusName());
	}

	@Override
	public void send(BaseAsyncMessage message) {
		try {
			channel.basicPublish("", super.getBusName(), null, message.getBody());
		} catch (IOException e) {
			this.logger.error("Error in sending message.");
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			this.channel.close();
			this.connection.close();
		} catch (IOException e) {
			this.logger.error("Error in closing connection.");
			e.printStackTrace();
		}
		
		this.logger.info("Bus closed: " + super.getBusName());
	}

}
