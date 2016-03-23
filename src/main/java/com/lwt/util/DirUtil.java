package com.lwt.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class DirUtil {
	private final File dir; 
	
	public static FileFilter dirFilter = new Dir_Filter();
	public static FileFilter fileFilter = new File_Filter();
	
	private static class Dir_Filter implements FileFilter{
		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory()){
				return true;
			}
			return false;
		}
	}
	private static class File_Filter implements FileFilter{
		@Override
		public boolean accept(File pathname) {
			if(pathname.isFile()){
				return true;
			}
			return false;
		}
	}
	
	public DirUtil(File dir){
		if(dir == null || !dir.isDirectory()){
			throw new IllegalArgumentException("The parameter must be a existed directory.");
		}
		if(!dir.canRead()){
			throw new IllegalArgumentException("Permission denied: The directory can't be read.");
		}
		this.dir = dir;
	}
	
	//返回当前目录下的文件
	public File[] getFiles(){
		File[] files = dir.listFiles(fileFilter);
		if(files == null){
			System.err.println("new File(" + dir + ").listFiles(fileFilter) return null.");
			files = new File[0];
		}
		return files;
	}
	
	//返回当前目录下的目录
	public File[] getDirs(){
		File[] files = dir.listFiles(dirFilter);
		if(files == null){
			System.err.println("new File(" + dir + ").listFiles(fileFilter) return null.");
			files = new File[0];
		}
		return files;
	}
	
	//返回当前目录和其子目录下所有文件
	public File[] getAllFiles(){
		ArrayList<File> filesArr = new ArrayList<File>();
		File[] files = getFiles();
		filesArr.addAll(Arrays.asList(files));
		
		File[] dirs = getDirs();
		for(File dir : dirs){
			DirUtil dirUtil = new DirUtil(dir);
			filesArr.addAll(Arrays.asList(dirUtil.getAllFiles()));
		}
		return filesArr.toArray(new File[0]);
	}
	
	//返回当前目录和其子目录下所有目录
	public File[] getAllDirs(){
		ArrayList<File> filesArr = new ArrayList<File>();
		File[] dirs = getDirs();
		filesArr.addAll(Arrays.asList(dirs));
		
		for(File dir : dirs){
			DirUtil dirUtil = new DirUtil(dir);
			filesArr.addAll(Arrays.asList(dirUtil.getAllDirs()));
		}
		return filesArr.toArray(new File[0]);
	}
	
}
