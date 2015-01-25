package util;
public class Log {
	
	/** Imprime qualquer quantidade de qualquer trem com um toString() */
	public static void out(String append, Object... args) {
		for (Object arg : args) {
	        System.out.print(append + arg + "\t");
	    }
		System.out.println("");
	}
	
	public static void error(Object... args) {
		out("ERROR | ", args);
	}
	
	public static void info(Object... args) {
		out("INFO | ", args);
	}
	
}
