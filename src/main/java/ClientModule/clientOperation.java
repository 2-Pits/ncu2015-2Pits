package ClientModule;

import java.net.InetAddress;

/**
 * Created by bighead on 2015/12/17.
 */
public interface clientOperation {

    public boolean connectServer(InetAddress serverip);

    public void inputMoves(String moveCode);

}
