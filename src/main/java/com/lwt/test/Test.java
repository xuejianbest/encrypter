package com.lwt.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Test {

	public static void main(String[] args) throws IOException {
		File t = new File("e:/abc.txt");
		t.renameTo(new File("e:/abcd.txt"));
		System.out.println(t);
	}

}
