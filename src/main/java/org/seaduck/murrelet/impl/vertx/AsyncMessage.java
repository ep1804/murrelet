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

import org.seaduck.murrelet.BaseAsyncMessage;

public class AsyncMessage extends BaseAsyncMessage {

	public AsyncMessage(String content) {
		super(content.getBytes());
	}

	public AsyncMessage(byte[] bytes) {
		super(bytes);
	}

	public String getContent(){
		return new String(super.getBody());
	}
	
	public void setContent(String content){
		super.setBody(content.getBytes());
	}
	
}
