package com.stupidbeauty.builtinftp;

import android.content.Context;
import android.os.AsyncTask;
import com.stupidbeauty.ftpserver.lib.FtpServer;

public class BuiltinFtpServer
{
    private int port=1421; //!< Port.
    private FtpServer ftpServer=null; //!< Ftp server object.
    private boolean allowActiveMode=true; //!<  Whether to allow active mode.
    
    /**
    * Set to allow or not allow active mode.
    */
    public void setAllowActiveMode(boolean allowActiveMode)
    {
        this.allowActiveMode=allowActiveMode;
    } //private void setAllowActiveMode(allowActiveMode)
    
    public void setPort(int port)
    {
        this.port=port;
    } //public void setPort(int port)
        
    private BuiltinFtpServer() {
    }

    public BuiltinFtpServer(Context context) 
    {
        this.context = context;
    }

    private Context context; //!< Context.

    public void start()
{
    new AsyncTask<Void, Void, Void>() {
        @Override
                protected Void doInBackground(Void... params) {
                    new FtpServer("0.0.0.0", port, context, allowActiveMode);

                    return null;
            }
        }.execute();
    }
}
