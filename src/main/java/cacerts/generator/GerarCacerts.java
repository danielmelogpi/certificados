package cacerts.generator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import util.Log;
import cacerts.generator.trustmanager.SavingTrustManager;
import cacerts.generator.webservices.ExtrairWebservices;

public class GerarCacerts {

	/**
	 * Gera o arquivo com os certificados de todos os servidores da NFE.
	 * Baseado em http://www.javac.com.br/jc/posts/list/34.page
	 */	

	private static final String CACERTS_NAME = "cacerts-experimento";
	private static final String CACERTS_PATH =  "src/main/resources/certificados/keystore";
	private static final char SEPARATOR = File.separatorChar;
	private static final int TIMEOUT_WS = 15 * 1000;
	private static Set<String> servidoresConectados = new HashSet<>();
	private static CertificateFactory certFactory;
	private static MessageDigest md5;
	private static MessageDigest sha1;
	private static KeyStore keystore;


	private static void initAlgorithms() throws CertificateException,
			NoSuchAlgorithmException {
		certFactory  = CertificateFactory.getInstance("X509");
		sha1 = MessageDigest.getInstance("SHA1");
		md5 = MessageDigest.getInstance("MD5");
	}

	public static void main(String[] args) {	
		try {
			initAlgorithms();
			
			char[] passphrase = "changeit".toCharArray();
			String outputCacertsFile = CACERTS_PATH + SEPARATOR + CACERTS_NAME;
			File file = new File(outputCacertsFile);

			loadKeystore(passphrase, file);
			
			ExtrairWebservices.init();
			Set<String> webservices = ExtrairWebservices.getExtractedHosts();
			
			Iterator<String> wsIterator = webservices.iterator();
			while(wsIterator.hasNext()) {
				String server = wsIterator.next();
				if (!servidoresConectados.contains(server)) {
					try {
						get(server, 443, keystore);
						servidoresConectados.add(server);
					}catch (Exception e) {
						Log.error("Erro ao buscar certificados de " + server + e.getMessage());
					}
		    	}
			}
			
			try {
				addOfflineCertificates(keystore);
			} catch (Exception e) {
				Log.error("Erro ao salvar certificados offline na keystore. " + e.getLocalizedMessage());
			}

			File cafile = new File(outputCacertsFile);
			OutputStream out = new FileOutputStream(cafile);
			keystore.store(out, passphrase);
			out.close();
			Log.info("Gerado arquivo em " + outputCacertsFile);
		} catch (Exception e) {
			Log.error("Erro ao gerar repositorio de ACs " + e.getMessage());
		}
	}

	private static void loadKeystore(char[] passphrase, File file)
			throws FileNotFoundException, KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		
		if (!file.exists()){
			Log.info("O arquivo indicado para criação não existe. Utilizando cacerts padrão do Java como base");
			file =  new File(System.getProperty("java.home") + SEPARATOR + "lib" + SEPARATOR + "security" + SEPARATOR + "cacerts");
		}
		
		
		Log.info("Carregando keystore em memória ...");
		InputStream in = new FileInputStream(file);
		keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(new FileInputStream(file), passphrase);
		in.close();
	}

	/** Inicia uma conexão com o host indicado e introduz na keystore
	 * os certificados enviados pelo servidor remoto.
	 * @param host String Servidor remoto
	 * @param port int	Porta para conexão (443 para tls)
	 * @param ks KeyStore Repositorio de chaves a receber os certificados
	 * @throws Exception
	 */
	public static void get(String host, int port, KeyStore ks) throws Exception {
		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();

		Log.info("Abrindo conexão com " + host + ":" + port + "...");
		
		SSLSocket socket = null;
		try{
			socket = (SSLSocket) factory.createSocket(host, port);
			socket.setSoTimeout(TIMEOUT_WS);
		}
		catch(Exception e) {
			Log.error(" - Erro ao acessar "+ host);
			return;
		} 
		
		try {
			Log.info(" - Starting SSL handshake...");
			socket.startHandshake();
			Log.info(" - Sem erros. Certificate já tem confiança.");
		} catch (SSLHandshakeException e) {
		} catch (SSLException e) {
			Log.error("" + e.toString());
		} finally{
			socket.close();
		}

		X509Certificate[] chain = tm.getChain();
		if (chain == null) {
			Log.error(" - Não foi possivel obter a cadeia do certificado");
		} else {
			Log.info(" - O servidor enviou " + chain.length + " certificado(s):");
			
			for (int i = 0; i < chain.length; i++) {
				X509Certificate cert = chain[i];
				sha1.update(cert.getEncoded());
				md5.update(cert.getEncoded());

				String alias = host + "-" + (i);
				ks.setCertificateEntry(alias, cert);
				Log.info(" - Adicionado certificado ao keystore usando o alias '" + alias + "'");
			}
		}
	}
	
	/** Adiciona certificados baixados */
	private static void addOfflineCertificates(KeyStore ks) throws CertificateException, FileNotFoundException, KeyStoreException {
		File dir = new File("src/main/resources/certificado/autoridades");
		Log.info("Adicionando autoridades offline");
		File[] offileCertDir = dir.listFiles();
		  if (offileCertDir != null) {
		    for (File certFile : offileCertDir) {
		    	
				FileInputStream crtInputStream = new FileInputStream(certFile);
				Collection<? extends Certificate>  crtColletion = certFactory.generateCertificates(crtInputStream);
		    	Certificate cert = (Certificate) crtColletion.iterator().next();
		    	sha1.update(cert.getEncoded());
				md5.update(cert.getEncoded());
		    	String alias = certFile.getName();
		    	Log.info("Adicionando alias " + alias);
		    	ks.setCertificateEntry(alias, cert);
		    }
		  } else {
		    Log.error("Erro ao iniciar instalacao de autoridades offline");
		  }
	}

}
