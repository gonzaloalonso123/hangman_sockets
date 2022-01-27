import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

public class Servidor extends JFrame {

	private String palabra = "";
	private int vidas = 6;

	private String[][] palabras = { { "ola", "sol", "ave", "paz", "rio" }, { "nave", "cola", "tubo", "rata", "perro" },
			{ "casco", "mosca", "arroz", "barco", "raton" }, { "liebre", "cabeza", "babosa", "maceta", "mueble" } };

	ServerSocket server;
	Socket conexion;
	char[] incognitas;
	OutputStream os;
	InputStream is;

	public static void main(String[] args) throws IOException {
		new Servidor();
	}

	public Servidor() throws IOException {

		initComponents();
		server = new ServerSocket(4045);
		iniciarPartida();
	}

	private void iniciarPartida() throws IOException {

		info.setText("Esperando cliente...");
		conexion = server.accept();

		info.append("\nConexion de cliente");
		os = conexion.getOutputStream();
		is = conexion.getInputStream();

		int longitud = 0;
		longitud = is.read();

		asignarPalabra(longitud);

		incognitas = new char[palabra.length()];

		for (int i = 0; i < incognitas.length; i++) {
			incognitas[i] = '_';
		}

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(new Paquete(new String(incognitas), vidas, false, false));
		escucharLetra();
	}

	private void escucharLetra() throws IOException {

		while (!comprobarFinPartida()) {
			char letra = (char) is.read();
			mandarPaquete(letra);
		}

		info.append("\nFin del juego");
		dispose();
	}

	private boolean comprobarFinPartida() {

		boolean terminado = true;

		for (int i = 0; i < incognitas.length; i++)
			if (incognitas[i] == '_')
				terminado = false;

		if (vidas < 1)
			terminado = true;

		return terminado;
	}

	private void mandarPaquete(char letra) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);

		Paquete paquete = new Paquete();

		paquete.setAcierto(comprobarLetra(letra));
		paquete.setPalabra(new String(incognitas));
		paquete.setVidas(vidas);
		paquete.setFinPartida(comprobarFinPartida());

		oos.writeObject(paquete);
	}

	private boolean comprobarLetra(char letra) throws IOException {

		boolean letraEncontrada = false;
		for (int i = 0; i < palabra.length(); i++) {
			if (palabra.charAt(i) == letra) {
				incognitas[i] = letra;
				info.append("\nSe ha encontrado la letra " + letra);
				letraEncontrada = true;
			}
		}
		if (!letraEncontrada) {
			vidas--;
			info.append("\nNo hay coincidencias, quedan " + vidas + " vidas.");
		}
		return letraEncontrada;
	}

	private void asignarPalabra(int longitud) throws IOException {

		Random r = new Random();
		palabra = palabras[longitud - 3][r.nextInt(palabras[longitud - 3].length)];
		info.append("\nPalabra escogida: " + palabra);
	}

	private void initComponents() {

		scroll = new java.awt.ScrollPane();
		info = new java.awt.TextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		scroll.setBackground(new java.awt.Color(204, 255, 255));
		scroll.add(info);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
						.addContainerGap()));
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private java.awt.ScrollPane scroll;
	private java.awt.TextArea info;
}