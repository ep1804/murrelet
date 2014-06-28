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

import org.seaduck.murrelet.BaseAsyncHandler;
import org.seaduck.murrelet.BaseAsyncReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class AsyncReceiver extends BaseAsyncReceiver {
	
	private Logger logger;
	private Connection connection;
	private Channel channel;
	private HandlerThread handling;
	
	
	public AsyncReceiver(String busName, String host) {
		super(busName);
		
		this.logger = LoggerFactory.getLogger(AsyncReceiver.class);
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			channel.queueDeclare(busName, false, false, false, null);
		} catch (IOException e) {
			this.logger.error("Error in opening connection.");
			e.printStackTrace();
		}
		
		this.logger.info("Bus established: " + super.getBusName());
	}

	@Override
	public void bindHandler(BaseAsyncHandler handler) {
		try {
			channel.queueDeclare(super.getBusName(), false, false, false, null);
		} catch (IOException e) {
			this.logger.error("Error in binding handler.");
			e.printStackTrace();
		}
		
		this.handling = new HandlerThread(super.getBusName(), (AsyncHandler)handler);
		this.handling.start();		
	}

	@Override
	public void close() {
		try {
			this.handling.interrupt();
			this.channel.close();
			this.connection.close();
		} catch (IOException e) {
			this.logger.error("Error in closing connection.");
			e.printStackTrace();
		}
		
		this.logger.info("Bus closed: " + super.getBusName());
	}
	
	class HandlerThread extends Thread {
		
		private String busName;
		private AsyncHandler handler;

		HandlerThread(String busName, AsyncHandler handler){
			this.busName = busName;
			this.handler = handler;
		}
		
		public void run(){						
			QueueingConsumer consumer = new QueueingConsumer(channel);
			
			logger.info("Start waiting for delivery from bus: " + busName);
			
			try {
				channel.basicConsume(this.busName, true, consumer);
			} catch (IOException e) {
				logger.error("Error in setting queue-consumer.");
				e.printStackTrace();
			}
			
			while(true){
				QueueingConsumer.Delivery delivery;
				try {
					delivery = consumer.nextDelivery();
					AsyncMessage message = new AsyncMessage(delivery.getBody());
					this.handler.handle(message);
				} catch (ShutdownSignalException e) {
					logger.error("Error in delivery from queue.");
					e.printStackTrace();
				} catch (ConsumerCancelledException e) {
					logger.error("Error in delivery from queue.");
					e.printStackTrace();
				} catch (InterruptedException e) {
					logger.info("Stop waiting for delivery from bus: " + busName);
					break;
				}				
			}			
		}		
	}
}
