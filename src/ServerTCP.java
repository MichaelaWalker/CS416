import java.awt.desktop.FilesEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

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
                Path filePathToDelete = Path.of("src/" + newMessage);
                if (Files.exists(filePathToDelete)){
                    Files.delete(filePathToDelete);
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
                Path originalFile = Path.of("src/" + oldName);
                if (Files.exists(originalFile)){
                    Files.copy(originalFile, Path.of("src/" + newName));
                    Files.delete(originalFile);
                    replyMessage = "Done";
                }
                else {
                    replyMessage = "Failed";
                }
            }

            if (message.contains("LI")){
                File directoryPath = new File("src/");
                FileFilter fileFilter = new FileFilter() {
                    public boolean accept(File pathname) {
                        if (pathname.isFile()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
                File[] list = directoryPath.listFiles(fileFilter);
                ArrayList<String> lists = new ArrayList<>();
                for (File file : list){
                    if (!file.isDirectory()) {
                        lists.add(file.getName() + "\n");
                        System.out.println(file.getName());
                    }
                }
                for (String s : lists) {
                    replyMessage += s;
                }
            }

            if (message.contains("DL")){
                String fileName = message.replace("DL", "").replace(" ", "").replace("\0", "");
                if (Files.exists(Path.of("src/" + fileName))){
                    ArrayList<String> file = (ArrayList<String>) Files.readAllLines(Path.of("src/" + fileName));
                    replyMessage = String.valueOf(file);
                }
            }

            ByteBuffer replyBuffer = ByteBuffer.wrap(replyMessage.getBytes(StandardCharsets.UTF_8));
            replyBuffer.rewind();
            serveChannel.write(replyBuffer);
            serveChannel.shutdownOutput();
        }
    }
}

