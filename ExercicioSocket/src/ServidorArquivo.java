import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorArquivo {

    private static boolean status = false;
    private static Servidor servidor;

    public static void main(String[] args) {
        try {
            ServerSocket welcomeSocket = new ServerSocket(6790);

            while (true) {
               
                // aguardando conexao do cliente
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Conexão aceita: " + connectionSocket);
                
                // Thread que conta o tempo
                new Thread(contaTempo).start();

                
                // criar thread passando a conexao do cliente
                servidor = new Servidor(connectionSocket);
                servidor.start();
            }
        } catch (Exception ex) {
        }
    }

    public static class Servidor extends Thread {

        private Socket connectionSocket;
        private String arquivo;
        private DataOutputStream outToClient;

        public Servidor(Socket s) {
            connectionSocket = s;
        }

        public void run() {
            try {
                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(connectionSocket.getInputStream()));

                outToClient = new DataOutputStream(
                        connectionSocket.getOutputStream());
                
                // aguardando recebimento do nome do arquivo
                arquivo = inFromClient.readLine();
                status = true;

                File file = new File("arquivos/" + arquivo + ".html");  // LEMBRAR DE MODIFICAR CAMINHO SE FOR NECESRÁRIO
                
                // verifica se ele existe
                if (!file.exists()) {
                    outToClient.writeBytes("naoexiste" + '\n');
                    connectionSocket.close();

                } else {
                    outToClient.writeBytes("existe" + '\n');
                    
                    // enviando dados do arquivo
                    byte[] mybytearray = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    bis.read(mybytearray, 0, mybytearray.length);
                    
                    fis.close();
                    bis.close();
                    
                    OutputStream os = connectionSocket.getOutputStream();
                    System.out.println("Enviando...");
                    
                    os.write(mybytearray, 0, mybytearray.length);
                    
                    connectionSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorArquivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     private static Runnable contaTempo = new Runnable() {
            public void run() {
                while (!status) {
                    try {
                        // contando 1 minuto
                        Thread.sleep(59000);
                        if (!status) {
                            servidor.connectionSocket.close();
                            status = true;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
}
