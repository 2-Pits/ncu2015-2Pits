package ClientModule;

import com.google.gson.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Client implements Runnable, clientOperation{

    private InetAddress serverIp;
    static final int PORT_NUMBER = 11111;
    private BufferedReader inFromUser;
    PrintWriter outToServer;
    BufferedReader inFromServer;
    Socket clientSocket = null;
    private static ScheduledExecutorService fScheduler = null;
    private static final int NUM_THREADS = 1;
    DOMInterface domOperation;

    private ArrayList<GameData> treasure = null;
    int clientID = 0;

    private enum MoveCode {
        TURNEAST, TURNSOUTH, TURNNORTH, TURNWEST, GET
    }

    public Client(DOMInterface domOperation){
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);
        this.domOperation = domOperation;   //Initial DOM operation.
    }

    @Override
    public boolean connectServer(InetAddress serverip) {
        try {


            this.serverIp = serverip;
            clientSocket = new Socket(serverip.getHostAddress() , PORT_NUMBER);
            outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            if(clientSocket.isConnected()) {

                runClient();
                return true;    //If it is here, it means work is finished
            }

        }catch(IOException IOE){
            System.out.println(IOE.toString());
        }

        return false;   //It means connection fail.
    }
    /**
     * Class inputMoves
     *
     * UIM or DOM call this method to send moveCode to sever.
     *
     * **/
     @Override
     public void inputMoves(String moveCode) {

         GameData data = new GameData(moveCode);

        //Wrap moveCode into Gson object
        Gson gson =  new Gson();
        String json = gson.toJson(data);
//        System.out.println("show : " + json);

        //Send it to server
//        System.out.println("out to server");
        outToServer.print(json + "\n");
        outToServer.flush();

    }

//    private GameData findItem(String target){
//
//        for (GameData item : treasure) {
//            if (item.command.equals(target)) {
//                return item;
//            }
//        }
//        return null;
//    }
//
//    private void getTreasure(String target){
//
//        String msgToServer = "GET " + target + "\n";
//        outToServer.write(msgToServer);
//        outToServer.flush();
//
////        System.out.println("ClientModule.Client: " + clientID + " GET " + findItem(target).command);
//    }
//
//    private void releaseTreasure(String target){
//
//        String msgToServer = "RELEASE " + target + "\n";
//        outToServer.write(msgToServer);
//        outToServer.flush();
////        System.out.println("ClientModule.Client: " + clientID + " RELEASE " + findItem(target).command);
//
//    }
//
//    private void updateItemTimeLeft(){
//
//        for (GameData item : treasure) {
//            if (item.timeLeft != 0) {
//                item.timeLeft = item.timeLeft - 1;
//                if(item.timeLeft == 0){
//                    item.setOwn(false);
//                    releaseTreasure(item.command);
//                }
//            }
//        }
//    }
//
//    private void printTreasureState(){
//
//        String msg = "ClientModule.Client " + clientID + "\n";
//        for (GameData item : treasure) {
//            if (item.isOwn()) {
//                msg += item.command + " YES " + item.timeLeft + "\n";
//            } else {
//                msg += item.command + " NO" + "\n";
//            }
//        }
//
//        System.out.println(msg);
//    }

    private void runClient() throws IOException{

//      Server will send initialized id to this client.
        String initialMsg = inFromServer.readLine();
        System.out.println(initialMsg);
        Player player = new Gson().fromJson(initialMsg, Player.class);
        domOperation.initMyCharacter(player.getID(), player.getX(), player.getY());

        //Start periodic tasks.
//        final ScheduledFuture<?> alarmFuture = fScheduler.scheduleWithFixedDelay(new ScheduledTask(), 0, 1, TimeUnit.SECONDS);

        /**Receive data from server**/
//        while(true){
//            if (inFromServer.ready()) {
                //Read message and divide it to be "target" and "response".
//                String message = inFromServer.readLine();
//                String target = message.split(" ")[1];
//                String response = message.split(" ")[0];
//                Item item = findItem(target);
//
//                if (response.equals("YES")) {
//
//                    item.setOwn(true);
//                    item.setTimeLeft(5);
//
////                    System.out.println("ClientModule.Client: " + clientID + " Item: " + target + item.isOwn());
//
//                }else if(response.equals("NO")){
//
//                    item.setOwn(false);
////                    System.out.println("ClientModule.Client: " + clientID + " Item: " + target + item.isOwn());
//                }else{
//
//                    System.out.println("Invalid Response!!!");
//                }
//            }
//        }

    }

    @Override
    public void run() {
        try {
            if(!connectServer(serverIp)){

                System.out.printf("Connection fail!!!");

            }

        }catch(Exception e){
            System.out.println(e);
        }

    }


//    /**
//     * Inner Class ScheduledTask
//     *
//     * ScheduledTask is in charge of periodic task.
//     *
//     * **/
//    class ScheduledTask implements Runnable{
//
//        private int counter = 0;
//        private String target = null;
//        private GameData currentGettedItem = null;
//        private int itemIndex = 0;
//
//        @Override
//        public void run() {
//
//            //Update item's time and release item if its left time is 0.
//            updateItemTimeLeft();
//
//            //Get treasure every seconds.
//            itemIndex = counter % 3;
//            currentGettedItem = treasure.get(itemIndex);    //Question: if I have current item, then skip it or get next?
//            if(!currentGettedItem.isOwn()){
//                target = currentGettedItem.getCommand();
//                getTreasure(target);
//            }
//
//            //Print state every 3 seconds.
//            if((counter % 3) == 0){
//                printTreasureState();
//            }
//
//            counter++;
//        }
//    }

//    public static void main(String[] args) throws Exception{
//        ClientModule.Client clientA = new ClientModule.Client("127.0.0.1");
//        Thread clientThreadA = new Thread(clientA);
//        clientThreadA.start();
//
//        ClientModule.Client clientB = new ClientModule.Client("127.0.0.1");
//        Thread clientThreadB = new Thread(clientB);
//        clientThreadB.start();
//
//    }

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

}

