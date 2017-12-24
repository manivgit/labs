import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class TestFieldOffset {

	private static Unsafe unsafe;

	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		
		TestFieldOffsetInner obj = new TestFieldOffsetInner();
		obj.setValue(200);
		System.out.println(obj.getValue());

		CASCounter c = new CASCounter();
		c.increment();
	}

	public static Unsafe getUnsafe() {
		return unsafe;
	}
}

class TestFieldOffsetInner {
	
	private int value = 0;
	private long offset;
	private Unsafe unsafe;

	public TestFieldOffsetInner() throws Exception {
		unsafe = TestFieldOffset.getUnsafe();
		offset = unsafe.objectFieldOffset(TestFieldOffsetInner.class.getDeclaredField("value"));
	}

	public void setValue(int value) {
		unsafe.putInt(this, offset, value);
	}

	public int getValue() {
		try {
			System.out.println("Address: " + offset);
			return unsafe.getInt(this, offset);
		} catch (Exception e) {
			return -1;
		}
	}
}

class CASCounter {
    private volatile long counter = 0;
    private Unsafe unsafe;
    private long offset;

    public CASCounter() throws Exception {
        unsafe = TestFieldOffset.getUnsafe();
        offset = unsafe.objectFieldOffset(CASCounter.class.getDeclaredField("counter"));
    }

    public void increment() {
        long before = counter;
        while (!unsafe.compareAndSwapLong(this, offset, before, before + 1)) {
            before = counter;
        }
    }

    public long getCounter() {
        return counter;
    }
}
