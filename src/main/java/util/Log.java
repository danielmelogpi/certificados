package util;

import java.io.PrintStream;

/** Utilitario simples para logar os experimentos sem adicao de 
 * biblioteca externa 
 * 
 * @author Daniel Melo 
 * github.com/danielmelogpi
 *
 */
public class Log {
	
	/** Imprime qualquer quantidade de qualquer trem com um toString() */
	public static void out(String append, Object... args) {
		printToStream(System.out, append, args);
	}
	
	public static synchronized final void printToStream(PrintStream stream, String append, Object[] obj) {
		stream.print(append);
		for (Object arg : obj) {
			stream.print(arg + "");
	    }
		stream.println("");
	}
	
	public static void out(Object obj) {
		out("", obj);
	}
	
	public static void error(Object... args) {
		printToStream(System.out, "ERROR | ", args);
	}
	
	public static void debug(Object... args) {
		out("DEBUG | ", args);
	}
	
	public static void warn(Object... args) {
		out("WARN | ", args);
	}
	
	public static void info(Object... args) {
		out("INFO | ", args);
	}
	
}
