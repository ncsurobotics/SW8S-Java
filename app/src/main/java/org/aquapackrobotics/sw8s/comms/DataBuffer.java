/**
 * 
 */
package org.aquapackrobotics.sw8s.comms;

/**
 * Data Buffer is used to store data (of type float) that is received quickly in order to filter out outliers.
 * @author vaibh
 *
 */
public class DataBuffer {
	
	private static int BUFFER_CAPACITY = 10;
	
	private float[] buffer;
	
	private int front;
	
	private int rear;
	
	private int size;
	
	private float sum;
	
	@SuppressWarnings("unchecked")
	public DataBuffer() {
		buffer = new float[BUFFER_CAPACITY];
		size = 0;
		front = 0;
		rear = 0;
		sum = 0;
	}
	
	/**
	 * Adds data to the buffer, removing the last element if capacity has been reached.
	 * @param data the element to add
	 */
	public void enqueue(float data) {
		if (size == BUFFER_CAPACITY) {
			removeLast();
		}
		buffer[rear] = data;
		rear = (rear + 1) % buffer.length;
		sum += data;
		size++;
	}
	
	/**
	 * Removes the last element in the buffer
	 */
	private void removeLast() {
		sum -= buffer[front];
		front = (front + 1) % buffer.length;
		size--;
	}
	
	/**
	 * Returns the average of the data contained in the buffer, removing any outliers.
	 * @return the average of the data contained in the buffer, removing any outliers.
	 */
	private float getAverage() {
		return sum / size;
	}

	private float getCurrentValue(){
		return buffer[rear];
	}
	
	
}
