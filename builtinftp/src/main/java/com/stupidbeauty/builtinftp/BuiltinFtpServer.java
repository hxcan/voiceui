package com.stupidbeauty.builtinftp;

import android.content.Context;
import android.os.AsyncTask;

import com.stupidbeauty.ftpserver.lib.FtpServer;

public class BuiltinFtpServer
        {
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
                        new FtpServer("0.0.0.0", 1421, context);

                        //UDP client and server (Here the client explicitly sends a message)
                        new com.github.reneweb.androidasyncsocketexamples.udp.Server("localhost", 7001);
                        return null;
                }
        }.execute();

}

        }








