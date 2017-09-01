package fr.coppernic.samples.gsmr;

/**
 * Created by benoist on 28/08/17.
 */

public class Data {
    private byte[] in;
    private byte[] out;

    public Data(byte[] in, byte[] out) {
        this.in = in;
        this.out = out;
    }

    public byte[] getIn() {
        return in;
    }

    public byte[] getOut() {
        return out;
    }
}
