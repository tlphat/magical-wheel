package fit.apcs.magicalwheel.server;

import fit.apcs.magicalwheel.server.connection.Server;

public final class Application {

    public static void main(String[] args) {
        Server.getInstance().run();
    }

}
