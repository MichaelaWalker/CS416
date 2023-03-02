import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        System.out.println("Enter DL to download, UL to upload, DE to delete, RE to rename, or LI to list all files");
        String input = scanner.nextLine();
        String inputToSend = "";
        switch (input) {
            case "DL" -> {
                System.out.println("Enter file name with extension that you want to download");
                inputToSend = "DL " + scanner.nextLine();
            }
            case "UL" -> {
                System.out.println("Enter file name with extension that you want to upload");
                inputToSend = "UL " + scanner.nextLine();
            }
            case "DE" -> {
                System.out.println("Enter the file name with extension you want to delete");
                inputToSend = "DE " + scanner.nextLine();
            }
            case "RE" -> {
                System.out.println("Enter the name of the file with extension you want to rename");
                String originalName = scanner.nextLine();
                System.out.println("Enter the new name of the file");
                String newName = scanner.nextLine();
                inputToSend = "RE" + originalName + " " + newName;
            }
            case "LI" -> {
                inputToSend = "LI";
            }
            default -> System.out.println("That is not a valid input");
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
        if (input.equals("DL")) {
            String fileName = inputToSend.replace("DL", "").replace(" ", "");
            File file = new File(System.getProperty("user.dir") + fileName);
            writeByteArrayToFile(bytes, file);
        }
        else {
            String replyMessage = new String(bytes);
            replyMessage = replyMessage.replace("\0", "");
            System.out.println(replyMessage);
        }
    }
    public static void writeByteArrayToFile(byte[] bytes, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
    }
}
