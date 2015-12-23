package com.twopits.balls;

import com.google.gson.*;
import com.twopits.balls.dom.DOMInterface;

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



    private void runClient() throws IOException{

//      Server will send initialized id to this client.
        String initialMsg = inFromServer.readLine();
        System.out.println(initialMsg);
        Player player = new Gson().fromJson(initialMsg, Player.class);
        domOperation.initMyCharacter(player.getClientno(), player.getX(), player.getY());

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
////                    System.out.println("main.ClientModule.Client: " + clientID + " Item: " + target + item.isOwn());
//
//                }else if(response.equals("NO")){
//
//                    item.setOwn(false);
////                    System.out.println("main.ClientModule.Client: " + clientID + " Item: " + target + item.isOwn());
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

