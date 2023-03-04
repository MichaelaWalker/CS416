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
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(serverIP, serverPort));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter DL to download, UL to upload, DE to delete, RE to rename, or LI to list all files");
        String input = scanner.nextLine();
        String inputToSend = "";
        switch (input) {
            case "DL" -> {
                System.out.println("Enter file name with extension that you want to download");
                inputToSend = "DL " + scanner.nextLine();
                ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
                buffer.rewind();
                sc.write(buffer);
                sc.shutdownOutput();
            }

            case "UL" -> {
                System.out.println("Enter file name directory that you want to upload");
                String fileName = scanner.nextLine();
                Path files = Path.of(fileName);
                inputToSend = "UL " + fileName;
                ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
                buffer.rewind();
                sc.write(buffer);
                File file = new File(files.toUri());
                sc.write(ByteBuffer.wrap(fileToByteArray(file)));
            }

            case "DE" -> {
                System.out.println("Enter the file name with extension you want to delete");
                inputToSend = "DE " + scanner.nextLine();
                ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
                buffer.rewind();
                sc.write(buffer);
                sc.shutdownOutput();
            }

            case "RE" -> {
                System.out.println("Enter the name of the file with extension you want to rename");
                String originalName = scanner.nextLine();
                System.out.println("Enter the new name of the file");
                String newName = scanner.nextLine();
                inputToSend = "RE" + originalName + " " + newName;
                ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
                buffer.rewind();
                sc.write(buffer);
                sc.shutdownOutput();
            }

            case "LI" -> {
                inputToSend = "LI";
                ByteBuffer buffer = ByteBuffer.wrap(inputToSend.getBytes(StandardCharsets.UTF_8));
                buffer.rewind();
                sc.write(buffer);
                sc.shutdownOutput();
            }

            default -> System.out.println("That is not a valid input");
        }
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
    public static byte[] fileToByteArray(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }
}
