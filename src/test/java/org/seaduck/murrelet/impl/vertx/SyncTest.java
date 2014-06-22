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
import org.seaduck.murrelet.BaseSyncMessage;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

public class SyncTest {

	@Test
	public void testSenderReceiver() throws InterruptedException{
		// for fetching data from threads other than main test thread.
		final CountDownLatch latch = new CountDownLatch(2);
		final Map<String,Object> map = new HashMap<String,Object>();		
		
		Vertx vertx = VertxFactory.newVertx();
		
		SyncSender sender = new SyncSender("TEST_DUPLEX_BUS", vertx.eventBus());
				
		sender.bindResponseHandler(new SyncHandler(){

			@Override
			public void handle(BaseSyncMessage message) {
				
				map.put("Response", new String(message.getBody()));
				latch.countDown();				
			}
			
		});
		
		final SyncReceiver receiver = new SyncReceiver("TEST_DUPLEX_BUS", vertx.eventBus());
		
		receiver.bindHandler(new SyncHandler(){

			@Override
			public void handle(BaseSyncMessage message) {

				map.put("ReceivedMessage", new String(message.getBody()));				
				latch.countDown();
				
				SyncMessage msg = new SyncMessage("{\"content\":\"well done\"}");
				msg.setCorrelatoinId(message.getCorrelationId());				
				receiver.respond(msg);
				
				/* Following code also works:
				
				((SyncMessage)message).setContent("{\"content\":\"well done\"}");
				receiver.respond(message);
				
				*/
			}
			
		});
		
		SyncMessage msg = new SyncMessage("{\"content\":\"test message\"}");
		sender.send(msg);		
		
		latch.await(10, TimeUnit.SECONDS);
		assertEquals( (String) map.get("ReceivedMessage"), "{\"content\":\"test message\"}");
		assertEquals( (String) map.get("Response"), "{\"content\":\"well done\"}");
		
		sender.close();
		receiver.close();		
	}

}
