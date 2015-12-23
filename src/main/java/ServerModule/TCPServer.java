package ServerModule;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;


public class TCPServer implements serverOperation{

    private static ScheduledExecutorService fScheduler = null;
    private static final int NUM_THREADS = 4;
    private ArrayList<ClientService> memberList = null;
    private ArrayList<String> clientIPTable = null;
    private List<GameData> serverMsgQueue = null;
    private ServerSocket serverSocket;
    private boolean serverOn = false;
    private int totalClient = 0;
    CDCOperation cdcOperation;

    private enum MoveCode {
        TURNEAST, TURNSOUTH, TURNNORTH, TURNWEST, GET
    }

    /**
     * TCPServer constructor
     *
     * @param cdcOperation  The interface of CDC
     * **/
    public TCPServer(CDCOperation cdcOperation) throws Exception{
        this.cdcOperation = cdcOperation;
        memberList = new ArrayList<ClientService>();    //Collect client's socket
        clientIPTable = new ArrayList<String>();    //Collect client's IP
        serverMsgQueue = Collections.synchronizedList(new LinkedList<GameData>()); //Wrap linkedlist in synchronizedList to handle race condition.
        fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);
        fScheduler.execute(new Controller());   //start controller
    }

    /**
     * initTCPServer()
     *
     * Initialize server socket and wait for connection.
     *
     * **/
    @Override
    public void initTCPServer() throws IOException{

        try
        {
            //Create server and execute Controller to handle event.
            serverSocket = new ServerSocket(11111);
            serverOn = true;

            //Wait for connections and keep connection in the member list.
            while(serverOn){

                // Accept incoming connections.
                Socket clientSocket = serverSocket.accept();
                totalClient++;

                /**
                 * Initial data of client(id , position and so on...)
                 * **/
                int clientID = cdcOperation.addCharacter();

                // Print out and collect IP of this connection
                String clientIP = clientSocket.getInetAddress().getHostName();
                System.out.println("Accepted ClientModule.Client Address - " + clientIP);
                clientIPTable.add(clientIP);

                //Create worker thread to keep socket and handle I/O.
                ClientService clientWorkerService = new ClientService(clientSocket, clientID);
                memberList.add(clientWorkerService);
                fScheduler.execute(clientWorkerService);

            }



        }catch(Exception ee)
        {
            ee.printStackTrace();
            System.out.println("Could not create server. Quitting.");
        }
//        finally{
//            serverSocket.close();
////            fScheduler.shutdown();
//            System.out.println("Server Stopped");
//        }
    }

    /**
     * stop()
     *
     * stop server, client connection and scheduler.
     *
     * **/
    public void stop() {

        try {
            for(ClientService client : memberList){
                client.stop();
            }
            serverSocket.close();
            fScheduler.shutdown();
            System.out.println("Server Stopped");
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    /**
     * getClientIPTable()
     *
     * give client's IP table to be used to broadcast.
     * **/
    @Override
    public ArrayList<String> getClientIPTable() {
        return clientIPTable;
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
        private BufferedReader in = null;
        private PrintWriter out = null;

        /**
         * ClientService constructor
         *
         * @param myClientSocket client's socket
         * @param id client's id
         * **/
        public ClientService(Socket myClientSocket, int id) throws Exception{
            in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));
            workerMsgQueue = Collections.synchronizedList(new LinkedList<String>());
            clientID = id;
        }

        /**
         * putMsgInWorkerQueue()
         *
         * The message sent to remote client must be put in workerMsgQueue
         *
         * @param msg The message to be sent.
         * **/
        public void putMsgInWorkerQueue(String msg){
            workerMsgQueue.add(msg);
        }

        private void sendInitialData(){
            Player player = cdcOperation.findPlayer(clientID);
            String playerData = new Gson().toJson(player);
            System.out.println(playerData);
            out.print(playerData + "\n");
            out.flush();
        }

        /**
         * stop()
         *
         * stop clientService socket, I/O.
         * **/
        public void stop() {

            try {
                workerOn = false;
                if (in != null) in.close();
                if (out != null) out.close();
                if(myClientSocket != null) myClientSocket.close();
                System.out.println("...Stopped");
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         * run()
         *
         * Read message, and put it in server message queue.
         * **/
        public void run(){

            try{

                /**Give initial player data to remote**/
                sendInitialData();


                // Run in a loop until workerOn is set to false
                while(workerOn){

                    //Get input from client and put it in server's message queue.
                    if(in.ready()){

/**get Gson object from client, then put it in server message queue.**/
                        //According to moveCode, finding which action to do.
                        String jsonMsg = in.readLine();
                        GameData data = new Gson().fromJson(jsonMsg, GameData.class);
                        serverMsgQueue.add(data);

                    }

                    if(!workerMsgQueue.isEmpty()){
                        out.print(workerMsgQueue.remove(0));
                        out.flush();
                    }

                }

            }catch(Exception e){
                e.printStackTrace();
            }
//            finally{
//                try{
//                    workerOn = false;
//                    if(in != null) in.close();
//                    if(out != null) out.close();
//                    myClientSocket.close();
////                    fScheduler.shutdown();
//                    System.out.println("...Stopped");
//                }
//                catch(IOException ioe){
//                    ioe.printStackTrace();
//                }
//            }
        }

    }

    /**
    * Inner Class Controller
    *
    * It is in charge of handling event and managing Treasure.
    *
    * **/
    public class Controller implements Runnable{

        private ArrayList<Treasure> treasure = null;


        public Controller(){
//            treasure = new ArrayList<Treasure>();
//            treasure.add(new Treasure("A", 0));
//            treasure.add(new Treasure("B", 0));
//            treasure.add(new Treasure("C", 0));
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
//                System.out.println("ClientModule.Client: " + client + "release item " + target);
            }else{
                System.out.println("ClientModule.Client: " + client + "do not has item " + target);
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

        private void sendMsgToWorker(String msg, int clientID){

            for (ClientService worker : memberList) {
                if (worker.clientID == clientID) {
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

//            //Every 3 seconds, print out treasure state.
//            final ScheduledFuture<?> soundAlarmFuture = fScheduler.scheduleWithFixedDelay(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("============================");
//                            printTreasureState();
//                        }
//                    }, 0, 3, TimeUnit.SECONDS
//            );

            while(serverOn){

                //If there is something in server's message queue, then handle it.
                if(!serverMsgQueue.isEmpty()){
                    GameData data = serverMsgQueue.remove(0);

//                    String command = msg.split(" ")[0];
//                    String target = msg.split(" ")[1];
//                    int currentClient = Integer.parseInt(msg.split(" ")[2]);
                    MoveCode moveCode = MoveCode.valueOf(data.getCommand());

                    System.out.println("The command is : " + data.getCommand());

                    switch (moveCode){
                        case TURNEAST:
                            cdcOperation.updateDirection(0, moveCode.ordinal());
                            break;
                        case TURNNORTH:
                            cdcOperation.updateDirection(0, moveCode.ordinal());
                            break;
                        case TURNSOUTH:
                            cdcOperation.updateDirection(0, moveCode.ordinal());
                            break;
                        case TURNWEST:
                            cdcOperation.updateDirection(0, moveCode.ordinal());
                            break;
                        case GET:
                            cdcOperation.getItem(0);
                            break;
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

    /**
     * Inner Class GameData
     *
     * GameData will be transformed to json, then passed to remote
     *
     * **/
    class GameData {

        private String command;


        public GameData(String command){
            this.command = command;
        }

        public String getCommand(){
            return command;
        }

        public void setCommand(String command){
            this.command = command;
        }

    }

//    public static void main(String[] args) throws Exception {
//        ServerModule.TCPServer server = new ServerModule.TCPServer(11111);
//        server.initTCPServer();
//    }

}

