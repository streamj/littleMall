package cc.freecloudfx.littlemall;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author StReaM on 3/14/2018
 */
public class SerializationTest {

    static class Serialize implements Serializable {
        private static final long serialVersionUID = Long.MIN_VALUE;
        public int num = 1390;
    }

    @Test
    public void testment() {
        try {
            FileOutputStream fos = new FileOutputStream("d:/serialize.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Serialize serialize = new Serialize();
            oos.writeObject(serialize);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
