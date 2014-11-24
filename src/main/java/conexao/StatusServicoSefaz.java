package conexao;


import java.net.URL;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import util.Log;
import br.com.pcsist.docfiscal.framework.https.HttpsClientBuilder;
import br.com.pcsist.docfiscal.framework.soap.CustomMassageCallback;
import br.com.pcsist.docfiscal.framework.soap.XmlHttpsSender;

public class StatusServicoSefaz {
	
	
	public StringResult execute(String uri, String data,  KeyManager[] keyManagers, X509TrustManager trustManager) throws Exception {
		HttpsClientBuilder builder = new HttpsClientBuilder();
		StringResult result = new StringResult();
		XmlHttpsSender sender = new XmlHttpsSender();
		
		CustomMassageCallback callback;
		StringSource source;
		
		try {
			ConsStatServ consStat = new ConsStatServ();
			source = new StringSource(consStat.toString());
			callback = new CustomMassageCallback(consStat.getHeader(), consStat.NFECONSULTA_SOAP_ACTION);
			
			builder.addTrustManager(trustManager).setKeyManager(keyManagers).setUrl(new URL(uri));
			sender.setMessageSender(builder.build());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			sender.send(uri, source, callback, result);
			return result;
		} catch (Exception e) {
			Log.out(e.getLocalizedMessage());
			return null;
		}
		
	}
	
	
}
