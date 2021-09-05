package com.stupidbeauty.ftpserver.lib;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.util.Date;    
import java.time.format.DateTimeFormatter;
import java.io.File;
import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import org.apache.commons.io.FileUtils;
import com.koushikdutta.async.callback.ConnectCallback;
import java.net.InetSocketAddress;
import android.text.format.Formatter;
import android.net.wifi.WifiManager;
import java.util.Random;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.stupidbeauty.builtinftp.Utils.shellExec;

public class FtpServer {
    private Context context; //!< 执行时使用的上下文。

    private static final String TAG="Server"; //!< 输出调试信息时使用的标记
    private AsyncSocket socket; //!< 当前的客户端连接。
    private AsyncSocket data_socket; //!< 当前的数据连接。
    private InetAddress host;
    private int port;
    private int data_port=1544; //!< 数据连接端口。
    private String currentWorkingDirectory="/"; //!< 当前工作目录
    private File writingFile; //!< 当前正在写入的文件。
        private boolean allowActiveMode=true; //!< 是否允许主动模式。

    public FtpServer(String host, int port, Context context, boolean allowActiveMode) {
        this.context=context;
        this.allowActiveMode=allowActiveMode;
        
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

//         if (autoPort) // 使用自动端口
//         {
//         陈欣
//         } //if (autoPort) // 使用自动端口
//         else // 不使用自动端口
//         {
        this.port = port;
//         } //else // 不使用自动端口

        setup();

        setupDataServer(); // 启动数据传输服务器。
    }

    /**
     * 启动数据传输服务器。
     */
    private void setupDataServer()
    {
        AsyncServer.getDefault().listen(host, data_port, new ListenCallback() {

            @Override
            public void onAccepted(final AsyncSocket socket)
            {
                handleDataAccept(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket)
            {
                System.out.println("[Server] Server started listening for data connections");
            }

            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully shutdown server");
            }
        });

    } //private void setupDataServer()

    private void setup()
    {
        AsyncServer.getDefault().listen(host, port, new ListenCallback() {

            @Override
            public void onAccepted(final AsyncSocket socket)
            {
                handleAccept(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket)
            {
                System.out.println("[Server] Server started listening for connections");
            }

            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully shutdown server");
            }
        });
    }
    
    /**
    *  获取目录的完整列表。
    */
    private String getDirectoryContentList(String wholeDirecotoryPath, String nameOfFile)
    {
    nameOfFile=nameOfFile.trim();
    
    String result="";
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。
            
           File[]   paths = photoDirecotry.listFiles();
         
         // for each pathname in pathname array
         for(File path:paths) {
         
            // prints file and directory paths
            System.out.println(path);
            
            
            // -rw-r--r-- 1 nobody nobody     35179727 Oct 16 07:31 VID_20201015_181816.mp4
// -rw-r--r-- 1 nobody nobody       243826 Jan 15 11:52 forum.php.jpg
// -rw-r--r-- 1 nobody nobody       240927 Jan 16 11:15 forum.php.1.jpg
// -rw-r--r-- 1 nobody nobody       205318 Jan 16 11:16 forum.php.2.jpg

            String fileName=path.getName(); // 获取文件名。

                            Date date=new Date(path.lastModified());  
                            
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//   String time= date.format(formatter);
                            String time="8:00";

                              DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM");

//                             String dateString=date.format(dateFormatter); // 日期字符串。
                            String dateString="30";
                            
                            long fileSize=path.length(); // 文件尺寸。
                            
                            String group="cx";
                            
                            String user = "ChenXin";
                            
                            String linkNumber="1";
                            
                            String permission="-rw-r--r--";

                            String month="Jan"; // 月份 。
            String currentLine = permission + " " + linkNumber + " " + user + " " + group + " " + fileSize + " " + month + " " + dateString + " " + time + " " + fileName + "\n" ; // 构造当前行。
            
            if (fileName.equals(nameOfFile)  || (nameOfFile.isEmpty())) // 名字匹配。
            {
            result=result+currentLine; // 构造结果。
            } //if (fileName.equals(nameOfFile)) // 名字匹配。
         }

         return result;
    } //private String getDirectoryContentList(String wholeDirecotoryPath)
    
