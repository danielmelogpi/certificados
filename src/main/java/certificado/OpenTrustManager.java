package certificado;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.X509TrustManager;
import util.Log;

public class OpenTrustManager {
	
	public X509TrustManager getX509TrustManagerKeyStoreGerado() {
		return  new MyX509TrustManager(loadCacertsKeyStoreGerado());
	}
	
	public X509TrustManager getX509TrustManagerKeyStoreDocfiscal() {
		return  new MyX509TrustManager(loadCacertsKeyStoreDocfiscal());
	}
	
	private KeyStore loadCacertsKeyStoreGerado() {
		String cacertPath = "./src/main/resources/certificados/keystore/ExperimentoCacerts";
		File file = new File(cacertPath);
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = new FileInputStream(file);
			ks.load(in, "changeit".toCharArray());
			in.close();
			return ks;
		} catch (Exception e) {
			Log.out("No foi possvel carregar TrustStore (cacerts).", e);
			throw new RuntimeException(e);
		}
	}
	
	private KeyStore loadCacertsKeyStoreDocfiscal() {
		String cacertPath = "./src/main/resources/certificados/keystore/DocfiscalCacerts";
		File file = new File(cacertPath);
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = new FileInputStream(file);
			ks.load(in, "changeit".toCharArray());
			in.close();
			return ks;
		} catch (Exception e) {
			Log.out("No foi possvel carregar TrustStore (cacerts).", e);
			throw new RuntimeException(e);
		}
	}
	
	
}
