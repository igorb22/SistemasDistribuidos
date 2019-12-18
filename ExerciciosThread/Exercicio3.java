import java.util.Random;

/**
 * UNIVERSIDADE FEDERAL DE SERGIPE
 * SISTEMAS DISTRIBUIDOS
 * IGOR BRUNO DOS SANTOS NASCIMENTO - 201600007888
 * EXERCICIO 3*/

public class Exercicio3 {
	private int tamanhoMaximoSalto;
	private int distanciaPercurso;
	private Random random;
	private String colocacao[] = {"","","","","",""};
	
	
	public Exercicio3() {
		this.tamanhoMaximoSalto = 5;
		this.distanciaPercurso = 30;
		this.random = new Random();
		
	}
	
	public static void main(String[] args) {
		Exercicio3 ex3 = new Exercicio3();
		ex3.new Sapo("Sapo 1").start();
		ex3.new Sapo("Sapo 2").start();
		ex3.new Sapo("Sapo 3").start();
		ex3.new Sapo("Sapo 4").start();
		ex3.new Sapo("Sapo 5").start();
		
		
		//ex3.imprimeColocacao();
	}
	
	
	
	/*Thread dos Sapos*/
	public class Sapo extends Thread{		
		private String nome;
		private int distanciaPercorrida;
		private int saltoAtual;
		
		public Sapo(String nome) {
			this.nome = nome;
			this.distanciaPercorrida = 0;
			this.saltoAtual = 0;
		}
		
		public void run() {
			while(true) {
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saltoAtual = random.nextInt(tamanhoMaximoSalto);
				distanciaPercorrida += saltoAtual; 
				
				System.out.println("\nSapo: "+nome+"\n"+"Salto: "+saltoAtual+"\n"+"Distancia Percorrida: "+distanciaPercorrida);
				
				if (distanciaPercorrida >= distanciaPercurso) {
					for (int i = 0;i <5;i++) {
						if (colocacao[i].equals("")) {
							colocacao[i] = nome;
							System.out.println("\nSapo "+nome+" chegou!!!"); 
							break;
						}	
					}
					imprimeColocacao();
					this.stop();
				}
			}
		}
	}
	
	
	public  void imprimeColocacao() {
		System.out.println("\n------- Colocação -------");
		for (int i = 0; i < 5;i++){
			System.out.println((i+1)+"° "+colocacao[i]);
		}
		System.out.println("-------------------------");
	}
	
}
