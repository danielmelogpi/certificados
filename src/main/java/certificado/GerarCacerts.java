package certificado;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class GerarCacerts {

	/**
	 * Gera o arquivo com os certificados de todos os servidores da NFE.
	 * Baseado em http://www.javac.com.br/jc/posts/list/34.page
	 */	

	private static final String CACERTS_NAME = "ExperimentoCacerts";
	private static final String CACERTS_PATH =  "src/main/resources/certificados/keystore";
	private static final char SEPARATOR = File.separatorChar;
	private static final int TIMEOUT_WS = 30;
	private static HashMap<String, Boolean> servidoresConectados = new HashMap<>();


	public static void main(String[] args) {	
		try {

			char[] passphrase = "changeit".toCharArray();
			File file = new File(CACERTS_PATH + SEPARATOR + CACERTS_NAME);

			if (file.isFile()) {
				file.delete();
			}

			if (file.isFile() == false) {

				File dir = new File(System.getProperty("java.home") + SEPARATOR + "lib" + SEPARATOR + "security");
				file = new File(dir, CACERTS_NAME);
				if (file.isFile() == false) {
					file = new File(dir, "cacerts");
				}
			}

			info("| Loading KeyStore " + file + "...");
			InputStream in = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, passphrase);
			in.close();
			
			File listaServidores = new File("src/main/resources/hosts/lista");
			LineIterator it = FileUtils.lineIterator(listaServidores, "UTF-8");
			try {
			    while (it.hasNext()) {
			    	String servidor = it.nextLine();
			    	if (servidoresConectados.get(servidor)==null) {
			    		get(servidor, 443, ks);
			    	}
			    }
			} finally {
			    it.close();
			}
			
			try {
				adicionarACBaixadas(ks);
			} catch (Exception e) {
				error(e.getLocalizedMessage());
			}
			

			System.out.println(CACERTS_PATH + SEPARATOR + CACERTS_NAME);
			File cafile = new File(CACERTS_PATH + SEPARATOR + CACERTS_NAME);
			OutputStream out = new FileOutputStream(cafile);
			ks.store(out, passphrase);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void get(String host, int port, KeyStore ks) throws Exception {
		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();

		info("| Opening connection to " + host + ":" + port + "...");
		
		SSLSocket socket = null;
		try{
			socket = (SSLSocket) factory.createSocket(host, port);
			socket.setSoTimeout(TIMEOUT_WS * 1000);
		}
		catch(Exception e) {
			error("Erro ao acessar "+ host);
			return;
		}
		
		
		try {
			info("| Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			info("| No errors, certificate is already trusted");
		} catch (SSLHandshakeException e) {
			/**
			 * PKIX path building failed:
			 * sun.security.provider.certpath.SunCertPathBuilderException:
			 * unable to find valid certification path to requested target
			 * Não tratado, pois sempre ocorre essa exceo quando o cacerts
			 * nao esta gerado.
			 */
		} catch (SSLException e) {
			error("| " + e.toString());
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			info("| Could not obtain server certificate chain");
		} else {
			info("| Server sent " + chain.length + " certificate(s):");
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (int i = 0; i < chain.length; i++) {
				X509Certificate cert = chain[i];
				sha1.update(cert.getEncoded());
				md5.update(cert.getEncoded());

				String alias = host + "-" + (i);
				ks.setCertificateEntry(alias, cert);
				info("| Added certificate to keystore '" + CACERTS_PATH + SEPARATOR + CACERTS_NAME + "' using alias '" + alias + "'");
			}
		}
	}
	
	private static void adicionarACBaixadas(KeyStore ks) throws CertificateException, FileNotFoundException, KeyStoreException {
		File dir = new File("src/main/resources/certificado/autoridades");
		info ("adicionando autoridades baixadas");
		File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	Collection  col_crt1 =CertificateFactory.getInstance("X509").generateCertificates(new FileInputStream(child));
		    	Certificate crt1 = (Certificate) col_crt1.iterator().next();
		    	Certificate[] chain = new Certificate[] { crt1 };
		    	String alias1 = ((X509Certificate) crt1).getSubjectX500Principal().getName();
		    	info("Adicionando alias " + alias1);
		    	ks.setCertificateEntry(alias1, crt1);
		    }
		  } else {
		    error("erro ao iniciar instalacao de autoridades baixadas");
		  }
		
	}

	private static class SavingTrustManager implements X509TrustManager {
		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
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


	private static void info(String log) {
		System.out.println("INFO: " + log);
	}

	private static void error(String log) {
		System.out.println("ERROR: " + log);
	}

}