    /**
    * 上传文件内容。
    */
    private void startStor(String data51, String currentWorkingDirectory) 
    {
                            String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory+data51; // 构造完整路径。
                    
                    wholeDirecotoryPath=wholeDirecotoryPath.replace("//", "/"); // 双斜杠替换成单斜杠
                    
                    Log.d(TAG, "startStor: wholeDirecotoryPath: " + wholeDirecotoryPath); // Debug.
                    
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。
            
            writingFile=photoDirecotry; // 记录文件。

//             陈欣

        if (photoDirecotry.exists())
        {
            photoDirecotry.delete();
        }
        
                    				try //尝试构造请求对象，并且捕获可能的异常。
		{

        FileUtils.touch(photoDirecotry); //创建文件。
        						} //try //尝试构造请求对象，并且捕获可能的异常。
		catch (Exception e)
		{
			e.printStackTrace();
		}

    } //private void startStor(String data51, String currentWorkingDirectory) // 上传文件内容。
    
    /**
    * 发送文件内容。
    */
    private void sendFileContent(String data51, String currentWorkingDirectory) 
    {
                        String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory+data51; // 构造完整路径。
                    
                    wholeDirecotoryPath=wholeDirecotoryPath.replace("//", "/"); // 双斜杠替换成单斜杠
                    
                    Log.d(TAG, "processSizeCommand: wholeDirecotoryPath: " + wholeDirecotoryPath); // Debug.
                    
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。
            
            String replyString=""; // 回复字符串。

            		byte[] photoBytes=null; //数据内容。

            				try //尝试构造请求对象，并且捕获可能的异常。
		{

				photoBytes= FileUtils.readFileToByteArray(photoDirecotry); //将照片文件内容全部读取。
				
						} //try //尝试构造请求对象，并且捕获可能的异常。
		catch (Exception e)
		{
			e.printStackTrace();
		}


		        if (data_socket!=null) // 数据连接存在
        {

				        Util.writeAll(data_socket, photoBytes, new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully wrote message");
                
//                 data_socket.close(); // 关闭套接字。
                                        notifyFileSendCompleted(); // 告知已经发送文件内容数据。

            }
        });

                } //if (data_socket!=null)
        else // 数据连接不存在
        {
            notifyLsFailedDataConnectionNull(); // 告知，数据连接未建立。
        } //else // 数据连接不存在

    } //private void sendFileContent(String data51, String currentWorkingDirectory)

    /**
     * 发送目录列表数据。
     * @param content 目录路径
     * @param currentWorkingDirectory 当前工作目录。
     */
    private void sendListContent(String content, String currentWorkingDirectory)
    {
//        puts "currentWorkingDirectory: #{currentWorkingDirectory}, lenght: #{currentWorkingDirectory.length}"
//        currentWorkingDirectory.strip!
//            puts "currentWorkingDirectory: #{currentWorkingDirectory}, lenght: #{currentWorkingDirectory.length}"
//        extraParameter=data.split(" ")[1]
//        puts "extraParameter: #{extraParameter}"
//        command="ls #{extraParameter} #{currentWorkingDirectory}"
//        puts "command: #{command}"
//        #command: ls -la /
//
//            output=`#{command}`
//        send_data("#{output}\n")
//        puts "sent #{output}"
//        FtpModule.instance.notifyLsCompleted

        String parameter=content.substring(5).trim(); // 获取额外参数。
        
        if (parameter.equals("-la")) // 忽略
        {
        parameter=""; // 忽略成空白。
        } //if (parameter.equals("-la")) // 忽略
        
        

        currentWorkingDirectory=currentWorkingDirectory.trim();

        String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory; // 构造完整路径。

            String output = getDirectoryContentList(wholeDirecotoryPath, parameter); // 获取目录的完整列表。
        
        Log.d(TAG, "output: " + output); // Debug
        
        // Debug:
//         output ="总用量 24452\ndrwxr-xr-x 2 root root     4096  9月  2 08:06 .\ndrwxr-xr-x 4 root root     4096  9月  2 08:03 ..\n-rw-r--r-- 1 root root    14317  9月  2 09:11 18189.cpp.txt\n-rw-r--r-- 1 root root      332  8月 31 09:47 dialog.h\n-rw-r--r-- 1 root root     2076  8月 31 09:47 dialog.ui\n-rw-r--r-- 1 root root      226  8月 31 09:47 imagefft.pro\n-rw-r--r-- 1 root root 24998479  9月  2 09:10 list.cpp.txt\n";

// -rw-r--r-- 1 nobody nobody     35179727 Oct 16 07:31 VID_20201015_181816.mp4
// -rw-r--r-- 1 nobody nobody       243826 Jan 15 11:52 forum.php.jpg
// -rw-r--r-- 1 nobody nobody       240927 Jan 16 11:15 forum.php.1.jpg
// -rw-r--r-- 1 nobody nobody       205318 Jan 16 11:16 forum.php.2.jpg


        Log.d(TAG, "sendListContent, data_socket: " + data_socket); // Debug
        
        if (data_socket!=null) // 数据连接存在
        {
        Util.writeAll(data_socket, (output + "\n").getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully wrote message");
                
//                 data_socket.close(); // 关闭套接字。
                                        notifyLsCompleted(); // 告知已经发送目录数据。

            }
        });
            
        } //if (data_socket!=null)
        else // 数据连接不存在
        {
            notifyLsFailedDataConnectionNull(); // 告知，数据连接未建立。
        } //else // 数据连接不存在


    } //private void sendListContent(String content, String currentWorkingDirectory)
    
    
    /**
    * 告知上传完成。
    */
    private void notifyStorCompleted() 
    {
//         def notifyStorCompleted
//         send_data("226 \n")
//     end

        String replyString="226 " + "\n"; // 回复内容。

        Log.d(TAG, "reply string: " + replyString); //Debug.

        Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully wrote message");
            }
        });


    } //private void notifyStorCompleted()
    
    /**
    * 告知已经发送文件内容数据。
    */
    private void notifyFileSendCompleted() 
    {
    //        send_data "216 \n"

        String replyString="216 " + "\n"; // 回复内容。

        Log.d(TAG, "reply string: " + replyString); //Debug.

        Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully wrote message");
            }
        });


    } //private void notifyFileSendCompleted()
    
    /**
    * 告知，数据连接未建立。
    */
    private void notifyLsFailedDataConnectionNull() 
    {
    //        send_data "216 \n"

        String replyString="426 " + "\n"; // 回复内容。

        Log.d(TAG, "reply string: " + replyString); //Debug.

        Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                Log.d(TAG, "notifyLsFailedDataConnectionNull, [Server] Successfully wrote message");
            }
        });

    } //private void notifyLsFailedDataConnectionNull()

    /**
     * 告知已经发送目录数据。
     */
    private void notifyLsCompleted()
    {
//        send_data "216 \n"

        String replyString="226 Data transmission OK. ChenXin" + "\n"; // 回复内容。

        Log.d(TAG, "reply string: " + replyString); //Debug.

        Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully wrote message");
            }
        });

    } //private void notifyLsCompleted()
    
