import java.io.Serializable;

public class Paquete implements Serializable {

	private int vidas;
	private String palabra = "";
	private boolean finPartida;
	private boolean acierto;
	
	Paquete(){}

	public Paquete(String palabra, int vidas, boolean fin, boolean acierto) {	
		setPalabra(palabra);
		this.vidas = vidas;
		this.finPartida = fin;
		this.acierto = acierto;
	}
	
	public void setVidas(int vidas) {
		this.vidas = vidas;
	}

	public int getVidas() {
		return vidas;
	}

	public boolean isAcierto() {
		return acierto;
	}

	public void setAcierto(boolean acierto) {
		this.acierto = acierto;
	}

	public String getPalabra() {
		return palabra;
	}

	public void setPalabra(String palabra) {

		// pone espacios entre cada letra para embellecer
		
		for (int i = 0; i < palabra.length(); i++) {
			this.palabra += palabra.charAt(i) + " ";
		}
	}

	public boolean isFinPartida() {
		return finPartida;
	}

	public void setFinPartida(boolean finPartida) {
		this.finPartida = finPartida;
	}
}
