import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


/* Universidade Federal de Sergipe
 * Departamento de sistemas de Informação - DSI
 * Sincronização de relogios - Sistemas Disribuidos
 * Igor Bruno dos santos nascimento - 201600007888
 * */


public class Escravo extends Thread{
	private int pid;
	private double clock =  0;

    public Escravo(int pid) { this.pid = pid;}
        
    public Escravo(int pid, double clockInical) { this.pid = pid; this.clock = clockInical;}

	public void run() {
	    new Relogio().start();
		String data = null;

		try {
			MulticastSocket mcs = new MulticastSocket(6001);
			byte rec[] = new byte[1024];
			DatagramPacket pkg = new DatagramPacket(rec, rec.length);
			mcs.receive(pkg);
			data = new String(pkg.getData(), 0, pkg.getLength());
            System.out.println("Recendo via broadcast/cliente");

        } catch (IOException e) {}

		if (data != null) {
            try {
                Socket clientSocket = new Socket("127.0.0.1", 6790);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(clock + "\n");

                //aguarda a resposta do servidor com o tempo que ï¿½ para ser ajustado
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                double ajuste = Double.parseDouble(inFromClient.readLine());
                System.out.println("\n-----------------------------");
                System.out.print("Processo..............: " + pid
                        + "\nAjuste................: " + ajuste
                        + "\nTempo antes do ajuste.: " + clock);

                //ajusta a variavel relogio
                clock += ajuste;
                System.out.println();

                System.out.println("Tempo apos ajuste.....: " + clock);
                System.out.println("-----------------------------");
            } catch (UnknownHostException e) {} catch (IOException e) {}
        }else
            System.out.println("Não recebemos nenhuma solicitacao via broadcast");
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
}
