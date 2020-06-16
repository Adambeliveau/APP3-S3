package PackageClient;

import PackageClient.Application;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        Application application = new Application(args);
        application.run();
    }
}
