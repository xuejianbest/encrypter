# encrypt
## 一个Java实现的文件加密程序

### 算法简介：
随机生成密钥文件，采用文件头替换技术对文件进行加密，用于简单的加密，解密需要加密时的密钥文件支持。

密钥文件的生成为随机生成，密钥文件用于给原文件头和文件名进行多表替换加密。新文件头采用RAR加密文件头试图隐藏文件，
新文件头尾部记录了原文件头和文件名在密文在秘钥文件中的偏移量和长度和密钥偏移量。

运行程序采用如下命令行参数：
···
usage: encrypt
 -d,--decrypt          Decrypt file.
 -e,--encrypt          Encrypt file.
 -f <file1,file2...>   A files list with ',' separate to handle
 -h                    Show this page.
 -k <keyFile>          Specify the key file
 -r <dir1,dir1...>     A directories list with ',' separate to handle its
                       child files
 -R <dir1,dir1...>     A directories list with ',' separate to recurse
                       handle child files
···
