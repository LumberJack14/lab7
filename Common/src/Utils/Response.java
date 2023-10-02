package Utils;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class Response implements Serializable {

    private ResponseStatus status;
    private String data;
    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public Response(ResponseStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status.toString();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
