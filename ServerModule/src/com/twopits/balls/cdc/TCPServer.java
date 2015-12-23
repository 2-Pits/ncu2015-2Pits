package com.twopits.balls.cdc;

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
    CentralizedDataCenterInterface cdcOperation;

    private enum MoveCode {
        TURNEAST, TURNSOUTH, TURNNORTH, TURNWEST, GET
    }

    /**
     * TCPServer constructor
     *
     * @param cdcOperation  The interface of CDC
     * **/
    public TCPServer(CentralizedDataCenterInterface cdcOperation) throws Exception{
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
                Player player = cdcOperation.addPlayer();


                // Print out and collect IP of this connection
                String clientIP = clientSocket.getInetAddress().getHostName();
                System.out.println("Accepted main.ClientModule.Client Address - " + clientIP);
                clientIPTable.add(clientIP);

                //Create worker thread to keep socket and handle I/O.
                ClientService clientWorkerService = new ClientService(clientSocket, player);
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
        Player player;

        /**
         * ClientService constructor
         *
         * @param myClientSocket client's socket
         * @param player client's id
         * **/
        public ClientService(Socket myClientSocket, Player player) throws Exception{
            this.player = player;
            in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));
            workerMsgQueue = Collections.synchronizedList(new LinkedList<String>());
            clientID = player.getClientno();
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

        public Controller(){

        }

        private void sendMsgToWorker(String msg, int clientID){

            for (ClientService worker : memberList) {
                if (worker.clientID == clientID) {
                    worker.putMsgInWorkerQueue(msg);
                }
            }

        }

        @Override
        public void run() {

            while(serverOn){

                //If there is something in server's message queue, then handle it.
                if(!serverMsgQueue.isEmpty()){
                    GameData data = serverMsgQueue.remove(0);

//                    String command = msg.split(" ")[0];
//                    String target = msg.split(" ")[1];
//                    int currentClient = Integer.parseInt(msg.split(" ")[2]);
                    MoveCode moveCode = MoveCode.valueOf(data.getCommand());

                    System.out.println("The command is : " + data.getCommand());

//                    switch (moveCode){
//                        case TURNEAST:
//                            cdcOperation.updateDirection(0, moveCode.ordinal());
//                            break;
//                        case TURNNORTH:
//                            cdcOperation.updateDirection(0, moveCode.ordinal());
//                            break;
//                        case TURNSOUTH:
//                            cdcOperation.updateDirection(0, moveCode.ordinal());
//                            break;
//                        case TURNWEST:
//                            cdcOperation.updateDirection(0, moveCode.ordinal());
//                            break;
//                        case GET:
//                            cdcOperation.getItem(0);
//                            break;
//                    }
                }
            }
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
//        main.ServerModule.TCPServer server = new main.ServerModule.TCPServer(11111);
//        server.initTCPServer();
//    }

}

