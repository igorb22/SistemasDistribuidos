import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/* *
 * Universidade Federal de Sergipe
 * Departamento de sistemas de Informação - DSI
 * Sincronização de relogios - Sistemas Disribuidos
 * Igor Bruno dos santos nascimento - 201600007888
 * */


public class ServidorMain {
	private static double relogio = 0.0;
	private static ArrayList<GuardaReferencia> referencia;
	private static ServerSocket welcomeSocket;
	private double clock = 0;
	
	public ServidorMain() {
	    new Relogio().start();
	}
	
	 public static void main(String[] args) throws InterruptedException, IOException {
	        Scanner sc =new Scanner(System.in);
	        Random random = new Random();
	        ServidorMain servidor;

	        System.out.println("=== Bem Vindo ao Sincronizador de Relógios ===");

	            System.out.println("Deseja instanciar quantos clientes/escravos?: ");
	            int qtd = sc.nextInt();

	            System.out.println("Iniciando processo servidor/master");
	            servidor = new ServidorMain();

	            System.out.println("Iniciando processos escravo...");
	            for(int i = 0;i < qtd;i++)
	                new Escravo(i,(random.nextDouble() * 10)).start();

	            System.out.println("Iniciando processo de sincronização...");
	            servidor.sincronizaRelogio();
	    }
	
	
	
	
	public void sincronizaRelogio() throws InterruptedException, IOException {

		enviaBroadcast();
		
		recebeConexao();		
		
		Thread.sleep(1000);
		
		enviajuste();
	}
	
	// enviar mensagem broadcast para todos os escravos contendo o tempo do servidor tempo
	private void enviaBroadcast() {
		byte tempo[] = (relogio + "").getBytes();

		try {
			InetAddress addr = InetAddress.getByName("255.255.255.255");
			DatagramPacket pkg = new DatagramPacket(tempo, tempo.length, addr, 6001);
			DatagramSocket ds = new DatagramSocket();
			ds.send(pkg);
			System.out.println("Enviando broadcast...");
		} catch (UnknownHostException e2) {} catch (SocketException e) {} catch (IOException e) {}
	}
	
	// Recebe conexões tcp dos escravos
	private static void recebeConexao() throws IOException {
	
			welcomeSocket = new ServerSocket(6790);
			referencia = new ArrayList<>();
		
			@SuppressWarnings("resource")
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				  public void run() {            
					  try {
						  System.out.println("Aceitando conexoes...");
						  Socket s = welcomeSocket.accept();
						  
						  new Conexao(s).start();
					} catch (Exception e) {System.out.println(e.getMessage());}
				  }
			}, 0000, 30);
		}	

	// Algoritmo de berkley, calcula o ajuste da hora dos clientes/escravo
	private void enviajuste() {
		System.out.println("Enviando o ajuste de cada relógio...");
		
		//loop para percorer a lista de conexÃµes estabelecidas
		System.out.println("quantos clientes responderam: "+referencia.size());
		
		double media,soma = 0;
		for (int j = 0; j < referencia.size(); j++)
			 soma += referencia.get(j).horaCliente;
		
		// tirando a media
		media = (soma+clock)/(referencia.size()+1);		
		
		for (int j = 0; j < referencia.size(); j++) {
			try {
				
				DataOutputStream outToServer = new DataOutputStream(referencia.get(j).s.getOutputStream());
				double d = media - referencia.get(j).horaCliente;

				// envia o ajuste para o scravo
				outToServer.writeBytes(d + "\n");

				//fecha a conexÃ£o
				referencia.get(j).s.close();
			} catch (IOException e) {}
		}
	}
	
	public class Relogio extends Thread{
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                    clock += 0.1;
                } catch (InterruptedException e) {}
            }
        }

    }

	public static class Conexao extends Thread {
		private Socket s;
		private double horaCliente;
		private int posicao;

		public Conexao(int posicao) {
			this.posicao = posicao;
			s = referencia.get(posicao).s;
		}

		public Conexao(Socket s) {
			this.s = s;
		}

		public void run() {
			try {
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));

				//pega hora atual de um escravo conectado com o mestre
				horaCliente = Double.parseDouble(inFromClient.readLine());

				//guarda no arraylist a referenï¿½a dos datagrama que recebeu
				referencia.add(new GuardaReferencia(s, horaCliente));
			} catch (IOException e) {
			}
		}
	}
	

	public static class GuardaReferencia {
		Socket s;
		double horaCliente;

		public GuardaReferencia(Socket s, double diferenca) {
			this.s = s;
			this.horaCliente = diferenca;
		}

		public GuardaReferencia(Socket s) {
			this.s = s;
		}

		public void setDiferenca(double diferenca) {
			this.horaCliente = diferenca;
		}
	}
}

