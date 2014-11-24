package certificadoTest;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.junit.Test;

import util.Log;
import certificado.OpenCert;

public class AbrirCertificados  {

	OpenCert openCert = new OpenCert();

	@Test
	public void abreCertificados() {
		KeyStore funcional = openCert.getKeystoreFuncional();
		imprimeCadeiasCertificadoras(funcional);
		KeyStore problema = openCert.getKeystoreProblema();
		imprimeCadeiasCertificadoras(problema);
		KeyStore exportado = openCert.getKeystoreProblemaExportado();
		imprimeCadeiasCertificadoras(exportado);
	}
	
	@Test
	public void abreCertificadoProblema(){
		KeyStore funcional = openCert.getKeystoreFuncional();
		imprimeCadeiasCertificadoras(funcional);
	}
	
	@Test
	public void abreCertificadoFuncional(){
		KeyStore problema = openCert.getKeystoreProblema();
		imprimeCadeiasCertificadoras(problema);
	}
	@Test
	public void abreCertificadoProblemaExportado(){
		KeyStore exportado = openCert.getKeystoreProblemaExportado();
		imprimeCadeiasCertificadoras(exportado);
	}
	
	private void imprimeCertificado(Certificate certificate) {
		Log.out("Imprimindo chave pública do certificado");
		Log.out(certificate.getPublicKey());
	}
	
	private void imprimeCadeiasCertificadoras(KeyStore keystore) {
		
		try {
			Log.out("DUMP da cadeia certificadora do certificado ");
			Enumeration<String> aliases = keystore.aliases();
			
			int cont = 1;
			while(aliases.hasMoreElements()){
				String nextAlias = aliases.nextElement();
				
				//imprimeCertificado(keystore.getCertificate(nextAlias));
				
				Certificate[] certs = keystore.getCertificateChain(nextAlias);
				for (Certificate cert : certs) {
					Log.out("\n",cont + "* item do certificado","\n");
					cont++;
					Log.out("[ Certificado da cadeia certificadora: ]",cert.toString());
				}
			}
		}
		catch (Exception e){
			Log.out(e);
		}
		
	}

	

}
