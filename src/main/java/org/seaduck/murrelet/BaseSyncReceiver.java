package org.seaduck.murrelet;

public abstract class BaseSyncReceiver extends BaseBus {

	public BaseSyncReceiver(String busName) {
		super(busName);
	}
	
	public abstract void bindHandler(BaseSyncHandler handler);
	
	public abstract void respond(BaseSyncMessage message);

}
