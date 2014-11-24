package conexao;

public class ConsStatServ {

	public String uri = "https://homologacao.nfe.sefaz.rs.gov.br/ws/NfeStatusServico/NfeStatusServico2.asmx";
	public String codUf = "43";

	public String NFECONSULTA_HEADER = "http://www.portalfiscal.inf.br/nfe/wsdl/NfeStatusServico2";
	public String NFECONSULTA_SOAP_ACTION = "http://www.portalfiscal.inf.br/nfe/wsdl/NfeStatusServico2/nfeStatusServicoNF2";
	public String VERSAO_STATUS_SERVICO = "3.10";

	public String toString() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NfeStatusServico2\"><consStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"3.10\"><tpAmb>2</tpAmb><cUF>"+codUf+"</cUF><xServ>STATUS</xServ></consStatServ></nfeDadosMsg>";
	}
	
	public String getHeader() {
		StringBuilder sb = new StringBuilder("<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NfeStatusServico2\"><cUF>43</cUF><versaoDados>3.10</versaoDados></nfeCabecMsg>");
		return sb.toString();
	}
	
	public String getRequestData() {
		return  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><consStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"3.10\"><tpAmb>2</tpAmb><cUF>"+codUf+"</cUF><xServ>STATUS</xServ></consStatServ>";
	}

}
