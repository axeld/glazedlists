/**
 * Glazed Lists
 * http://glazedlists.dev.java.net/
 *
 * COPYRIGHT 2003 O'DELL ENGINEERING LTD.
 */
package ca.odell.glazedlists.net;

// NIO is used for CTP
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.io.*;
// logging
import java.util.logging.*;

/**
 * A CTPConnectionToEstablish models a desired connection. It is a temporary object used
 * by the connection manager to be passed between threads.
 *
 * <p>A CTPConnectionToEstablish is created for each call to the connect() method, and
 * queued until it can be processed by the CTP thread.
 */
class CTPConnectionToEstablish implements CTPRunnable {
     
    private String host;
    private int port;
    private CTPHandler handler;

    /**
     * Create a new CTPConnectionToEstablish.
     */
    public CTPConnectionToEstablish(String host, int port, CTPHandler handler) {
        this.host = host;
        this.port = port;
        this.handler = handler;
    }
    
    /**
     * Establish the connection. This creates a CTPProtocol for the client and
     * registers it with the selector.
     */
    public boolean run(Selector selector) {
        CTPConnection client = null;
        try {
            // prepare a channel to connect
            InetSocketAddress address = new InetSocketAddress(host, port);
            SocketChannel channel = SocketChannel.open();
    
            // configure the channel for no-blocking and selection
            channel.configureBlocking(false);
            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_CONNECT);
    
            // prepare the handler for the connection
            client = CTPConnection.client(host, selectionKey, handler);
            selectionKey.attach(client);

            // connect (non-blocking)
            channel.connect(address);

        } catch(IOException e) {
            handler.connectionClosed(client, e);
        }
        
        return true;
    }
}