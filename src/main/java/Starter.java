/**
 * Created by bighead on 2015/10/30.
 */
public class Starter {


    public static void main(String[] args) throws Exception{
        TCPServer server = new TCPServer(11111);
        server.setServerOn();
//        Thread serverThread = new Thread(server);
//        serverThread.start();

//        Client clientA = new Client(1);
//        Thread clientThreadA = new Thread(clientA);
//        clientThreadA.start();
//
//        Client clientB = new Client(2);
//        Thread clientThreadB = new Thread(clientB);
//        clientThreadB.start();

    }

}
