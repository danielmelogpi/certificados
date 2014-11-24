package conexaoTest;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;



import org.junit.Assert;
import org.junit.Test;
import org.springframework.xml.transform.StringResult;

import util.Log;
import certificado.OpenCert;
import certificado.OpenTrustManager;
import conexao.ConsStatServ;
import conexao.StatusServicoSefaz;

public class OlaSefaz {
	
	private OpenCert openCert = new OpenCert();
	private OpenTrustManager openTrust = new OpenTrustManager();

	public List<KeyManager[]> parametrosKeyManager() {
		
		List<KeyManager[]> lista =  new ArrayList<>();
		
		lista.add(openCert.getKeyManagerFuncional());
		lista.add(openCert.getKeyManagerProblema());
		lista.add(openCert.getKeyManagerProblemaExportado());
		
		return lista;
	}
	
	public List<X509TrustManager> parametrosTrustManager() {
		
		List<X509TrustManager> lista =  new ArrayList<>();
		
		lista.add(openTrust.getX509TrustManagerKeyStoreDocfiscal());
		lista.add(openTrust.getX509TrustManagerKeyStoreGerado());
		return lista;
	}
	
	
	
	public StringResult consultaStatusSefaz(KeyManager[] keyManager, X509TrustManager trustManager) {
		StatusServicoSefaz sefaz = new StatusServicoSefaz();
		ConsStatServ cons = new ConsStatServ();
		try {
			Log.out("Falando com " + cons.uri);
			return sefaz.execute(cons.uri, cons.getRequestData(), keyManager, trustManager);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean conversarSefaz(KeyManager[] keyManager, X509TrustManager trustManager) {
			
		StringResult resp = null;
		try {
			resp = consultaStatusSefaz(keyManager, trustManager);
		} catch (Exception e) {
			Log.out(e.getLocalizedMessage(),"Que triste :(" );
		}
		
		if (resp != null) {
			Log.out("Comunicação ok. :)  Sefaz disse:" + resp);
			return true;
		}
		return false;
				
	}
	
	@Test
	public void conversarKFuncionalTDocfiscal() {
		Log.out("Utilizando keystore funcional e cacerts do docfiscal");
		KeyManager[] keymanager = openCert.getKeyManagerFuncional();
		X509TrustManager trustmanager = openTrust.getX509TrustManagerKeyStoreDocfiscal();
		boolean conversa = conversarSefaz(keymanager, trustmanager);
		Assert.assertTrue(conversa);
	}
	
	@Test
	public void conversarKFuncionalTGerado() {
		Log.out("Utilizando keystore funcional e cacerts gerado");
		KeyManager[] keymanager = openCert.getKeyManagerFuncional();
		X509TrustManager trustmanager = openTrust.getX509TrustManagerKeyStoreGerado();
		boolean conversa = conversarSefaz(keymanager, trustmanager);
		Assert.assertTrue(conversa);
	}
	
	@Test
	public void conversarKProblematicoTDocfiscal() {
		Log.out("Utilizando keystore problematico e cacerts do docfiscal");
		KeyManager[] keymanager = openCert.getKeyManagerProblema();
		X509TrustManager trustmanager = openTrust.getX509TrustManagerKeyStoreDocfiscal();
		boolean conversa = conversarSefaz(keymanager, trustmanager);
		Assert.assertTrue(conversa);
	}
	
	@Test
	public void conversarKProblematicoTGerado() {
		Log.out("Utilizando keystore problematico e cacerts do gerado");
		KeyManager[] keymanager = openCert.getKeyManagerProblema();
		X509TrustManager trustmanager = openTrust.getX509TrustManagerKeyStoreGerado();
		boolean conversa = conversarSefaz(keymanager, trustmanager);
		Assert.assertTrue(conversa);
	}

}
