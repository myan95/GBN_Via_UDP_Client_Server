/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_client;

/**
 *
 * @author Myan
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static byte[] toBytes(Object obj) throws IOException {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);

        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {

        ByteArrayInputStream b = new ByteArrayInputStream(bytes);

        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
