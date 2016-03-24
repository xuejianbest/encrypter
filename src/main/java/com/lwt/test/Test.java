package com.lwt.test;

import java.io.IOException;
import java.net.URL;

public class Test {
	public void getResource() throws IOException {
		URL fileURL = this.getClass().getResource("/text.txt");
		System.out.println(fileURL.getFile());
	}

	public static void main(String[] args) throws IOException {
		Test t = new Test();
		t.getResource();
	}

}
