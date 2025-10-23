package t02_ordering.demos.de01;

public class IPAddress
{
    //192.168.10.54
    private int ipA, ipB, ipC, ipD;

    public IPAddress(String strIPAddress)
    {
        strIPAddress.trim(); //white space
        String[] parts = strIPAddress.split(".");
        initializeParts(parts);
    }

    private void initializeParts(String[] parts)
    {
        //if int, then assign to part, if not then assign default (throw exception)
    }

    //toString
    //getters (immutable)
}
