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
        System.out.println("Enter DL to download, UL to upload, DEL to delete, or RE to rename");
        String input = scanner.nextLine();
        String inputToSend = input + " ";
        if (input.equals("DL")){
            System.out.println("Enter file name with extension that you want to delete");
            inputToSend += scanner.nextLine();
        }
        if (input.equals("UL")){
            System.out.println("Enter file name with extension that you want to upload");
            inputToSend += scanner.nextLine();
        }
        if (input.equals("DEL")){
            System.out.println("Enter the file name with extension you want to delete");
            inputToSend += scanner.nextLine();
        }
        if (input.equals("RE")){
            System.out.println("Enter the name of the file you want to rename");
            String originalName = scanner.nextLine();
            System.out.println("Enter the new name of the file");
            String newName = scanner.nextLine();
            inputToSend += originalName + " " + newName;
        }
        else {
            System.out.println("That is not a valid input");
        }

        ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
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
