package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class ServerConnectionManager {

    private static final int PORT = 6789;
    private static final int BUFFER_SIZE = 2048;


    public void receiveConnections() {
        // setting up the environment
        Selector selector = null;
        DatagramChannel dc = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        ExecutorService readingExecutor = Executors.newFixedThreadPool(5);
        ArrayList<Future<CommandAddressType>> readFutures = new ArrayList<>();
        ExecutorService performingExecutor = Executors.newFixedThreadPool(5);
        ArrayList<Future<ResponseChannelType>> performFutures = new ArrayList<>();
        ExecutorService replyingExecutor = Executors.newCachedThreadPool();

        try {
            dc = DatagramChannel.open();
            SocketAddress address = new InetSocketAddress(PORT);
            dc.configureBlocking(false);
            dc.bind(address);
            selector = Selector.open();
            dc.register(selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            System.out.println("Error while setting up the environment");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        //working with connections
        try {
            while (true) {
                if (bufferedReader.ready()) {
                    String str = bufferedReader.readLine();
                    if (str.equals("stop")) {
                        break;
                    } else {
                        System.out.println("Unknown command");
                    }
                }

                int count = selector.selectNow();
                if (!(count == 0)) {

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter;
                    for (iter = keys.iterator(); iter.hasNext(); ) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isValid()) {
                            if (key.isReadable()) {
                                DatagramChannel serv = (DatagramChannel) key.channel();
                                ByteBuffer buf = ByteBuffer.wrap(new byte[BUFFER_SIZE]);
                                InetSocketAddress address = (InetSocketAddress) serv.receive(buf);

                                Callable<CommandAddressType> newRead = () -> {
                                    System.out.println("reading");
                                    CommandAddressType cat = new CommandAddressType();
                                    CommandWrapper cm = QueryValidator.readCommand(buf);
                                    cat.setCm(cm);
                                    cat.setAddress(address);
                                    cat.setServ(serv);
                                    return cat;
                                };
                                Future<CommandAddressType> newFuture = readingExecutor.submit(newRead);
                                readFutures.add(newFuture);
                            }

                        }
                    }
                }

                //Cycling threads that read queries and start executing command
                Iterator<Future<CommandAddressType>> iterator1 = readFutures.iterator();
                while (iterator1.hasNext()) {
                    Future<CommandAddressType> future = iterator1.next();
                    if (future.isDone()) {
                        CommandAddressType cat = future.get();

                        Callable<ResponseChannelType> newPerform = () -> {
                            System.out.println("performing");
                            ResponseChannelType rct = new ResponseChannelType();
                            ByteBuffer responseBuf = QueryValidator.ExecuteCommand(cat.getCm());
                            rct.setAddress(cat.getAddress());
                            rct.setDc(cat.getServ());
                            rct.setResponseBuf(responseBuf);
                            return rct;
                        };

                        Future<ResponseChannelType> newFuture = performingExecutor.submit(newPerform);
                        performFutures.add(newFuture);

                        iterator1.remove();
                    }
                }

                //Cycling threads that execute (perform) commands on the server
                Iterator<Future<ResponseChannelType>> iterator2 = performFutures.iterator();
                while (iterator2.hasNext()) {
                    Future<ResponseChannelType> future = iterator2.next();
                    if (future.isDone()) {
                        ResponseChannelType rct = future.get();
                        Runnable newReply = () -> {
                            System.out.println("sending");
                            DatagramChannel serv = rct.getDc();
                            ByteBuffer responseBuf = rct.getResponseBuf();
                            InetSocketAddress address = rct.getAddress();
                            try {
                                serv.send(responseBuf, address);
                                serv.configureBlocking(false);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        };
                        replyingExecutor.submit(newReply);
                        iterator2.remove();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while cycling connections");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            selector.close();
            dc.close();
            readingExecutor.shutdown();
            performingExecutor.shutdown();
            replyingExecutor.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
