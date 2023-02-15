

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            serveChannel.read(buffer);
            buffer.flip();
            byte[] bytes = buffer.array();
            String message = new String(bytes);
            File[] fileList = File.listRoots();
            System.out.println(message);
            }

//            buffer.rewind();
//            serveChannel.write(buffer);
//            serveChannel.close();
        //}
    }
}

