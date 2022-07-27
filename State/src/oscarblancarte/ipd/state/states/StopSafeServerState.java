package oscarblancarte.ipd.state.states;

import oscarblancarte.ipd.state.Server;

public class StopSafeServerState extends AbstractServerState{

    private Thread monitoringThread;

    public StopSafeServerState(Server server) {
        server.getMessageProcess().start();
        monitoringThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (server.getMessageProcess()
                                .countMessage() == 0) {
                            server.setState(
                                    new StopServerState(server));
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        monitoringThread.start();
    }

    @Override
    public void handleMessage(Server server, String message) {
        System.out.println("Can't send requests,the server making a safe stop with the remaining requests");
    }
}
