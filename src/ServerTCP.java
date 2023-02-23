import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerTCP {
    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Usage: ServerTCP <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);

        ServerSocketChannel listenChannel =
                ServerSocketChannel.open();

        listenChannel.bind(new InetSocketAddress(port));


        while(true){
            SocketChannel serveChannel =
                    listenChannel.accept();

            ByteBuffer buffer = ByteBuffer.allocate(1040);
            serveChannel.read(buffer);
            buffer.flip();
            byte[] bytes = buffer.array();
            String message = new String(bytes);
            String replyMessage = "not working";
            if (message.contains("DE")){
                String newMessage = message.replace("DE", "").replace("\0", "").replace(" ", "");
                if (Files.exists(Path.of("src/" + newMessage))){
                    Files.delete(Path.of("src/" + newMessage));
                    replyMessage = "Done";
                }
                else {
                    replyMessage = "Failed";
                }
            }

            if (message.contains("RE")){
                String newMessage = message.replace("RE", "").replace("\0", "");
                int middle = newMessage.indexOf(" ");
                String newName = newMessage.substring(middle).replace(" ", "");
                String oldName = newMessage.replace(newName, "").replace(" ", "");
                if (Files.exists(Path.of("src/" + oldName))){
                    Files.copy(Path.of("src/" + oldName), Path.of("src/" + newName));
                    Files.delete(Path.of("src/" + oldName));
                    replyMessage = "Done";
                }
                else {
                    replyMessage = "Failed";
                }
            }

            ByteBuffer replyBuffer = ByteBuffer.wrap(replyMessage.getBytes(StandardCharsets.UTF_8));
            replyBuffer.rewind();
            serveChannel.write(replyBuffer);
            serveChannel.shutdownOutput();
        }
    }
}

