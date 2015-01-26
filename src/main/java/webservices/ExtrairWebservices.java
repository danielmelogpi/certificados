package webservices;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import util.Log;

/** Vai até os sites conhecidos por fornecer webservices e tenta extrair uma lista 
 * de urls que serão usadas para posterior conexão e criação do repositorio
 * de autoridades certificadoras confiaveis
 * 
 * @author Daniel Melo 
 * github.com/danielmelogpi
 */
public class ExtrairWebservices {

	private static ArrayList<String> sourceSites = new ArrayList<>();
	private static Set<String> extractedHosts = new HashSet<String>();

	public static void main(String[] args) {
		init();
	}
	
	/** Inicializa busca e extração dos webservices. 
	 * @see getExtractedHosts()
	 */
	public static void init(){		
		fillSourceSite();
		extractWebservices();
	}
	
	/** Retorna os webservices extraidos das paginas indicadas 
	 * na configuração */
	public static Set<String> getExtractedHosts() {
		return extractedHosts;
	}
	
	/** TODO expressar como YAML */
	private static void fillSourceSite() {
		sourceSites.add("http://hom.nfe.fazenda.gov.br/portal/WebServices.aspx");
		sourceSites.add("http://www.nfe.fazenda.gov.br/portal/WebServices.aspx");
		sourceSites.add("http://www.cte.fazenda.gov.br/webservices.aspx");
		sourceSites.add("http://hom.cte.fazenda.gov.br/webservices.aspx");
		sourceSites.add("https://mdfe-portal.sefaz.rs.gov.br/Site/Servicos");
	}

	private static void extractWebservices() {
		try {
			for (String site : sourceSites) {
				Log.info("Descobrindo servidores informados em  " + site);
				URL url = new URL(site);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream response = conn.getInputStream();
				String siteContent = IOUtils.toString(response);
				Set<String> hosts = extractHosts(siteContent);
				extractedHosts.addAll(hosts);
				Log.info("Hosts encontrados : ", hosts);
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	private static Set<String> extractHosts(String rawContent) {
		/** TODO configurar via YAML */ 
		String hostRegex = "https:\\/\\/[a-zA-Z]([\\w\\.\\-])*\\.gov.br";
		Matcher matcher = Pattern.compile(hostRegex).matcher(rawContent);
		
		Set<String> results = new HashSet<String>();
		while(matcher.find()) {
			results.add(matcher.group().replace("http://", "").replace("https://", ""));
		}
		return results;
		
	}
	

}
