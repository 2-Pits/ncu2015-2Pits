import ClientModule.Client;
import ServerModule.serverOperation;
import junit.framework.TestCase;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientTest extends TestCase {

    Client client;
    Thread thread;
    static MockTCPServer mockTCPServer;
    static boolean isServerUp = false;

    private enum MoveCode {
        TURNEAST, TURNSOUTH, TURNNORTH, TURNWEST, GET
    }

    @Before
    public void setUp() throws Exception {
        client = new Client();
        if(!isServerUp) {
        thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mockTCPServer = new MockTCPServer();
                        mockTCPServer.initTCPServer();
                    } catch (IOException e) {
                    }
                }
            });
        thread.start();
        }

    }

    @After
    public void tearDown() throws Exception {
        client = null;
    }

    @Test
    public void testConnectServerSuccess() throws Exception {

        assertTrue(client.connectServer(InetAddress.getByName("127.0.0.1")));

    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

//    @Test
//    public void testConnectServerFail(){
//
//        thrown.expect(UnknownHostException.class);
//        client.connectServer(InetAddress.getByName(" "));
//
//    }

         @Test(timeout = 1000)
        public void testConnectServerTimeout() throws UnknownHostException{

            client.connectServer(InetAddress.getByName("192.168.1.1"));

    }

    @Test
    public void testInputMoves() throws Exception{
        client.connectServer(InetAddress.getByName("127.0.0.1"));
        client.inputMoves(MoveCode.GET.toString());

        assertEquals(MoveCode.GET.toString(), mockTCPServer.getUserRequest());
//        System.out.println(mockTCPServer.getUserRequest());


    }

    class MockTCPServer implements serverOperation {
        String userRequest = null;
        ServerSocket serverSocket;
        BufferedReader in = null;
        @Override
        public void initTCPServer() throws IOException {

            try
            {
                //Create server and execute Controller to handle event.
                serverSocket = new ServerSocket(11111);
                isServerUp = true;
                System.out.println("server up");
                //Wait for connections and keep connection in the member list.
                while(true){

                    // Accept incoming connections.
                    Socket clientSocket = serverSocket.accept();
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    System.out.println("user in");


                }

            }
            catch(Exception ioe)
            {
                System.out.println("Could not create server. Quitting.");
                System.exit(-1);
            }finally {
                serverSocket.close();
                System.out.println("Server Stopped");
            }
        }

        public String getUserRequest() throws Exception{

            userRequest = in.readLine();

            return userRequest;
        }

        @Override
        public ArrayList<String> getClientIPTable() {
            return null;
        }
    }


}