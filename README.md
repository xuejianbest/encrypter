# encrypt
## 一个Java实现的文件加密程序

### 算法简介：
采用文件头替换技术对文件进行加密，用于简单的加密，解密需要加密时的密钥文件。

密钥文件的生成为随机生成，密钥文件用于给原文件头和文件名进行多表替换加密，并存储原文件头和文件名的密文。
新文件头头部写入RAR加密文件试图隐藏文件，新文件头尾部记录了原文件头和文件名密文在秘钥文件中的偏移量和长度和密钥偏移量。

运行程序采用如下命令行参数：
```shell
usage: encrypt
 -c,--create           Create new key file.
 -d,--decrypt          Decrypt file.
 -e,--encrypt          Encrypt file.(default)
 -f <file1,file2...>   A files list with ',' separate to handle
 -h                    Show this page.
 -k <keyFile>          Specify the key file
 -n,--name             Encrypt file name.
 -r <dir1,dir1...>     A directories list with ',' separate to handle its
                       child files
 -R <dir1,dir1...>     A directories list with ',' separate to recurse
                       handle child files
```

***
假设此程序被打包为可执行jar文件：encrypt.jar。
使用之前先生成密钥文件，用于加密文件和存储文件头、文件名密文，如：
```shell
java -jar encrypt.jar -ck keyFileName
```
会在当前目录建立名为keyFileName的密钥文件，文件大小2kB

使用密钥文件加密指定文件：
```shell
java -jar encrypt.jar -k keyFileName -f d:\file1,d:\file2
```

使用密钥文件加密目录下的所有文件（**不包括**子目录里面的文件）
```shell
java -jar encrypt.jar -k keyFileName -r d:\dir1,d:\dir2
```

使用密钥文件加密目录下的所有文件（**包括**子目录里面的文件）
```shell
java -jar encrypt.jar -k keyFileName -R d:\dir1,d:\dir2
```

加密文件同时加密文件名（-n参数）
```shell
java -jar encrypt.jar -nk keyFileName -f d:\file1,d:\file2
```

解密文件与加密文件的命令类似，不同是解密文件需加-d参数
```shell
java -jar encrypt.jar -dk keyFileName -f d:\file1
```

显示帮助（-h参数）
```shell
java -jar encrypt.jar -h
```