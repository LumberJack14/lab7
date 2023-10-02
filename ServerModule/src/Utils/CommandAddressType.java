package Utils;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class CommandAddressType {
    private CommandWrapper cm;
    private InetSocketAddress address;
    private DatagramChannel serv;

    public DatagramChannel getServ() {
        return serv;
    }

    public void setServ(DatagramChannel serv) {
        this.serv = serv;
    }

    public CommandAddressType() {
    }

    public CommandWrapper getCm() {
        return cm;
    }

    public void setCm(CommandWrapper cm) {
        this.cm = cm;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
