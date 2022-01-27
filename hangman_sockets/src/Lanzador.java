import java.io.IOException;

public class Lanzador extends Thread{

	public static void main(String[] args) {
		
		new Lanzador().start();
	}
	
	public void run()
	{
		try {
			Runtime.getRuntime().exec("java -jar servidorAhorcado.jar");
		} catch (IOException e2) {}
		try {
			sleep(2000);
		} catch (InterruptedException e1) {}
		try {
			Runtime.getRuntime().exec("java -jar clienteAhorcado.jar");
		} catch (IOException e) {}
	}
}