package jp.bs.app.ukiukiview;


import java.util.LinkedList;
import jp.co.brilliantservice.utility.SdLog;

public class BackgroundEventHandler {
	private QueueThread mThread = null;
	private LinkedList<Event> mQueue = null;

	BackgroundEventHandler() {
		SdLog.put("BackgroundEventHandler create");
		mThread = new QueueThread();
		mThread.start();
	}

	public void addEvent(Event event) {
		mThread.addEvent(event);
	}

	public void finish() {
		Event event = new Event(Event.MESSAGE_FINISH, null, null);
		mThread.addEvent(event);
	}

	class QueueThread extends Thread {
		public QueueThread() {
			mQueue = new LinkedList<Event>();
		}

		public void addEvent(Event event) {
			mQueue.offer(event);
			synchronized (this) {
				notify();
			}
		}
		public void run() {
			boolean isFinish = false;
			while(!isFinish) {
				synchronized (this) {
					if (mQueue!=null && mQueue.peek() != null) {
						Event event = mQueue.poll();
						switch (event.mEventId) {
						case Event.MESSAGE_NONE:
							break;
						case Event.MESSAGE_POST_POI_WITH_PARENT:
						case Event.MESSAGE_POST_POI:
							event.mAdapter.startPostData(event.mPoiObject);
							break;
//						case Event.MESSAGE_DELETE:
//							event.mAdapter.deleteObjectAsync(event.);
//							break;
						case Event.MESSAGE_FINISH:
							isFinish = true;
							break;
						default:
							break;
						}
					}
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						isFinish = true;
					}
				}
			}
		}
	}
}