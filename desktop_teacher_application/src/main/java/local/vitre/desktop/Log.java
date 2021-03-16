package local.vitre.desktop;

import java.util.logging.Logger;

public class Log {

	private static Logger myLogger = Logger.getLogger("DTA");
	public static boolean debug = true;

	public static void fine(String msg) {
		myLogger.fine(msg);
	}

	public static void fine(String tag, String msg) {
		myLogger.fine(format(tag) + msg);
	}

	public static void newLine() {
		System.out.println();
	}

	public static void info(String msg) {
		myLogger.info(msg);
	}

	public static void info(String tag, String msg) {
		myLogger.info(format(tag) + msg);
	}

	private static String format(String tag) {
		return "[" + tag + "]: ";
	}

	public static void severe(String tag, String msg) {
		myLogger.severe(format(tag) + msg);
	}

	public static void warn(String msg) {
		myLogger.warning(msg);
	}

	public static Logger getLogger() {
		return myLogger;
	}

}
