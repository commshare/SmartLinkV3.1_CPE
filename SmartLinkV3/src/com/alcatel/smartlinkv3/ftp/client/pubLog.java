package com.alcatel.smartlinkv3.ftp.client;

import android.util.Log;

public class pubLog {
	/**
	 * log tag
	 */
	private String tag = "Module name";// application name
	/**
	 * debug or not
	 */
	private static boolean debug = true;

	private static pubLog instance = new pubLog();

	public pubLog() {

	}

	public static void setDebug(boolean isOpen) {
		debug = isOpen;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public static pubLog getLogger() {
		return instance;
	}

	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();

		if (sts == null) {
			return null;
		}

		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}

			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}

			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}

			return "[" + Thread.currentThread().getName() + "("
					+ Thread.currentThread().getId() + "): " + st.getFileName()
					+ ":" + st.getLineNumber() + "]";
		}

		return null;
	}

	private String createMessage(String msg) {
		String functionName = getFunctionName();
		String message = (functionName == null ? msg
				: (functionName + " - " + msg));
		return message;
	}

	/**
	 * log.i
	 */
	public void i(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.i(tag, message);
		}
	}

	/**
	 * log.v
	 */
	public void v(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.v(tag, message);
		}
	}

	/**
	 * log.d
	 */
	public void d(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.d(tag, message);
		}
	}

	/**
	 * log.e
	 */
	public void e(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.e(tag, message);
		}
	}

	/**
	 * log.error
	 */
	public void error(Exception e) {
		if (debug) {
			StringBuffer sb = new StringBuffer();
			String name = getFunctionName();
			StackTraceElement[] sts = e.getStackTrace();

			if (name != null) {
				sb.append(name + " - " + e + "\r\n");
			} else {
				sb.append(e + "\r\n");
			}
			if (sts != null && sts.length > 0) {
				for (StackTraceElement st : sts) {
					if (st != null) {
						sb.append("[ " + st.getFileName() + ":"
								+ st.getLineNumber() + " ]\r\n");
					}
				}
			}
			Log.e(tag, sb.toString());
		}
	}

	/**
	 * log.d
	 */
	public void w(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.w(tag, message);
		}
	}

}