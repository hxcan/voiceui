package com.stupidbeauty.nqna;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.io.File;
import android.net.Uri;

/**
 * @author Hxcan
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MacroOperations
{
	private final Context context; //!<The context.
	private static final String TAG="MacroOperations"; //!<输出调试信息时使用的标记。


	private  InetAddress group; //!<广播组地址。
	private MulticastSocket multiSocket; //!<多播套接字。
	private static final int PORT = 11500; //!<多播组的端口号。

	/**
	 * 请求扫描照片。
	 * @param apkFilePath 照片文件的完整路径。
	 */
	public void requestScanPhoto(String apkFilePath)
	{

		Log.d(TAG, "requestInstallApk"); //Debug.

		scanFile(apkFilePath); //要求扫描 。

	} //public void requestScanPhoto(String apkFilePathJString)

	/**
	 * 要求扫描照片。
	 * @param path 照片文件的路径。
	 */
	private void scanFile(String path)
	{

		MediaScannerConnection.scanFile(context,
				new String[] { path }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					public void onScanCompleted(String path, Uri uri) {
						Log.i("TAG", "Finished scanning " + path);
					}
				});
	} //private void scanFile(String path)


	/**
	 * 执行提交文字动作。
	 * @param apkFilePath 文字内容。
	 *
	 */
	public void requestInstallApk(String apkFilePath)
	{
		Log.d(TAG, "requestInstallApk"); //Debug.
		String className= apkFilePath; //要提交的文字内容。

		String Result = ""; // 结果。

		Log.d(TAG, "requestInstallApk, 54"); //Debug.
		String maskFileName=""; //获取掩码图片文件名。

		PackageManager packageManager=context.getPackageManager(); //获取软件包管理器。

		Log.d(TAG, "requestInstallApk, 59, apk file path: "+apkFilePath); //Debug.

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.d(TAG, "requestInstallApk, 64"); //Debug.
		context.startActivity(intent);
		Log.d(TAG, "requestInstallApk, 66"); //Debug.

//		return 0;
	} //private void requestInstallApk(String textContent, String transactionId)

	/**
	 * 构造函数。
	 * @param context 服务上下文。
	 */
	public MacroOperations(Context context)
	{
		super();
		Log.d(TAG,"MacroOperations, 70."); //Debug.

		this.context=context; //Remember context.
		Log.d(TAG,"MacroOperations, 74."); //Debug.
	} //public MacroOperations()

	/**
	 * 加入多播组。
	 */
	private void joinMulticastGroup() 
	{
		
		try {
			//224.0.0.0~239.255.255.255

//Table 1 Multicast Address Range Assignments
//
//Description
//Range
//Reserved Link Local Addresses
//
//224.0.0.0/24
//
//Globally Scoped Addresses
//
//224.0.1.0 to 238.255.255.255
//
//Source Specific Multicast
//
//232.0.0.0/8
//
//GLOP Addresses
//
//233.0.0.0/8
//
//Limited Scope Addresses
//
//239.0.0.0/8
//			
			group = InetAddress.getByName("239.173.40.5");
			multiSocket=new MulticastSocket(PORT);
			multiSocket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} //private void joinMulticastGroup()




}
