package com.twopits.balls;

import java.net.InetAddress;

public interface clientOperation {

    public boolean connectServer(InetAddress serverip);

    public void inputMoves(String moveCode);

}
