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
    			 * tempo o processo atual irá solicitar entrada na 
    			 * regiao critica, por exemplo, se o tempo atual em 
    			 * milissegundos dividido  por o periodEscrita tiver 
    			 * resto 0 na divisão, então é o tempo deste processo 
    			 * solicitar entrada na região crítica.*/
    			
    			/*
    			 * Caso este processo não pretenda entrar na região critica 
    			 * e o tempo atual do sistema for o periodo de escrita dele, então   
    			 * devemos enviar uma solicitação para etrar na região critica*/
    			
    			if (!pretendeEntrar && ( new Date().getTime()%periodoEscrita == 0)) {
            		
    				pretendeEntrar = true;	
        			
    				long t = new Date().getTime();
            		solicitacao = "regiao_critica;"+pid+";"+t;
            		
            		/*
    				 * Enviando broadcast com a solicitação*/
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
        			 * Trecho de código que fica aguardando mensagens 
        			 * dos outros processos*/
        			
        			MulticastSocket mcs = new MulticastSocket(6001);
        		    DatagramPacket pkg = new DatagramPacket(rec, rec.length);
        		    mcs.receive(pkg); 
        		    String data = new String(pkg.getData(), 0, pkg.getLength()); 
        		        
        			System.out.println("Processo "+pid+" recebendo mansagem: "+data);
        		        
        			/*
        			 * Se eu pretendo entrar na região critica e recebi 
        			 * um OK então devo contabiliza-lo, caso contrário não contabilizado */
        			
        		    if(data.equals("OK") && pretendeEntrar) {
        		    	
        		    	/*
        		    	 * Quando eu envio uma mensagem fora dessa classe, o
        		    	 * processo que enviou também recebe  a informação,
        		    	 * então temos que tratar isso para não acontecer erros */
        		    	
        		    	if(respondiOk)
        		    		respondiOk = false;
        		    	else
        		    		qtdOk++;
        		    	
        		    	/*
        		    	 * Tratamento para uma solicitação para entrar 
        		    	 * na região crítica*/
        		    }else if (data.split(";").length > 0){
        		    	
        		    	/*
        		    	 * Se eu não pretendo entrar na região critica  
        		    	 * e nem estou na região crítica, então envio um OK, 
        		    	 * caso uma das situações seja verdadeira então , 
        		    	 * devo tratá-las de maneiras diferente, enfileirando 
        		    	 * a requisição ou respondendo OK*/
        		    	
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
    				 * deram permissão para entrar na região crítica*/
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
			    			 * Caso alguma requisição tenha sido enfileirada 
			    			 * desenfileira e responde OK para liberar o processo que está em espera*/
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
