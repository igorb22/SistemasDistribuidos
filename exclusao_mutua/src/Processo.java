import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Date;


public class Processo extends Thread {
    private int pid;
    private int qtdProcessos;
    private int qtdOk;
    private int periodoEscrita;
	private String solicitacao;
    private boolean pretendeEntrar = false; 
    private boolean estouNaRegiao = false;
    private boolean respondiOk = false;
    private byte rec[] = new byte[256];
	private ArrayList<String> fileira = new ArrayList<>();


    public Processo(int id,int qtdProcessos,int periodoEscrita){
        this.pid = id;
        this.qtdProcessos = qtdProcessos;
        this.periodoEscrita = periodoEscrita;
    }

    @Override
    public void run() {
        super.run();
        
        new RecebeRespostas().start();
        new ManipulaAreaCritica().start();
        
                
        while(true) {
    		try {
    			
    			/*
    			 * A variavel periodoEscrita define quais intervalos de 
    			 * tempo o processo atual ir� solicitar entrada na 
    			 * regiao critica, por exemplo, se o tempo atual em 
    			 * milissegundos dividido  por o periodEscrita tiver 
    			 * resto 0 na divis�o, ent�o � o tempo deste processo 
    			 * solicitar entrada na regi�o cr�tica.*/
    			
    			/*
    			 * Caso este processo n�o pretenda entrar na regi�o critica 
    			 * e o tempo atual do sistema for o periodo de escrita dele, ent�o   
    			 * devemos enviar uma solicita��o para etrar na regi�o critica*/
    			
    			if (!pretendeEntrar && ( new Date().getTime()%periodoEscrita == 0)) {
            		
    				pretendeEntrar = true;	
        			
    				long t = new Date().getTime();
            		solicitacao = "regiao_critica;"+pid+";"+t;
            		
            		/*
    				 * Enviando broadcast com a solicita��o*/
        			enviarBroadcast(solicitacao);	
            	}
    			
    			Thread.sleep(500);
    		} catch (IOException | InterruptedException e) {e.printStackTrace();}
        }
    }


    public void enviarBroadcast(String s) throws IOException, InterruptedException {
    	/*
    	 * Enviando broadcast para os demais processos*/
    	
    	byte[] b = s.getBytes();
    	InetAddress addr = InetAddress.getByName("255.255.255.255");
        DatagramPacket pkg = new DatagramPacket(b, b.length, addr,6001);
        DatagramSocket ds = new DatagramSocket();
		Thread.sleep(1000);
        ds.send(pkg);
    }
    
    public class RecebeRespostas extends Thread{	
    	public void run() {
    		while(true) {
        		try {
        			
        			/*
        			 * Trecho de c�digo que fica aguardando mensagens 
        			 * dos outros processos*/
        			
        			MulticastSocket mcs = new MulticastSocket(6001);
        		    DatagramPacket pkg = new DatagramPacket(rec, rec.length);
        		    mcs.receive(pkg); 
        		    String data = new String(pkg.getData(), 0, pkg.getLength()); 
        		        
        			System.out.println("Processo "+pid+" recebendo mansagem: "+data);
        		        
        			/*
        			 * Se eu pretendo entrar na regi�o critica e recebi 
        			 * um OK ent�o devo contabiliza-lo, caso contr�rio n�o contabilizado */
        			
        		    if(data.equals("OK") && pretendeEntrar) {
        		    	
        		    	/*
        		    	 * Quando eu envio uma mensagem fora dessa classe, o
        		    	 * processo que enviou tamb�m recebe  a informa��o,
        		    	 * ent�o temos que tratar isso para n�o acontecer erros */
        		    	
        		    	if(respondiOk)
        		    		respondiOk = false;
        		    	else
        		    		qtdOk++;
        		    	
        		    	/*
        		    	 * Tratamento para uma solicita��o para entrar 
        		    	 * na regi�o cr�tica*/
        		    }else if (data.split(";").length > 0){
        		    	
        		    	/*
        		    	 * Se eu n�o pretendo entrar na regi�o critica  
        		    	 * e nem estou na regi�o cr�tica, ent�o envio um OK, 
        		    	 * caso uma das situa��es seja verdadeira ent�o , 
        		    	 * devo trat�-las de maneiras diferente, enfileirando 
        		    	 * a requisi��o ou respondendo OK*/
        		    	
	        		    if (!pretendeEntrar && !estouNaRegiao) 
	        		    	 enviarBroadcast("OK");
	        		    else if (estouNaRegiao){
	        		    
	        		    	fileira.add(data);	
	        		    	 System.out.println("Processo "+pid+", enfileirado DE "+fileira.get(fileira.size()-1));	
	        		    
	        		    } else if (pretendeEntrar) {
	        		    	
	        		    	int myId = Integer.parseInt(solicitacao.split(";")[1]);
	        		 	    int yourId = Integer.parseInt(data.split(";")[1]); 
	        		 	    	
	        		 	    if (myId != yourId) {
	        		 	    		
	        		 	    	long myTemp = Long.parseLong(solicitacao.split(";")[2]);
	        		 	 	    long yourTemp = Long.parseLong(data.split(";")[2]);
	        		 	    		
	        		 	    	if (myTemp < yourTemp) {
	        		 	    		
	        		 	    		fileira.add(data);	
	        		 	    		qtdOk++;
	        		 	    		System.out.println("Processo "+pid+", enfileirado "+fileira.get(fileira.size()-1));
	        		 	    	
	        		 	    	} else 
	        		 	    		enviarBroadcast("OK");
	        		 	    }
	        		     }
        		    }
    	    		Thread.sleep(1000);
				} catch (IOException | InterruptedException e) {e.printStackTrace();} 
        	}
    	}
    }
    
    public class ManipulaAreaCritica extends Thread{
    	public void run() {
    		while(true) {
    			try {
    				/*
    				 * Se a quantidade de OKs recebidos for a quantidade de processos
    				 * menos o processo atual, quer dizer que os demais processos 
    				 * deram permiss�o para entrar na regi�o cr�tica*/
		    		if (qtdOk == (qtdProcessos-1)) {
		    			
		    			estouNaRegiao = true;
		    			System.out.println("Processo "+pid+" escrevendo na regiao critica...");
		    			
							
			    			FileWriter fw = new FileWriter(new File("regiao_critica.txt"),true);
			    			BufferedWriter bw = new BufferedWriter(fw);
			    		
			    			String[] s = solicitacao.split(";");
			    			bw.write("id: "+s[1]+" horario: "+s[2]+"\n");
			    			
			    			bw.close();
			    			fw.close();
			    			
			    			pretendeEntrar = false;
			    			estouNaRegiao = false;
			    			
			    			qtdOk = 0;
			    			
			    			/*
			    			 * Caso alguma requisi��o tenha sido enfileirada 
			    			 * desenfileira e responde OK para liberar o processo que est� em espera*/
			    			if (fileira.size() > 0) {
			    				enviarBroadcast("OK");
			    				respondiOk = true;
			    				fileira = new ArrayList();
			    			}
		    		}
	    		Thread.sleep(100);
	    		} catch (InterruptedException | IOException e) {e.printStackTrace();}
    		}
    	}
    }
   
}
