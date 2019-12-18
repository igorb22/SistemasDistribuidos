import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * UNIVERSIDADE FEDERAL DE SERGIPE
 * SISTEMAS DISTRIBUIDOS
 * IGOR BRUNO DOS SANTOS NASCIMENTO - 201600007888
 * EXERCICIO 1*/

public class Exercicio1 extends JFrame {
	private static JProgressBar jProgressBar;
	private static JLabel text;
	private static JLabel addBtn;
	private static JButton btnIniciar;
	private static final String[] palavras = { "java", "python", "ada" };
	private static Thread threadArray[];
	private static int progresso = 0;

	public Exercicio1() {
		this.setVisible(true);
		this.setSize(430, 200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		adicionaElementos();
	}	
	
	/* Seta todos os elementos na tela */
	public void adicionaElementos() {
		/* Configurando barra de progresso */
		jProgressBar = new JProgressBar();
		jProgressBar.setStringPainted(true);
		jProgressBar.setValue(0);
		jProgressBar.setBounds(11, 50, 400, 20);

		/* Configurando JLabel do texto */
		text = new JLabel();
		text.setText("Texto aqui");
		text.setBounds(180, 20, 150, 20);

		/* Configurando JButton */
		btnIniciar = new JButton("Inicar");
		btnIniciar.setBounds(130, 90, 150, 20);

		/* Configurando JButton ao um Jlabel */
		addBtn = new JLabel();
		addBtn.add(btnIniciar);

		/* Adicioando componentes no JFrame */
		this.add(jProgressBar);
		this.add(text);
		this.add(addBtn);
	}

	/* Thread 1 - Modifica barra de progresso*/
	private static Runnable modificaBarra = new Runnable() {
	        public void run(){
	            while(true){
	                try {
	                	
						Thread.sleep(20);
						progresso++;
		                jProgressBar.setValue(progresso);
		                
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	                
	                if (progresso == 100)
	                    threadArray[0].stop();
	            }
	        }
	};

	/*Thread 2 - faz a troca entre 3 palavras e para quando a thread 1 para */
	private static Runnable modificaTexto = new Runnable() {
	        int indice = 0;
	        public void run() {
	            while(true){
	            	
	               try {
	            	   
					    Thread.sleep(100);
						text.setText(palavras[indice]);
				        indice++;
				                    
				        if (indice == 3)
				           indice = 0;
				        
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	 
					
		            if (!threadArray[0].isAlive())
		               threadArray[1].stop();
	            }
	        }
	};

	public static void main(String[] args) {
		JFrame janela = new Exercicio1();
		
		btnIniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				threadArray = new Thread[2];

		        // Adicionando referencia da thread responsável pela statusBar
		        threadArray[0] = new Thread(modificaBarra);
		        threadArray[0].start();
		        
		        // Adicionando referencia da thread das palavras
		        threadArray[1] = new Thread(modificaTexto);
		        threadArray[1].start();
		        
		        btnIniciar.setVisible(false);
				
			}
		});
	}
}
