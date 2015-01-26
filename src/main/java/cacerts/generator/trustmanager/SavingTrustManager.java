package cacerts.generator.trustmanager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SavingTrustManager implements X509TrustManager {
	private final X509TrustManager tm;
	public X509Certificate[] chain;

	public X509Certificate[] getChain() {
		return this.chain;
	}

	public SavingTrustManager(X509TrustManager tm) {
		this.tm = tm;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
		// throw new UnsupportedOperationException();
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		this.tm.checkServerTrusted(chain, authType);
	}
}