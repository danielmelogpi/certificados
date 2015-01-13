package certificado;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertWindows {
	
	public static void main(String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("WINDOWS-MY");
		ks.load(null, null);
		Enumeration<String> aliases = ks.aliases();
		while (aliases.hasMoreElements()) {
			String al = aliases.nextElement();
			System.out.println("\n\n\n\n"+al);
			
			X509Certificate cert = (X509Certificate) ks.getCertificate(al);
			
			info("Version      : '" + cert.getVersion() + "'");
			info("SerialNumber : '" + cert.getSerialNumber() + "'");
			info("SigAlgName   : '" + cert.getSigAlgName() + "'");
			info("NotBefore    : '" + cert.getNotBefore().toString() + "'");
			info("NotAfter     : '" + cert.getNotAfter().toString() + "'");
			info("TBS          : '" + cert.getTBSCertificate().toString() + "'");
			Certificate[] chain =  ks.getCertificateChain(al);
			info("Cadeia:");
			for (Certificate chainCert : chain) {
				String alias1 = ((X509Certificate) chainCert).getSubjectX500Principal().getName();
				info(alias1);
			}
			
		}
	}

	private static void info(String string) {
		System.out.println("INFO | " + string);
		
	}
	
}
