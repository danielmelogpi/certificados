package certificado;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/* Classe copiada do <a href='http://goo.gl/oJc6ow'>link do site da ibm </a>,
 * verificar esse codigo apenas
 * adicionei uma referencia ao field chain e criei um mtodo pra executar a
 * criao dos certificados
 * 
 * @author fabricio.csantos
 * 
 */
public class MyX509TrustManager implements X509TrustManager {
	/*
	 * The default X509TrustManager returned by IbmX509. We'll delegate
	 * decisions to it, and fall back to the logic in this class if the default
	 * X509TrustManager doesn't trust it.
	 */
	private X509TrustManager pkixTrustManager;
	private X509Certificate chain[];

	// TODO: esse mtodo est aqui para encapsular a necessidade de conhecer
	// detalhes desse objeto, verificar o melhor lugar para esse mtodo.
	public void generateCertificates(KeyStore ks, String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateEncodingException {
		if (chain == null) {
			throw new NullPointerException();
		}
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			sha1.update(cert.getEncoded());
			md5.update(cert.getEncoded());
			String aliasFinal = alias + " - " + i;
			ks.setCertificateEntry(aliasFinal, cert);
		}
	}

	public MyX509TrustManager(KeyStore ks) {

		TrustManagerFactory tmf;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(ks);
			TrustManager tms[] = tmf.getTrustManagers();

			/*
			 * Iterate over the returned trustmanagers, look for an instance of
			 * X509TrustManager. If found, use that as our "default" trust
			 * manager.
			 */
			for (int i = 0; i < tms.length; i++) {
				if (tms[i] instanceof X509TrustManager) {
					pkixTrustManager = (X509TrustManager) tms[i];
					return;
				}
			}

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
		/*
		 * Find some other way to initialize, or else we have to fail the
		 * constructor.
		 */
		throw new RuntimeException("Couldn't initialize");
	}

	/*
	 * Delegate to the default trust manager.
	 */
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		try {
			this.chain = chain;
			pkixTrustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException excep) {
			// do any special handling here, or rethrow exception.
		}
	}

	/*
	 * Delegate to the default trust manager.
	 */
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		try {
			this.chain = chain;
			pkixTrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException excep) {
			/*
			 * Possibly pop up a dialog box asking whether to trust the cert
			 * chain.
			 */
		}
	}

	/*
	 * Merely pass this through.
	 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return pkixTrustManager.getAcceptedIssuers();
	}
}
