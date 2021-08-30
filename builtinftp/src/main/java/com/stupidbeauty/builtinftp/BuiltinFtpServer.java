package com.stupidbeauty.builtinftp;

import android.os.AsyncTask;

public class BuiltinFtpServer
        {
public void start()
{
        new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                        //TCP client and server (Client will automatically send welcome message after setup and server will respond)
                        new com.github.reneweb.androidasyncsocketexamples.tcp.Server("0.0.0.0", 1421);

                        //UDP client and server (Here the client explicitly sends a message)
                        new com.github.reneweb.androidasyncsocketexamples.udp.Server("localhost", 7001);
                        return null;
                }
        }.execute();

}

        }








