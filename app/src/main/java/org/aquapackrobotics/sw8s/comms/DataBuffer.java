/**
 * 
 */
package org.aquapackrobotics.sw8s.comms;

/**
 * Data Buffer is used to store data that is received quickly in order to filter out outliers.
 * @author vaibh
 *
 * @param E the type of Data stored in the buffer;
 */
public class DataBuffer<E> {
	
	private static int BUFFER_CAPACITY = 10;
	
	private E[] buffer;
	
	private int front;
	
	private int rear;
	
	private int size;
	
	@SuppressWarnings("unchecked")
	public DataBuffer() {
		buffer = (E[]) new Object[BUFFER_CAPACITY];
		size = 0;
		front = 0;
		rear = 0;
	}
	
	public void enqueue(E data) {
		if (size == BUFFER_CAPACITY) {
			removeLast();
		}
		buffer[rear] = data;
		rear = (rear + 1) % buffer.length;

	}
	
	private void removeLast() {
		front = (front + 1) % buffer.length;
		size--;
	}
	
	private void getAverage() {
		float sum;
		for (int i = front; i != rear; i = (i + 1) % buffer.length) {
			sum = buffer[i].getData();
		}
	}
	
	
}
