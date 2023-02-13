import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TCP_Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 2){
            System.out.println("Syntax: TCP_Client <Server IP> <Server Port>");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a Message");




        String message = scanner.nextLine();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(serverIP, serverPort));
        buffer.rewind();
        sc.write(buffer);
        sc.shutdownOutput();
//reading response
        buffer.clear();
        sc.read(buffer);
        sc.close();
        buffer.flip();
        byte[] bytes = buffer.array();
        System.out.println(new String(bytes));

    }
}
