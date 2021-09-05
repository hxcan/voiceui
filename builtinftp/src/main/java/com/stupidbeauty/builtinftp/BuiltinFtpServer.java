package com.stupidbeauty.builtinftp;

import android.content.Context;
import android.os.AsyncTask;

import com.stupidbeauty.ftpserver.lib.FtpServer;

public class BuiltinFtpServer
        {
        private int port=1421; //!< 端口。
        private FtpServer ftpServer=null; //!< Ftp服务器对象。
        private boolean allowActiveMode=true; //!< 是否允许主动模式。
        
        /**
        * 设置，是否允许主动模式。
        */
        private void setAllowActiveMode(boolean allowActiveMode)
        {
        this.allowActiveMode=allowActiveMode;
        } //private void setAllowActiveMode(allowActiveMode)
        
        public void setPort(int port)
        {
        this.port=port;
        } //public void setPort(int port)
        
                private BuiltinFtpServer() {
                }

                public BuiltinFtpServer(Context context) {
                        this.context = context;
                }

                private Context context; //!< 上下文。

public void start()
{
        new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                        //TCP client and server (Client will automatically send welcome message after setup and server will respond)
                        new FtpServer("0.0.0.0", port, context, allowActiveMode);

                        return null;
                }
        }.execute();

}

        }








