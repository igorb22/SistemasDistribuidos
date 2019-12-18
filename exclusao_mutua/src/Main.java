import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        
    	Scanner sc = new Scanner(System.in);
    	
    
        
        
        System.out.println("============= Esclusão mútua - Algoritmo distribuido ===============\n");
        System.out.println("1 - INSTANCIAR PROCESSOS DE ALGORITMO DISTRIBUIDO");
        System.out.println("2 - VER DADOS GRAVADOS NO ARQUIVO");
        System.out.println("\nOpcao: ");
        
        switch(sc.nextInt()) {
        	case 1: instanciaProcessos();
        		break;
        		
        	case 2: imprimirDadosGravados();
        		break;
        		
        	default: System.out.println("Opção inválida, tente novamente");
        
        }
    }
    
    public static void instanciaProcessos() {
    	Scanner sc = new Scanner(System.in);
    	Random r = new Random();
    	
    	System.out.println("====== Iniciando algoritmo distribuido de exclusão mútua ======\n");
    	System.out.println("**Caso instancie muitos processos é possível \nque sua máquina degrade desempenho**\n");
    	System.out.println("**Caso ele pare ou demore muito pra responder aguarde \num momento ou pare a execução e tente novamente**\n");
    	System.out.println("**Para ver a sincrônia perfeita do \nalgoritmo instancie 2 processos**\n");
    	
    	System.out.println("DESEJA INSTANCIAR QUANTOS PROCESSOS?: ");
    	int qtd = sc.nextInt();
    	
    	System.out.println("Iniciando processos...");
    	for (int i = 0; i < qtd;i++)
    		new Processo(i,qtd,(r.nextInt(50)+1)).start();
    	
    }
    
    
    public static void imprimirDadosGravados() throws IOException {
    	FileReader fr = new FileReader(new File("regiao_critica.txt"));
		
    	BufferedReader br = new BufferedReader(fr);
		String linha = br.readLine();
		
		while(linha != null) {
			System.out.println(linha);
			linha = br.readLine();
		}
		
		br.close();
		fr.close();
    	
    }
}