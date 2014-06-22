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

package org.seaduck.murrelet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.seaduck.murrelet.BaseAsyncMessage;

public class BaseAsyncMessageTest {

	@Test
	public void test() {
		String content = "{\"content\":\"test message\"}";		
		StringMessage msg = new StringMessage(content);
		assertEquals(content, msg.getString());
	}

	class StringMessage extends BaseAsyncMessage {
		public StringMessage(String content) {
			super(content.getBytes());
		}

		public String getString(){
			return new String(super.getBody());
		}
		
		public void setString(String content){
			super.setBody(content.getBytes());
		}
	}
}
