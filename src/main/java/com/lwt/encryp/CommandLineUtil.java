package com.lwt.encryp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.lwt.util.DirUtil;

public class CommandLineUtil {
	private String[] args;
	private Options opts = new Options();
	
	private File keyFile;
	private boolean encrypt;
	private boolean create;
	private File[] files;
	private File[] dirs;
	public File getKeyFile() {
		return keyFile;
	}
	public boolean isEncrypt() {
		return encrypt;
	}
	public boolean isCreate() {
		return create;
	}
	public File[] getFiles() {
		return files;
	}
	public File[] getDirs() {
		return dirs;
	}

	
	public CommandLineUtil(String[] args) {
		this.args = args;
		definedOptions();
		parseOptions();
		duplicate_removal();
	}
	
	// 定义命令行参数
	private void definedOptions(){
		Option opt_h = new Option("h", "Show this page.");
		Option opt_e = new Option("e", "encrypt", false, "Encrypt file.");
		Option opt_d = new Option("d", "decrypt", false, "Decrypt file.");
		Option opt_c = new Option("c", "create", false, "Create new key file.");
		Option opt_k = Option.builder("k").hasArg().argName("keyFile")
				.desc("Specify the key file").build();
		Option opt_f = Option.builder("f").hasArgs().argName("file1,file2...")
				.valueSeparator(',')
				.desc("A files list with ',' separate to handle").build();
		Option opt_r = Option
				.builder("r")
				.hasArgs()
				.argName("dir1,dir1...")
				.valueSeparator(',')
				.desc("A directories list with ',' separate to handle its child files")
				.build();
		Option opt_R = Option
				.builder("R")
				.hasArgs()
				.argName("dir1,dir1...")
				.valueSeparator(',')
				.desc("A directories list with ',' separate to recurse handle child files")
				.build();
		
		opts.addOption(opt_c);
		opts.addOption(opt_k);
		opts.addOption(opt_h);
		opts.addOption(opt_e);
		opts.addOption(opt_d);
		opts.addOption(opt_f);
		opts.addOption(opt_r);
		opts.addOption(opt_R);
	}
	
	// 解析处理命令行参数
	private void parseOptions(){
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		// 解析命令行参数
		try {
			line = parser.parse(opts, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		// 若指定h则显示帮助
		if (args == null || args.length == 0 || line.hasOption("h")) {
			HelpFormatter help = new HelpFormatter();
			help.printHelp("encrypt", opts);
		}

		// 选择加密或解密操作，默认是加密文件
		if (line.hasOption("d")) {
			if (line.hasOption("e")) {
				System.err
						.println("The -e and -d option can't specify at the same time.");
				System.exit(1);
			}
			encrypt = false;
		} else {
			encrypt = true;
		}
		
		if (line.hasOption("k")) {
			String k = line.getOptionValue("k");
			File file = new File(k);
			if (line.hasOption("c")) {
				keyFile = file;
				create = true;
			}else {
				if(file.isFile()){
					keyFile = file;
				} else{
					System.err.println(file + " is not a available key file");
					System.exit(1);
				}
			}
		}

		ArrayList<File> files = new ArrayList<File>();
		ArrayList<File> dirs = new ArrayList<File>();
		if (line.hasOption("f")) {
			String[] fs = line.getOptionValues("f");
			for(String f : fs){
				File file = new File(f);
				if(file.isFile()){
					files.add(file);
				}else{
					System.err.println(file + " is not a file");
					System.exit(1);
				}
			}
		}

		if (line.hasOption("r")) {
			String[] rs = line.getOptionValues("r");
			for(String r : rs){
				File dir = new File(r);
				if(dir.isDirectory()){
					dirs.add(dir);
					DirUtil dirUtil = new DirUtil(dir);
					files.addAll(Arrays.asList(dirUtil.getFiles()));
					dirs.addAll(Arrays.asList(dirUtil.getDirs()));
				}else{
					System.err.println(dir + " is not a directory");
					System.exit(1);
				}
			}
		}

		if (line.hasOption("R")) {
			String[] Rs = line.getOptionValues("R");
			for(String R : Rs){
				File dir = new File(R);
				if(dir.isDirectory()){
					dirs.add(dir);
					DirUtil dirUtil = new DirUtil(dir);
					files.addAll(Arrays.asList(dirUtil.getAllFiles()));
					dirs.addAll(Arrays.asList(dirUtil.getAllDirs()));
				}else{
					System.err.println(dir + " is not a directory");
					System.exit(1);
				}
			}
		}
		
		this.files = files.toArray(new File[0]);
		this.dirs = dirs.toArray(new File[0]);
		
	}
	
	
	public void duplicate_removal (){
		HashSet<File> fileSet = new HashSet<File>();
		for(File file : files){
			try {
				fileSet.add(file.getCanonicalFile());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		files = fileSet.toArray(new File[0]);
		
		fileSet = new HashSet<File>();
		for(File dir : dirs){
			try {
				fileSet.add(dir.getCanonicalFile());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		dirs = fileSet.toArray(new File[0]);
	}
}
