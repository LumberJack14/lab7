package Utils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseChannelType {
    private ByteBuffer responseBuf;
    private DatagramChannel dc;
    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public ByteBuffer getResponseBuf() {
        return responseBuf;
    }

    public void setResponseBuf(ByteBuffer responseBuf) {
        this.responseBuf = responseBuf;
    }

    public DatagramChannel getDc() {
        return dc;
    }

    public void setDc(DatagramChannel dc) {
        this.dc = dc;
    }
}
