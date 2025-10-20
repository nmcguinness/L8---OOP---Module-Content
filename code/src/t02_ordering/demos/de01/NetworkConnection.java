package t02_ordering.demos.de01;


/*
 Represents network connection. It is IMMUTABLE.
 */
public class NetworkConnection //implements Comparable<NetworkConnection>
{
    /*
    @Override
    public int compareTo(NetworkConnection o) {

       // return Float.compare(this.pingMs, o.pingMs);

        float delta = this.pingMs - o.getPingMs();
        if(delta == 0)
            return 0;
        else if (delta > 0)
            return 1;
        else
            return -1;
    }
*/

    //region Fields
    private String ipAddress;
    private int port;
    private String protocol;  //REFACTOR
    private float pingMs;
    //endregion

    //region Accessors
    public float getPingMs() {
        return pingMs;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    //endregion

    //region Constructors
    public NetworkConnection(String ipAddress, int port, String protocol) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.protocol = protocol;
    }
    //endregion

    //region Core
    public void ping()
    {
        this.pingMs = Math.round(Math.random() * (100-10) + 10);
    }
    //endregion


    //region Housekeeping
    //Annotations start with @
    @Override
    public String toString() {
        return "NetworkConnection{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", protocol='" + protocol + '\'' +
                ", pingMs=" + pingMs +
                '}';
    }


    //endregion
}
