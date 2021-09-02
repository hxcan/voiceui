package com.stupidbeauty.ftpserver.lib;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;

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

    public FtpServer(String host, int port, Context context) {
        this.context=context;
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        this.port = port;

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

        currentWorkingDirectory=currentWorkingDirectory.trim();

        String extraParameter=content.split(" ")[1];


        String wholeDirecotoryPath= context.getFilesDir().getPath() + currentWorkingDirectory; // 构造完整路径。

        String command = "ls " + extraParameter + " " + wholeDirecotoryPath; // 构造命令。

        Log.d(TAG, "command: " + command); // Debug.

//         String output = `command`;
        String output = shellExec(command);
        
        Log.d(TAG, "output: " + output); // Debug

        Util.writeAll(data_socket, (output + "\n").getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully wrote message");
                
                        notifyLsCompleted(); // 告知已经发送目录数据。

            }
        });

    } //private void sendListContent(String content, String currentWorkingDirectory)

    /**
     * 告知已经发送目录数据。
     */
    private void notifyLsCompleted()
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

    } //private void notifyLsCompleted()

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

            currentWorkingDirectory=content.substring(4);

            String replyString="200 " + "\n"; // 回复内容。

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

            String replyString="227 Entering Passive Mode (0,0,0,0,6,8) \n"; // 回复内容。

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
        else if (command.equals("list")) // 列出目录
        {
            //        elsif command=='list'
//        send_data "150 \n"
//        DataModule.instance.sendListContent(data, @currentWorkingDirectory)

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

            sendListContent(content, currentWorkingDirectory); // 发送目录列表数据。
        } //else if (command.equals("list")) // 列出目录

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
     * 接受数据连接
     * @param socket 连接对象。
     */
    private void handleDataAccept(final AsyncSocket socket)
    {
        this.data_socket=socket;
        System.out.println("[Server] data New Connection " + socket.toString());

        socket.setDataCallback(
                new DataCallback()
                {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        String content = new String(bb.getAllByteArray());
                        Log.d(TAG, "[Server] data Received Message " + content); // Debug
                    }
                });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] data Successfully closed connection");
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
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully end connection");
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
