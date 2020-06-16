package PackageClient;

import PackageClient.Application;

public class Client {
    public static void main(String[] args) {
        Application application = new Application(args);
        application.run();
    }
}
