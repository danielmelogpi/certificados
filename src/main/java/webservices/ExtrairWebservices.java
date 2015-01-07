package webservices;

import java.util.HashMap;

public class ExtrairWebservices {
	
	private static HashMap<String, String> sourceSites = new HashMap<>();  //url, content
	
	public static void main(String[] args) {
		fillSourceSite();
		getPagesContent();
	}

	private static void fillSourceSite() {
		sourceSites.put("http://hom.nfe.fazenda.gov.br/portal/WebServices.aspx","");
		sourceSites.put("http://www.nfe.fazenda.gov.br/portal/WebServices.aspx","");
	}
	
	private static void getPagesContent() {
		
		
	}
	
}
