package fit.apcs.magicalwheel.server;

import fit.apcs.magicalwheel.server.socket.SocketHandler;

public final class Application {

    public static void main(String[] args) {
        new SocketHandler().runServer();
    }

}
