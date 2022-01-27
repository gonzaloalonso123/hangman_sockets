import java.io.DataOutputStream;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Cliente extends JFrame {

	Socket conexion;

	boolean funcionando = true;
	boolean longitudDada = false;

	InputStream is;
	OutputStream os;

	private char[][] ahorcado = new char[10][10];
	String letrasUsadas = "";

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Cliente();
	}

	public Cliente() throws UnknownHostException, IOException {
		initComponents();
		crearConexion();
		crearHorca();
	}

	private void crearConexion() throws UnknownHostException, IOException {

		conexion = new Socket("localhost", 4045);

		is = conexion.getInputStream();
		os = conexion.getOutputStream();
		info.setText(
				"<html><b>Bienvenido al ahorcado.</b><br/>Introduce una longitud de palabra para continuar...</html>");
	}

	private void enviarBtnActionPerformed(java.awt.event.ActionEvent evt) throws IOException, ClassNotFoundException {

		String mensaje = enviar.getText();
		if (comprobarInput(mensaje)) {
			if (!longitudDada) {
				os.write(Integer.parseInt(mensaje));
				longitudDada = true;
				labelLetrasConsumidas.setVisible(true);

			} else {
				os.write(mensaje.charAt(0));
			}
			ObtenerRespuesta();
		}
		enviar.setText("");
	}

	private void ObtenerRespuesta() throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(is);
		Paquete paquete = (Paquete) ois.readObject();
		analizarPaquete(paquete);
	}

	private void analizarPaquete(Paquete paquete) throws IOException {
		dibujo.setText("");

		incognitas.setText(paquete.getPalabra());

		actualizarHorca(paquete.getVidas());

		dibujo.setText(horcaAString());

		letrasUsadasLabel.setText(letrasUsadas.replace("", ",").replaceAll("^.|.$", ""));

		if (paquete.isAcierto()) {
			if (paquete.isFinPartida()) {
				info.setText("HAS GANADO!");
				cambiarAFin();
			} else
				info.setText("<html>ACIERTO!</html>");

			info.setBackground(new java.awt.Color(0, 255, 0));

		} else if (letrasUsadas.length() != 0) {
			if (paquete.isFinPartida()) {
				info.setText("HAS MUERTO");
				cambiarAFin();
			}

			else
				info.setText("ESTAS MAS CERCA DE MORIR...");

			info.setBackground(new java.awt.Color(250, 60, 60));

		} else {
			info.setText("Introduce letras para jugar");
			info.setBackground(new java.awt.Color(238, 238, 238));
		}
	}

	private void crearHorca() {
		for (int i = 0; i < ahorcado.length; i++) {
			for (int j = 0; j < ahorcado[i].length; j++) {
				ahorcado[i][j] = ' ';
			}
		}

		ahorcado[3][4] = '|';
		ahorcado[3][3] = '|';
		ahorcado[3][2] = '|';
		ahorcado[3][1] = '|';
		ahorcado[3][0] = '-';
		ahorcado[4][0] = '-';
		ahorcado[6][0] = '-';
		ahorcado[6][0] = '+';
		ahorcado[7][4] = '_';
		ahorcado[6][4] = '_';
		ahorcado[5][4] = '_';
		ahorcado[4][4] = '_';
	}

	private void actualizarHorca(int vidas) throws IOException {
		switch (vidas) {
		case 5:
			ahorcado[8][1] = 'O';
			break;
		case 4:
			ahorcado[8][2] = '|';
			break;
		case 3:
			ahorcado[7][2] = '/';
			break;
		case 2:
			ahorcado[9][2] = '\\';
			break;
		case 1:
			ahorcado[8][3] = '/';
			break;
		case 0:
			ahorcado[9][3] = '\\';
			break;
		}
	}

	private String horcaAString() {
		String horca = "<html><br/><br/><br/><br/>";
		for (int i = 0; i < ahorcado.length; i++) {
			for (int j = 0; j < ahorcado[i].length; j++) {
				if (ahorcado[j][i] == ' ') {
					horca += "&nbsp;";
				} else {
					horca += ahorcado[j][i];
				}
			}
			horca += "<br/>";
		}
		horca += "</html>";
		return horca;
	}

	private void cambiarAFin() {
		enviar.setVisible(false);
		enviarBtn.setText("JUGAR DE NUEVO");
		enviarBtn.setIcon(null);
		enviarBtn.setBackground(Color.black);
		enviarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				Lanzador.main(null);
			}
		});
	}

	private boolean seHaMandadoYa(char letra) {
		for (int i = 0; i < letrasUsadas.length(); i++)
			if (letra == letrasUsadas.charAt(i))
				return true;

		return false;
	}

	private boolean comprobarInput(String textoMandado) {

		int tipoError = 0;

		// comprobar 1 solo caracter
		if (textoMandado.length() != 1) {
			tipoError = 1;
		} else {
			if (!longitudDada) {
				// errores en el input de la longitud de la palabra
				int longitud = 0;
				try {
					longitud = Integer.parseInt(textoMandado);
					if (longitud < 3 || longitud > 6) {
						// la longitud no esta entre 3-6
						tipoError = 3;
					}
				} catch (Exception e) {
					// La longitud no es parseable a int
					tipoError = 2;
				}

			} else { // errores en el input de una letra

				// es una letra?
				if (textoMandado.matches("[a-zA-Z]")) {

					if (seHaMandadoYa(textoMandado.charAt(0))) {
						// se ha mandado ya
						tipoError = 5;
					} else {
						letrasUsadas += textoMandado;
					}
				} else {
					tipoError = 4;
				}
			}
		}
		if (tipoError == 0)
			return true;
		mostrarError(tipoError);
		return false;
	}

	private void mostrarError(int tipo) {
		info.setBackground(new java.awt.Color(250, 250, 80));

		switch (tipo) {
		case 1:
			info.setText("Introduce 1 solo caracter");
			break;
		case 2:
			info.setText("Introduzca un numero");
			break;
		case 3:
			info.setText("La longitud debe ser entre 3-6");
			break;
		case 4:
			info.setText("Introduzca una letra");
			break;
		case 5:
			info.setText("Esta letra ya ha sido usada");
			break;
		}
	}

	private void initComponents() {

		dibujo = new javax.swing.JLabel();
		enviar = new javax.swing.JTextField();
		enviarBtn = new javax.swing.JButton();
		imagen = new javax.swing.JLabel();
		info = new javax.swing.JLabel();
		labelLetrasConsumidas = new javax.swing.JLabel();
		letrasUsadasLabel = new javax.swing.JLabel();
		incognitas = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(143, 181, 248));

		dibujo.setBackground(new java.awt.Color(204, 255, 204));
		dibujo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		enviar.setBackground(new java.awt.Color(200, 200, 200));
		enviar.setBorder(new javax.swing.border.MatteBorder(null));

		enviarBtn.setBackground(new java.awt.Color(51, 255, 0));
		enviarBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("mandar.png"))); // NOI18N
		enviarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		enviarBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					enviarBtnActionPerformed(evt);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		imagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("fotoAhorcado.png"))); // NOI18N

		info.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		labelLetrasConsumidas.setText("Letras usadas:");

		incognitas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(imagen, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup().addGap(6, 6, 6)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup().addComponent(labelLetrasConsumidas)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(letrasUsadasLabel, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(info, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(enviar, javax.swing.GroupLayout.PREFERRED_SIZE, 51,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(enviarBtn))
						.addComponent(dibujo, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(incognitas, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addGroup(layout.createSequentialGroup()
										.addComponent(imagen, javax.swing.GroupLayout.PREFERRED_SIZE, 42,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(info, javax.swing.GroupLayout.PREFERRED_SIZE, 79,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup()
										.addComponent(dibujo, javax.swing.GroupLayout.PREFERRED_SIZE, 108,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(incognitas, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(enviar, javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(labelLetrasConsumidas).addComponent(letrasUsadasLabel,
														javax.swing.GroupLayout.PREFERRED_SIZE, 29,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(0, 8, Short.MAX_VALUE))
								.addGroup(layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addComponent(enviarBtn)))
						.addContainerGap()));

		Font font = new Font("Arial Black", Font.BOLD, 16);
		enviar.setHorizontalAlignment(enviar.CENTER);
		incognitas.setHorizontalAlignment(incognitas.CENTER);
		enviar.setFont(font);
		dibujo.setOpaque(true);
		info.setOpaque(true);
		labelLetrasConsumidas.setVisible(false);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		pack();

	}

	private javax.swing.JLabel dibujo;
	private javax.swing.JButton enviarBtn;
	private javax.swing.JLabel imagen;
	private javax.swing.JLabel incognitas;
	private javax.swing.JLabel info;
	private javax.swing.JLabel labelLetrasConsumidas;
	private javax.swing.JTextField enviar;
	private javax.swing.JLabel letrasUsadasLabel;
}