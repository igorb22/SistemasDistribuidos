
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.net.Socket;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Igor Bruno
 */
public class ClientInterface extends JFrame {

    private static Socket clientSocket;
    private DataOutputStream outToServer;
    private String arquivo, texto;
    private ClientInterface exercicio;
    private static boolean status = false;

    // interface
    private static JProgressBar jProgressBar;
    private JButton btnPesquisar;
    private JTextField textField;
    private JLabel addBtn;
    private JPanel panel;
    private JLabel textoInformacao;
    private static JTextArea textArea;
    private JLabel textoDownload;

    public ClientInterface() throws IOException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 460, 340);

        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panel); // adicionando o painel
        panel.setLayout(null);

        textField = new JTextField();
        textField.setBounds(60, 33, 161, 20);
        panel.add(textField);
        //textField.setColumns(10);

        btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.setBounds(260, 33, 100, 20);
        panel.add(btnPesquisar);

        textArea = new JTextArea();
        textArea.setBounds(15, 100, 420, 160);
        panel.add(textArea);

        jProgressBar = new JProgressBar();
        jProgressBar.setStringPainted(true);
        jProgressBar.setBounds(15, 270, 420, 20);
        jProgressBar.setVisible(false);
        panel.add(jProgressBar);

        textoDownload = new JLabel("Fazendo download");
        textoDownload.setBounds(160, 290, 150, 20);
        textoDownload.setVisible(false);
        panel.add(textoDownload);

        textoInformacao = new JLabel();
        textoInformacao.setBounds(15, 70, 420, 20);
        panel.add(textoInformacao);

        setVisible(true);
        setResizable(false);

        estabeleceConexao();

        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (clientSocket.isConnected())
                        solicitacaoCliente();
                    else
                        textoInformacao.setText("Conexao fechada, "
                                + "limite de tempo de solicitacoes/resposta atingido");
                } catch (IOException ex) {
                    Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        ClientInterface exercicio = new ClientInterface();
    }

    public void estabeleceConexao() throws IOException {
        clientSocket = new Socket("127.0.0.1", 6790);
    }

    public void solicitacaoCliente() throws IOException {
        new Thread(contaTempo).start();

        outToServer = new DataOutputStream(clientSocket.getOutputStream());

        arquivo = textField.getText();

        if (!arquivo.equalsIgnoreCase(" ")) {
            System.out.println(arquivo);

            outToServer.writeBytes(arquivo + "\n");

            BufferedReader inFromServer = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String objeto = inFromServer.readLine();

            System.out.println(objeto);

            if (objeto.equals("existe")) {
                // ------------------------------------------------

                jProgressBar.setVisible(true);
                jProgressBar.setValue(0);

                textoDownload.setVisible(true);
                textoDownload.setText("Fazendo download");

                // ------------------------------------------------
                int filesize = 6022386;

                int bytesRead;
                int current = 0;

                // recebendo o arquivo
                byte[] mybytearray = new byte[filesize];

                InputStream is = clientSocket.getInputStream();
                FileOutputStream fos = new FileOutputStream(arquivo+".html");
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                current = 0;
                do {
                    bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                    if (bytesRead >= 0) {
                        current += bytesRead;
                    }
                    jProgressBar.setValue(current);
                } while (bytesRead > -1);
                status = true;

                if (current < 100) {
                    jProgressBar.setValue(100);
                }
                textoDownload.setText("Download Concluido");

                // imprimindo o array de bytes na tela
                textArea.setText(new String(mybytearray, "UTF-8") + "");

                // gravando dados do download em um novi arquivo do lado do cliente
                bos.write(mybytearray, 0, current);

                bos.close();

                clientSocket.close();
                textoInformacao.setText("Arquivo encontrado."
                        + " A conexão foi fechada, limite de solicitcoes atingido");

            } else if (objeto.equals("naoexiste")) {
                textoInformacao.setText("Arquivo não encontrado, conexão fechada,"
                        + " tente novamente.");
            } 
        }
    }

    private static Runnable contaTempo = new Runnable() {
        public void run() {
            while (!status) {
                try {
                    Thread.sleep(59000);
                    if (!status) {
                        clientSocket.close();
                        status = true;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}
