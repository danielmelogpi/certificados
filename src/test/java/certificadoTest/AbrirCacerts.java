package certificadoTest;

import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import util.Log;
import certificado.OpenTrustManager;

public class AbrirCacerts {
	
	OpenTrustManager openTrustManager = new OpenTrustManager();
	
	@Test
	public void abrirCacertsDocfiscal() {
		X509TrustManager trust = openTrustManager.getX509TrustManagerKeyStoreDocfiscal();
		Log.out(trust.getAcceptedIssuers().toString());
	}
	
	@Test
	public void abrirCacertsGerado() {
		X509TrustManager trust = openTrustManager.getX509TrustManagerKeyStoreGerado();
		Log.out(trust.getAcceptedIssuers().toString());
	}
	
}
