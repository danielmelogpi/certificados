package certificado;

public class CertificadoArquivo {
	
	public static CertificadoArquivo problema;
	public static CertificadoArquivo funcional;
	public static CertificadoArquivo problemaExportado;
	
	public String senha;
	public String caminho;
	
	public CertificadoArquivo(String caminho, String senha) {
		this.senha = senha;
		this.caminho = caminho;
	}

	public static CertificadoArquivo getCertificadoProblema(){
		if (problema == null) {
			problema = new CertificadoArquivo("./resource/problematico.pfx", "140edson1971");
		}
		return problema;
	}
	
	public static CertificadoArquivo getCertificadoFuncional(){
		if (problema == null) {
			problema = new CertificadoArquivo("./resource/funcional.pfx", "1234");
		}
		return problema;
	}
	
	public static CertificadoArquivo getCertificadoProblemaExportado(){
		if (problemaExportado == null) {
			problemaExportado = new CertificadoArquivo("./resource/problematico-exportado-full.pfx", "140edson1971");
		}
		return problemaExportado;
	}
}
