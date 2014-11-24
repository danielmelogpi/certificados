package util;
public class Log {
	
	/** Imprime qualquer quantidade de qualquer trem com um toString() */
	public static void out(Object... args) {
		for (Object arg : args) {
	        System.out.print(arg + "\t");
	    }
		System.out.println("");
	}
	
}
