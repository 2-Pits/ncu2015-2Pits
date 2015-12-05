import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class TCPServer{

    private static ScheduledExecutorService fScheduler = null;
    private static final int NUM_THREADS = 4;
    private ArrayList<ClientService> memberList = null;
    private List<String> serverMsgQueue = null;
    private ServerSocket serverSocket;
    private int serverPort = 0;
    private boolean serverOn = false;
    private int totalClient = 0;

    public TCPServer(int serverPort){
        this.serverPort = serverPort;
        memberList = new ArrayList<ClientService>();
        serverMsgQueue = Collections.synchronizedList(new LinkedList<String>()); //Wrap linkedlist in synchronizedList to handle race condition.
        fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);
    }

    public void setServerOn() throws Exception{

        try
        {
            //Create server and execute Controller to handle event.
            serverSocket = new ServerSocket(serverPort);
            serverOn = true;
            fScheduler.execute(new Controller());
//            System.out.println("Server On!");

            //Wait for connections and keep connection in the member list.
            while(serverOn){

                // Accept incoming connections.
                Socket clientSocket = serverSocket.accept();
                totalClient++;
//                System.out.printf("Client %d connecting.\n", totalClient);

                //Create worker thread to keep socket and handle I/O.
                ClientService clientWorkerService = new ClientService(clientSocket, totalClient);
                memberList.add(clientWorkerService);
                fScheduler.execute(clientWorkerService);

            }

        }
        catch(IOException ioe)
        {
            System.out.println("Could not create server. Quitting.");
            System.exit(-1);
        }finally {
            serverSocket.close();
            System.out.println("Server Stopped");
        }
    }

    /**
    * Inner Class ClientService
    *
    * ClientService is the worker thread to keep and monitor connection status, and help handle I/O.
    *
    * **/
    class ClientService implements Runnable{

        private Socket myClientSocket;
        private boolean workerOn = true;
        private int clientID = 0;
        private List<String> workerMsgQueue = null;

        public ClientService(){
            super();
        }

        public ClientService(Socket s, int id){
            workerMsgQueue = Collections.synchronizedList(new LinkedList<String>());
            clientID = id;
            myClientSocket = s;

        }

        public void putMsgInWorkerQueue(String msg){
            workerMsgQueue.add(msg);
        }

        public void run(){

            // Print out details of this connection 
            System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
            BufferedReader in = null;
            PrintWriter out = null;

            try{

                in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));

                //Send initial message to Client and give client id.
                String initialMsg = "initialization " + String.valueOf(clientID) + "\n";
                out.write(initialMsg);
                out.flush();

                // Run in a loop until workerOn is set to false
                while(workerOn){

                    //Get input from client and put it in server's message queue.
                    if(in.ready()){

                        String clientMsg = in.readLine();
                        clientMsg += " "+String.valueOf(clientID);  //Add this client's ID , then Controller can tell where the msg sent from.
                        serverMsgQueue.add(clientMsg);
                    }

                    //If there is something in worker's message queue, then send it to client.
                    if(!workerMsgQueue.isEmpty()){

                        out.write(workerMsgQueue.remove(0));
                        out.flush();
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    workerOn = false;
                    if(in != null) in.close();
                    if(out != null) out.close();
                    myClientSocket.close();
                    fScheduler.shutdown();
                    System.out.println("...Stopped");
                }
                catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }

    }

    /**
    * Inner Class Controller
    *
    * It is in charge of handling event and managing Treasure.
    *
    * **/
    class Controller implements Runnable{

        private ArrayList<Treasure> treasure = null;


        public Controller(){
            treasure = new ArrayList<Treasure>();
            treasure.add(new Treasure("A", 0));
            treasure.add(new Treasure("B", 0));
            treasure.add(new Treasure("C", 0));
        }

        private Treasure findItem(String target){

            for (Treasure item : treasure) {
                if (item.getName().equals(target)) {
                    return item;
                }
            }
            return null;
        }

        private void releaseItem(String target, int client){

            Treasure item = findItem(target);

            if(item.getOwner() == client){
                item.setOwner(0);
//                System.out.println("Client: " + client + "release item " + target);
            }else{
//                System.out.println("Client: " + client + "do not has item " + target);
            }

        }

        private void getItem(String target, int client){

            Treasure item = findItem(target);

            if(item.getOwner() == 0){
//                System.out.println("GETITEM: " + target + "  " + client);
                item.setOwner(client);
                sendMsgToWorker("YES " + target + "\n", client);
            }else{
                sendMsgToWorker("NO " + target + "\n", client);
            }

        }

        private void sendMsgToWorker(String msg, int Client){

            for (ClientService worker : memberList) {
                if (worker.clientID == Client) {
                    worker.putMsgInWorkerQueue(msg);
                }
            }

        }

        private void printTreasureState(){

            for (Treasure item : treasure) {
                String msg = item.getName() + " ";
                if (item.getOwner() != 0) {
                    msg += "YES " + item.getOwner();
                } else {
                    msg += "NO " + item.getOwner();
                }
                System.out.println(msg);

            }
        }

        @Override
        public void run() {

            //Every 3 seconds, print out treasure state.
            final ScheduledFuture<?> soundAlarmFuture = fScheduler.scheduleWithFixedDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            printTreasureState();
                        }
                    }, 0, 3, TimeUnit.SECONDS
            );

            while(serverOn){
                //If there is something in server's message queue, then handle it.
                if(!serverMsgQueue.isEmpty()){
                    String msg = serverMsgQueue.remove(0);
//                    System.out.println(msg);

                    String command = msg.split(" ")[0];
                    String target = msg.split(" ")[1];
                    int currentClient = Integer.parseInt(msg.split(" ")[2]);

                    if(command.equals("GET")){

                        getItem(target, currentClient);

                    }else if(command.equals("RELEASE")){

                        releaseItem(target, currentClient);

                    }else{

                        System.out.println("Invalid command!!!");
                    }
                }
            }
        }
    }

    /**
     * Inner Class Treasure
     *
     * Server's treasure
     *
     * **/
    class Treasure {

        private String name = null;
        private int owner = 0;

        public Treasure(String name, int owner){
            this.name = name;
            this.owner = owner;
        }

        public String getName(){
            return name;
        }

        public void setOwner(int owner){
            this.owner=owner;
        }

        public int getOwner(){
            return owner;
        }

    }

}

