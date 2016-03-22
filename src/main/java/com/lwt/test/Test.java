package com.lwt.test;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		Timer timer = new Timer();
		
		
		TimerTask task = new Task();
		timer.schedule(task, new Date(), 1000);
		System.out.println("...main");
		
//		Thread.sleep(5000);
		task.cancel();
		
		timer = null;
	}
	
	public static class Task extends TimerTask{
		@Override
		public void run() {
			System.out.println("...excu");
		}
	}
	
	
}