//         def processSizeCommand(data51)
//         if File.exists?(data51)
//             send_data("213 #{File.size(data51)} \n")
//         else
//             send_data("550 \n") # file not found
//         end
//     end

    /**
    * 处理尺寸查询命令。
    */
    private void processSizeCommand(String data51)
    {
                        Log.d(TAG, "processSizeCommand: filesdir: " + context.getFilesDir().getPath()); // Debug.
                        Log.d(TAG, "processSizeCommand: workding directory: " + currentWorkingDirectory); // Debug.
                        Log.d(TAG, "processSizeCommand: data51: " + data51); // Debug.

    
                    String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory+data51; // 构造完整路径。
                    
                    wholeDirecotoryPath=wholeDirecotoryPath.replace("//", "/"); // 双斜杠替换成单斜杠
                    
                    Log.d(TAG, "processSizeCommand: wholeDirecotoryPath: " + wholeDirecotoryPath); // Debug.
                    
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。
            
            String replyString=""; // 回复字符串。

            if (photoDirecotry.exists()) // 文件存在
            {
            long fileSize= photoDirecotry.length(); //文件尺寸。 陈欣
            
                replyString="213 " + fileSize + " \n"; // 文件尺寸。
            } //if (photoDirecotry.exists()) // 文件存在
            else // 文件不 存在
            {
            replyString="550 \n"; // 文件不存在。
            } //else // 文件不 存在

                        Log.d(TAG, "reply string: " + replyString); //Debug.

                                Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

    } //private void processSizeCommand(String data51)


    /**
     * 处理命令。
     * @param command 命令关键字
     * @param content 整个消息内容。
     */
    private void processCommand(String command, String content)
    {

        Log.d(TAG, "command: " + command + ", content: " + content); //Debug.

        if (command.equals("USER")) // 用户登录
        {
//            send_data "230 \n"

            Util.writeAll(socket, "230 \n".getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //if (command.equals("USER")) // 用户登录
        else if (command.equals("SYST")) // 系统信息
        {
            //        send_data "200 UNIX Type: L8\n"

            Util.writeAll(socket, "200 UNIX Type: L8\\n".getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("SYST")) // 系统信息
        else if (command.equals("PWD")) // 查询当前工作目录
        {
            //        send_data "200 #{@currentWorkingDirectory}\n"
//        puts "200 #{@currentWorkingDirectory}\n"

            String replyString="200 " + currentWorkingDirectory + "\n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("PWD")) // 查询当前工作目录
        else if (command.equals("cwd")) // 切换工作目录
        {
            //        elsif command=='cwd'
//        newWorkingDirectory=data[4..-1]
//        puts "newWorkingDirectory: #{newWorkingDirectory}"
//        @currentWorkingDirectory= newWorkingDirectory
//        send_data "200 \n"

            String targetWorkingDirectory=content.substring(4).trim();
            
                        String wholeDirecotoryPath= context.getFilesDir().getPath() + targetWorkingDirectory; // 构造完整路径。
                    
                    wholeDirecotoryPath=wholeDirecotoryPath.replace("//", "/"); // 双斜杠替换成单斜杠
                    
                    Log.d(TAG, "processSizeCommand: wholeDirecotoryPath: " + wholeDirecotoryPath); // Debug.
                    
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。

                        String replyString="" ; // 回复内容。

            if (photoDirecotry.isDirectory()) // 是个目录
            {
            currentWorkingDirectory=targetWorkingDirectory;

            replyString="200 " + "\n"; // 回复内容。
            
            } //if (photoDirecotry.isDirectory()) // 是个目录
                else //不是个目录
                {
//                 陈欣
            replyString="550 " + "\n"; // 回复内容。
                
                }


            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });


        } //else if (command.equals("cwd")) // 切换工作目录
        else if (command.equals("TYPE")) // 传输类型
        {
//        elsif command =='TYPE'
//        send_data "200 \n"

            String replyString="200 " + "\n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("TYPE")) // 传输类型
        else if (command.equals("PASV")) // 被动传输
        {
            //        elsif command=='PASV'
//            #227 Entering Passive Mode (a1,a2,a3,a4,p1,p2)
//            #where a1.a2.a3.a4 is the IP address and p1*256+p2 is the port number.
//        a1=a2=a3=a4=0
//
//            #1544
//        p1=6
//        p2=8
//
//        send_data "227 Entering Passive Mode (#{a1},#{a2},#{a3},#{a4},#{p1},#{p2}) \n"

                    Random random=new Random(); //随机数生成器。

                int randomIndex=random.nextInt(65535-1025)+1025; //随机选择一个文件。


        data_port=randomIndex; 
        
        setupDataServer(); // 初始化数据服务器。
        
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        
        String ip = ipAddress.replace(".", ",");
        
        int port256=data_port/256;
        int portModule=data_port-port256*256;

            String replyString="227 Entering Passive Mode ("+ip+","+port256+","+portModule+") \n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });


        } //else if (command.equals("PASV")) // 被动传输
        else if (command.equals("EPSV")) // 扩展被动模式
        {
            //        elsif command=='EPSV'
//        send_data "202 \n"

            String replyString="202 \n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("EPSV")) // 扩展被动模式
        else if (command.equals("PORT")) // 要求服务器主动连接客户端的端口
        {
            //        elsif command=='EPSV'
//        send_data "202 \n"

//             String replyString="202 \n"; // 回复内容。未实现

            String replyString="150 \n"; // 回复内容。正在打开数据连接


        if (allowActiveMode) // 允许主动模式
        {
            openDataConnectionToClient(content); // 打开指向客户端特定端口的连接。

                         replyString="150 \n"; // 回复内容。正在打开数据连接

            } //if (allowActiveMode) // 允许主动模式
        else // 不允许主动模式。
        {

                     replyString="202 \n"; // 回复内容。未实现。
} //else // 不允许主动模式。


            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    Log.d(TAG, "[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("EPSV")) // 扩展被动模式
        else if (command.equals("list")) // 列出目录
        {
            //        elsif command=='list'
//        send_data "150 \n"
//        DataModule.instance.sendListContent(data, @currentWorkingDirectory)

//            陈欣

            String replyString="150 Opening BINARY mode data connection for file list, ChenXin\n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

            sendListContent(content, currentWorkingDirectory); // 发送目录列表数据。
        } //else if (command.equals("list")) // 列出目录
        else if (command.equals("retr")) // 获取文件
        {
//            陈欣

            String replyString="150 \n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

            String data51=            content.substring(5);

data51=data51.trim(); // 去掉末尾换行


            sendFileContent(data51, currentWorkingDirectory); // 发送文件内容。
        } //else if (command.equals("list")) // 列出目录
        else if (command.equals("SIZE")) // 文件尺寸
        {
        //        elsif command == 'SIZE'
//        processSizeCommand(data[5..-1])

String data51=            content.substring(5);

data51=data51.trim(); // 去掉末尾换行


processSizeCommand(data51); // 处理尺寸 命令。
        } //else if (command.equals("SIZE")) // 文件尺寸
        else if (command.equals("DELE")) // 删除文件
        {
        //        fileName=data[5..-1]
//        File.delete(fileName.strip)
//        send_data "250 \n"

String data51=            content.substring(5);

data51=data51.trim(); // 去掉末尾换行

// 删除文件。陈欣

                        String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory+data51; // 构造完整路径。
                    
                    wholeDirecotoryPath=wholeDirecotoryPath.replace("//", "/"); // 双斜杠替换成单斜杠
                    
                    Log.d(TAG, "processSizeCommand: wholeDirecotoryPath: " + wholeDirecotoryPath); // Debug.
                    
            File photoDirecotry= new File(wholeDirecotoryPath); //照片目录。

            photoDirecotry.delete();
            
            
            String replyString="250 \n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

        } //else if (command.equals("DELE")) // 删除文件
        else if (command.equals("stor")) // 上传文件
        {
        //        elsif command=='stor'
//        send_data "150 \n"
//        DataModule.instance.startStor(data[5..-1])

            String replyString="150 \n"; // 回复内容。

            Log.d(TAG, "reply string: " + replyString); //Debug.

            Util.writeAll(socket, replyString.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully wrote message");
                }
            });

            String data51=            content.substring(5);

data51=data51.trim(); // 去掉末尾换行


            startStor(data51, currentWorkingDirectory); // 发送文件内容。

        } //else if (command.equals("stor")) // 上传文件

//        2021-08-29 20:57:40.287 16876-16916/com.stupidbeauty.builtinftp.demo D/Server: [Server] Received Message cwd /
//            2021-08-29 20:57:40.287 16876-16916/com.stupidbeauty.builtinftp.demo D/Server: command: cwd, content: cwd /
//            2021

//        def processCommand (command,data)
//        if command== 'USER'
//        send_data "230 \n"
//        elsif command == 'SYST'
//        send_data "200 UNIX Type: L8\n"
//        elsif command== 'PWD'
//        send_data "200 #{@currentWorkingDirectory}\n"
//        puts "200 #{@currentWorkingDirectory}\n"
//        elsif command=='cwd'
//        newWorkingDirectory=data[4..-1]
//        puts "newWorkingDirectory: #{newWorkingDirectory}"
//        @currentWorkingDirectory= newWorkingDirectory
//        send_data "200 \n"
//        elsif command =='TYPE'
//        send_data "200 \n"
//        elsif command=='PASV'
//            #227 Entering Passive Mode (a1,a2,a3,a4,p1,p2)
//            #where a1.a2.a3.a4 is the IP address and p1*256+p2 is the port number.
//        a1=a2=a3=a4=0
//
//            #1544
//        p1=6
//        p2=8
//
//        send_data "227 Entering Passive Mode (#{a1},#{a2},#{a3},#{a4},#{p1},#{p2}) \n"
//        elsif command=='EPSV'
//        send_data "202 \n"
//        elsif command=='list'
//        send_data "150 \n"
//        DataModule.instance.sendListContent(data, @currentWorkingDirectory)
//        elsif command == 'SIZE'
//        processSizeCommand(data[5..-1])
//        elsif command=='stor'
//        send_data "150 \n"
//        DataModule.instance.startStor(data[5..-1])
//        elsif command=='SITE'
//        send_data "502 \n"
//        elsif command=='DELE'
//        fileName=data[5..-1]
//        File.delete(fileName.strip)
//        send_data "250 \n"
//        end
//
//
//                end

    } //private void processCommand(String command, String content)
    
    /**
    * 从数据套接字处接收数据。陈欣
    */
    private void                         receiveDataSocket( ByteBufferList bb)
    {
                            byte[] content=bb.getAllByteArray(); // 读取全部内容。
                        
                        boolean appendTrue=true;

                        try
                        {
                        FileUtils.writeByteArrayToFile(writingFile, content, appendTrue); // 写入。
                        }
                        catch (Exception e)
                        {
                        e.printStackTrace();
                        }
                        

    } //private void                         receiveDataSocket( ByteBufferList bb)

    
        private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {
        if(ex != null) 
        {
//         throw new RuntimeException(ex);
ex.printStackTrace(); //报告错误
        }
        else // 无异常。
        {
        this.data_socket=socket; // 记录数据连接。

//         Util.writeAll(socket, "Hello Server".getBytes(), new CompletedCallback() {
//             @Override
//             public void onCompleted(Exception ex) {
//                 if (ex != null) throw new RuntimeException(ex);
//                 System.out.println("[Client] Successfully wrote message");
//             }
//         });

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
//                 System.out.println("[Client] Received Message " + new String(bb.getAllByteArray()));
                        receiveDataSocket(bb);

            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully closed connection");
                
                                                data_socket=null;
                
                
//                     def unbind
//         @file&.close
//         
//         FtpModule.instance.notifyStorCompleted
//     end

                notifyStorCompleted(); // 告知上传完成。

            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully end connection");
                

            }
        });
        
        } //else // 无异常。
        
    }

    /**
    * 打开指向客户端特定端口的连接。
    */
    private void openDataConnectionToClient(String content)
    {
    String portString=content.split(" ")[1].trim(); // 端口字符串。
    
    String[] addressStringList= portString.split(","); //获取地址字符串。
    
    String ip=addressStringList[0]+"."+addressStringList[1]+"."+addressStringList[2]+"."+addressStringList[3]; // 构造IP。陈欣
    int port=Integer.parseInt(addressStringList[4])*256+Integer.parseInt(addressStringList[5]); // 计算出端口号。
    
    //连接：陈欣
    
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(ip, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                handleConnectCompleted(ex, socket);
            }
        });

    } //private void openDataConnectionToClient(String content)

    /**
     * 接受数据连接
     * @param socket 连接对象。
     */
    private void handleDataAccept(final AsyncSocket socket)
    {
        this.data_socket=socket;
        Log.d(TAG, "handleDataAccept, [Server] data New Connection " + socket.toString());

        socket.setDataCallback(
                new DataCallback()
                {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
//                         String content = new String(bb.getAllByteArray());
//                         Log.d(TAG, "[Server] data Received Message " + content); // Debug
                        
                        receiveDataSocket(bb);
                        
                    }
                });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully closed connection");
                
