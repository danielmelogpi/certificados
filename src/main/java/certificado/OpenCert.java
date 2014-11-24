package certificado;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;

import javax.net.ssl.KeyManager;


public class OpenCert {
	
	public KeyStore getKeystoreProblema() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoProblema();
		return getKeystore(cert.caminho, cert.senha);
	}
	
	public KeyManager[] getKeyManagerProblema() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoProblema();
		return initKeyManager(getKeystoreProblema(), cert.senha);
	}
	
	public KeyManager[] getKeyManagerFuncional() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoFuncional();
		return initKeyManager(getKeystoreFuncional(), cert.senha);
	}
	
	public KeyManager[] getKeyManagerProblemaExportado() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoProblemaExportado();
		return initKeyManager(getKeystoreProblemaExportado(), cert.senha);
	}
	
	public KeyStore getKeystoreFuncional() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoFuncional();
		return getKeystore(cert.caminho, cert.senha);
	}
	
	public KeyStore getKeystoreProblemaExportado() {
		CertificadoArquivo cert = CertificadoArquivo.getCertificadoProblemaExportado();
		return getKeystore(cert.caminho, cert.senha);
	}
	
	public KeyStore getKeystore(String arquivo, String senha) {
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12", "SunJSSE");
			InputStream in = new FileInputStream(arquivo);
			ks.load(in, senha.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, senha.toCharArray());
			return ks;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  KeyManager[] initKeyManager(KeyStore keystore, String senha) {
		try {
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keystore, senha.toCharArray());
			return kmf.getKeyManagers();
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

