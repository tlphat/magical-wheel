package fit.apcs.magicalwheel.server;

import fit.apcs.magicalwheel.server.socket.Controller;

public final class Application {

    public static void main(String[] args) {
        new Controller().runServer();
    }

}
