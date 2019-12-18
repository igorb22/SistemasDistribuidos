import java.util.Random;

/**
 * UNIVERSIDADE FEDERAL DE SERGIPE
 * SISTEMAS DISTRIBUIDOS
 * IGOR BRUNO DOS SANTOS NASCIMENTO - 201600007888
 * EXERCICIO 2*/


public class Exercicio2 {
	private static int matriz[][];
	 
	public Exercicio2(){
		matriz = new int[3][5];
	}
	
	
	public static void main(String[] args) {
		
		/*Preenchendo vetor*/
		Exercicio2 ex = new Exercicio2();
		ex.preencheMatriz();
		
		ex.imprimeMatriz();
		
		/* Iniciando Threads */
		ex.new SomaValoresLinha1().start(); 
		ex.new SomaValoresLinha2().start(); 
		ex.new SomaValoresLinha3().start(); 
	}
	
	
	/*  ------ Threads ------*/
	public class SomaValoresLinha1 extends Thread{
		private int soma;

		
		public SomaValoresLinha1() {
			this.soma = 0;
		}
		
		public void run() {
			for(int i = 0; i < 5;i++) {
				soma += matriz[0][i];
			}
			
			System.out.println("linha: 1, soma: "+soma);
		}		
	} 
	
	public class SomaValoresLinha2 extends Thread{
		private int soma;

		
		public SomaValoresLinha2() {
			this.soma = 0;
		}
		
		public void run() {
			for(int i = 0; i < 5;i++) {
				soma += matriz[1][i];
			}
			System.out.println("linha: 2, soma: "+soma);

		}		
	} 
	
	public class SomaValoresLinha3 extends Thread{
		private int soma;
		
		public SomaValoresLinha3() {
			this.soma = 0;
		}
		
		public void run() {
			for(int i = 0; i < 5;i++) {
				soma += matriz[2][i];
			}	
			System.out.println("linha: 3, soma: "+soma);
			
		}		
	} 
	
	
	public void preencheMatriz() {
		Random gerador = new Random();
		for (int i = 0; i < 3; i++) {
			for(int j = 0; j < 5; j++) {
				matriz[i][j] = gerador.nextInt(1000);
			}
		}
	}

	
	public void imprimeMatriz() {
		for (int i = 0; i < 3; i++) {
			System.out.print("Linha "+(i+1)+": ");
			for(int j = 0; j < 5; j++) {
				System.out.print(matriz[i][j]+" ");
			}
			System.out.println();
		}
		
	} 
	
}
