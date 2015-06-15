package com.alcatel.smartlinkv3.ftp.client;

import java.util.LinkedList;

public class ListQueue {
	private LinkedList<Object> list;

	public ListQueue() {
		list = new LinkedList<Object>();
	}
	
	public synchronized void clear() {
		list.clear();
	}

	public synchronized boolean QueueEmpty() {
		return list.isEmpty();
	}

	public synchronized void addQueue(Object obj) {
		list.addLast(obj);
	}

	public synchronized Object getQueue() {
		if (!list.isEmpty()) {
			return list.removeFirst();
		}
		return null;
	}

	public synchronized int QueueLength() {
		return list.size();
	}

	public synchronized Object QueuePeek() {
		return list.getFirst();
	}
}
