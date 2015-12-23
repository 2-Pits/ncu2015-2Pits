package ServerModule;

import java.io.IOException;
import java.util.ArrayList;

public interface serverOperation {

    public void initTCPServer() throws IOException;

    public ArrayList<String> getClientIPTable();

}
