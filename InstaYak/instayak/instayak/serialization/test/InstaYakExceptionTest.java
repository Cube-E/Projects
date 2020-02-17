/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization.test;

import instayak.serialization.InstaYakException;
import org.junit.Assert;
import org.junit.Test;

public class InstaYakExceptionTest {
    public InstaYakExceptionTest() {
    }

    @Test
    public void testFoodNetworkExceptionConstructor() {
        String testMsg = "msg";
        InstaYakException e = new InstaYakException("msg", (Throwable)null);
        Assert.assertEquals(new String("msg"), e.getMessage());
        Assert.assertNull(e.getCause());
    }

    @Test
    public void testFoodNetworkExceptionConstructorCause() {
        String testMsg = "msg";
        Exception testEx = new Exception();
        InstaYakException e = new InstaYakException("msg", testEx);
        Assert.assertEquals(new String("msg"), e.getMessage());
        Assert.assertEquals(testEx, e.getCause());
    }
}