//                                         notifyLsCompleted(); // 告知已经发送目录数据。

                data_socket=null;
                
                
//                     def unbind
//         @file&.close
//         
//         FtpModule.instance.notifyStorCompleted
//     end

                notifyStorCompleted(); // 告知上传完成。
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully end connection");
            }
        });
    } //private void handleDataAccept(final AsyncSocket socket)

    /**
     * 接受新连接
     * @param socket 新连接的套接字对象
     */
    private void handleAccept(final AsyncSocket socket)
    {
        this.socket=socket;
        System.out.println("[Server] New Connection " + socket.toString());

        socket.setDataCallback(
                new DataCallback()
                {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String content = new String(bb.getAllByteArray());
                Log.d(TAG, "[Server] Received Message " + content); // Debug

                String command = content.split(" ")[0]; // 获取命令。


                command=command.trim();
//                command = data.split(" ").first
//
//                processCommand( command,data)


                processCommand(command, content); // 处理命令。


            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) // 有异常出现
                {
//                 throw new RuntimeException(ex);
                    ex.printStackTrace(); // 报告。
                }
                else // 无异常
                {
                Log.d(TAG, "ftpmodule [Server] Successfully end connection");
                } //else // 无异常
            }
        });

        //发送初始命令：
//        send_data "220 \n"

        Util.writeAll(socket, "220 \n".getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully wrote message");
            }
        });

    }
}
