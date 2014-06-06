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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.seaduck.murrelet.BaseAsyncMessage;
import org.seaduck.murrelet.impl.vertx.AsyncHandler;
import org.seaduck.murrelet.impl.vertx.AsyncMessage;
import org.seaduck.murrelet.impl.vertx.AsyncReceiver;
import org.seaduck.murrelet.impl.vertx.AsyncSender;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

public class AsyncTest {

	@Test
	public void testSenderReceiver() throws InterruptedException {
		
		// for fetching data from threads other than main test thread.
		final CountDownLatch latch = new CountDownLatch(1);
		final Map<String,Object> map = new HashMap<String,Object>();
		
		Vertx vertx = VertxFactory.newVertx();
		String content = "{\"content\":\"test message\"}";
		
		AsyncSender sender = new AsyncSender("TEST_BUS", vertx.eventBus());
		
		AsyncReceiver receiver = new AsyncReceiver("TEST_BUS", vertx.eventBus());
		
		receiver.bindHandler(new AsyncHandler(){

			@Override
			public void handle(BaseAsyncMessage message) {
				map.put("ReceivedMessage", new String(message.getBytes()));
				latch.countDown();
			}
			
		});
		
		AsyncMessage msg = new AsyncMessage(content);
		sender.send(msg);
		
		latch.await(10, TimeUnit.SECONDS);
		assertEquals( (String) map.get("ReceivedMessage"), content);
		
		sender.close();
		receiver.close();
	}

}
