package certificado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;

public class VisualizadorCertificado {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		try {
			String caminhoDoCertificadoDoCliente = "C:/pcsist/produtos/docfiscal/certificado/1certificado.pfx";
			String senhaDoCertificadoDoCliente = "1234";			
			
			InputStream entrada = new FileInputStream(caminhoDoCertificadoDoCliente);
			KeyStore ks = KeyStore.getInstance("pkcs12");
			try {
				ks.load(entrada, senhaDoCertificadoDoCliente.toCharArray());
			} catch (IOException e) {
				throw new Exception("Senha do Certificado Digital incorreta ou Certificado invlido.");
			}
			
			Provider pp = ks.getProvider();
			info("--------------------------------------------------------");
			info("Provider   : " + pp.getName());
			info("Prov.Vers. : " + pp.getVersion());
			info("KS Type    : " + ks.getType());
			info("KS DefType : " + ks.getDefaultType());
	
			String alias = null;
			Enumeration <String> al = ks.aliases();
			while (al.hasMoreElements()) {
				alias = al.nextElement();
				info("--------------------------------------------------------");
				if (ks.containsAlias(alias)) {
					info("Alias exists : '" + alias + "'");
					
					X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
					
					info("Certificate  : '" + cert.toString() + "'");
					info("Version      : '" + cert.getVersion() + "'");
					info("SerialNumber : '" + cert.getSerialNumber() + "'");
					info("SigAlgName   : '" + cert.getSigAlgName() + "'");
					info("NotBefore    : '" + cert.getNotBefore().toString() + "'");
					info("NotAfter     : '" + cert.getNotAfter().toString() + "'");
					info("TBS          : '" + cert.getTBSCertificate().toString() + "'");
					Certificate[] chain =  ks.getCertificateChain(alias);
					info("Cadeia:");
					for (Certificate chainCert : chain) {
						String alias1 = ((X509Certificate) chainCert).getSubjectX500Principal().getName();
						info(alias1);
					}
					
				} else {
					info("Alias doesn't exists : '" + alias + "'");
				}
			}
		} catch (Exception e) {
			error(e.toString());
		}
	}

	/**
	 * Log ERROR.
	 * 
	 * @param error
	 */
	private static void error(String error) {
		System.out.println("| ERROR: " + error);
	}

	/**
	 * Log INFO.
	 * 
	 * @param info
	 */
	private static void info(String info) {
		System.out.println("| INFO: " + info);
	}
	
}
