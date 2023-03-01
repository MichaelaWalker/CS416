import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        if (input.equals("DL")){
            String fileName = inputToSend.replace("DL", "").replace(" ", "");
            Path of = Path.of(fileName);
            Files.createFile(of);
            //BufferedReader reader = new BufferedReader(new FileReader(fileName));
            if (sc.read(replyBuffer) != -1){
            try{
                BufferedReader in = new BufferedReader(new FileReader(sc.toString()));
                String contentLine = in.readLine();
                while (contentLine != null){
                    contentLine = in.readLine();
                    if (contentLine != null){
                        Files.write(of, contentLine.getBytes());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
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
}
