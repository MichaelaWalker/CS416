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
        System.out.println("Enter DL to download, UL to upload, DE to delete, or RE to rename");
        String input = scanner.nextLine();
        String inputToSend = "";
        if (input.equals("DL")){
            System.out.println("Enter file name with extension that you want to delete");
            inputToSend = "DL " + scanner.nextLine();
        }
        else if (input.equals("UL")){
            System.out.println("Enter file name with extension that you want to upload");
            inputToSend = "UL " + scanner.nextLine();
        }
        else if (input.equals("DE")){
            System.out.println("Enter the file name with extension you want to delete");
            inputToSend = "DE " + scanner.nextLine();
        }
        else if (input.equals("RE")){
            System.out.println("Enter the name of the file you want to rename");
            String originalName = scanner.nextLine();
            System.out.println("Enter the new name of the file");
            String newName = scanner.nextLine();
            inputToSend = "RE" + originalName + " " + newName;
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
        ByteBuffer replyBuffer = ByteBuffer.allocate(1040);
        replyBuffer.clear();
        replyBuffer.rewind();
        sc.read(replyBuffer);
        sc.close();
        replyBuffer.flip();
        byte[] bytes = replyBuffer.array();
        String replyMessage = new String(bytes);
        replyMessage = replyMessage.replace("\0", "");
        System.out.println(replyMessage);

    }
}
