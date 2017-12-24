import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class TestOffHeap {

	private static Unsafe unsafe = null;

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
		long startIndex = unsafe.allocateMemory(4);
		unsafe.putInt(startIndex, 245);
		OffHeapObject o = new OffHeapObject(startIndex);
		System.out.println(o.getIntField());
	}

	private static class OffHeapObject {
		
		private long objectOffset;

		public OffHeapObject(long objectOffset) {
			this.objectOffset = objectOffset;
		}

		public int getIntField() {
			return unsafe.getInt(objectOffset);
		}

		public void setIntField(int intField) {
			unsafe.putInt(objectOffset, intField);
		}
	}
}
