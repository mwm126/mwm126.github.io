class CircularQueue {
	//data fields
	private double[] items;
	private int pointer;//points to "first" item

	//constructor
	CircularQueue(int init_size) {
		items = new double[init_size];
		pointer = init_size - 1;
	}
	//methods
	public int getSize() {
		return items.length;
	}
	
	public void addItem(double dub) {
		pointer--;
		pointer = (pointer + items.length)%items.length;
		try {
		items[pointer] = dub;
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			System.out.println("!@#!@#!@#ArrayIndexOutOfBoundsException, pointer = "+pointer+" , items.length = "+items.length);
		}
	}
	
	public double getItem(int location) {
		location = Math.max(location,0);
		location = Math.min(location,items.length-1);
		location = (location + pointer) % items.length;
		try {
			return items[location];
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			System.out.println("!@#!@#!@#ArrayIndexOutOfBoundsException, pointer = "+pointer+" , items.length = "+items.length);
		}
		return 42;
	}
}
