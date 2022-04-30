package com.stupidbeauty.voiceui;

import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.content.Context;
import android.os.AsyncTask;
import com.stupidbeauty.ftpserver.lib.FtpServer;
import java.net.BindException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.iflytek.cloud.SpeechRecognizer;
import com.stupidbeauty.victoriafresh.VFile;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

public class VoiceUi
{
  private int recognizeCounter=0; //!<识别计数器．
  private ErrorListener errorListener=null; //!< Error listener.
  private FtpServerErrorListener ftpServerErrorListener=null; //!< The ftp server error listner. Chen xin.
  private int port=1421; //!< Port.
  private FtpServer ftpServer=null; //!< Ftp server object.
  private boolean allowActiveMode=true; //!<  Whether to allow active mode.
  private static final String TAG="VoiceUi"; //!< 输出调试信息时使用的标记。
  private SpeechSynthesizer mIat; //!< 语言合成器。
  private String recordSoundFilePath; //!< 录音文件路径．
    
  public void setErrorListener(ErrorListener errorListener)    
  {
    this.errorListener = errorListener;
  } //public void setErrorListener(ErrorListener errorListener)    
    
  public void onError(Integer errorCode) 
  {
    if (errorListener!=null)
    {
      errorListener.onError(errorCode); // Report error.
    }
    else // Not listener
    {
      Exception ex = new BindException();
      throw new RuntimeException(ex);
    }
  } //public void onError(Integer errorCode)
    
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
        
  private VoiceUi() 
  {
  }

  public VoiceUi(Context context) 
  {
    this.context = context;
  }

  private Context context; //!< Context.

  public void say(String text)
  {
    assessInitializeMsc(); // 考虑要不要初始化讯飞语音合成。

    commandRecognizebutton2(text); // 开始说话。
  } //public void start()

  private final RecognizerListener mRecognizerListener=new RecognizerListener()
  {
    @Override
    public void onVolumeChanged(int i, byte[] bytes)
    {
    }

    @Override
    public void onBeginOfSpeech()
    {
    }

    @Override
    public void onEndOfSpeech()
    {
    } //public void onEndOfSpeech()

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b)
    {
      //完整内容:
      String text=recognizerResult.getResultString(); //结果字符串。
    } //public void onResult(RecognizerResult recognizerResult, boolean b)

    @Override
    public void onError(SpeechError speechError)
    {
      String errorText=speechError.getErrorDescription(); //获取错误信息。
      int errorCode=speechError.getErrorCode(); //获取错误码。
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) 
    {
    }
  };

  /**
  * 在线命令词识别。
  */
  @SuppressWarnings("StatementWithEmptyBody")
  public void commandRecognizebutton2(String text)
  {
    int ret = 0;

    recognizeCounter=recognizeCounter+1; //计数．

    if (mIat==null) //识别器未创建。
    {
      mIat= SpeechSynthesizer.createSynthesizer(context, null); // 创建识别器。
    } //if (mIat==null) //识别器未创建。

    if (mIat==null) //仍然创建失败。
    {
    } //if (mIat==null) //仍然创建失败。
    else //创建成功。
    {
      if (!setParam()) //参数设置失败。
      {
        return;
      } //if (!setParam()) //参数设置失败。

      ret = mIat.startSpeaking(text, null); // 开始合成。
      if (ret != ErrorCode.SUCCESS) //未能启动识别
      {
        if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED)
        {
          // 未安装则跳转到提示安装页面
          // mInstaller.install();
        }
      } //if (ret != ErrorCode.SUCCESS) //未能启动识别

      String myString = "Messages";
      int myInt = 12;
    } //else //创建成功。
  } //public void commandRecognizebutton2()

        /**
     * 设置语言及区域参数字符串。
     */
    private void setLanguageAndAccentParameters()
    {
      boolean foundDirectLanguage=false; //是否已经直接找到匹配的语言

      //获取系统当前的语言。
      Locale locale=Locale.getDefault(); //获取默认语系。

      String androidLocaleName=locale.toString(); //获取语系名字。

      Locale zhCnLocale=Locale.SIMPLIFIED_CHINESE;

      if (androidLocaleName.startsWith("zh_CN")) //简体中文。
      {
        foundDirectLanguage=true;
      } //简体中文。
      else if (androidLocaleName.startsWith("en")) //英语
      {
        foundDirectLanguage=true;
      } //else if (androidLocaleName.startsWith("en")) //英语
      else //其它语言。后面还有机会选择
      {
        foundDirectLanguage=false; //未直接找到匹配的语言
      } //else //英语。

      Locale[] locales = Locale.getAvailableLocales();
      ArrayList<String> localcountries=new ArrayList<String>();
      for(Locale l:locales)
      {
        localcountries.add(l.getDisplayLanguage().toString());
      }
      
      String[] languages=(String[]) localcountries.toArray(new String[localcountries.size()]);

      if (foundDirectLanguage) //直接找到了语言
      {
      } //if (foundDirectLanguage) //直接找到了语言
      else //未直接找到语言
      {
        LocaleList localeList;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) //选择多种语言
        {
          localeList= LocaleList.getDefault(); //获取默认语言列表

          int localeSize=localeList.size();

          int localeCounter=0;

          for(localeCounter=0; localeCounter< localeSize; localeCounter++)
          {
            Locale locale1=localeList.get(localeCounter);

            androidLocaleName=locale1.toString(); //获取语系名字。

            Log.d(TAG, "setLanguageAndAccentParameters, candidate language: " + androidLocaleName+ ", locale counter: " + localeCounter); //Debug.

            if (androidLocaleName.startsWith("zh_CN")) //简体中文。
            {
              foundDirectLanguage=true;
            } //简体中文。
            else if (androidLocaleName.startsWith("en")) //英语
            {
              foundDirectLanguage=true;
            } //else if (androidLocaleName.startsWith("en")) //英语
            else //其它语言。后面还有机会选择
            {
              foundDirectLanguage=false; //未直接找到匹配的语言
            } //else //英语。

            if (foundDirectLanguage) //找到了。
            {
              break; //不用再找了
            } //if (foundDirectLanguage) //找到了。
          } //for(localeCounter=0; localeCounter< localeSize; localeCounter++)
        } //if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) //选择多种语言
      } //else //未直接找到语言
    } //private void setLanguageAndAccentParameters()

    /**
     * 参数设置
     *
     * @return 是否设置成功。
     */
    public boolean setParam()
    {
      boolean result = false;

      if (mIat!=null) //识别器存在。
      {
        // 设置识别引擎
        String mEngineType = SpeechConstant.TYPE_CLOUD;

        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        setLanguageAndAccentParameters(); //设置语言及区域参数字符串。

        result = true;

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

        recordSoundFilePath=Environment.getExternalStorageDirectory() + "/voiceui/msc/tts."+ recognizeCounter +".wav"; //构造录音文件路径．

        mIat.setParameter(SpeechConstant.TTS_AUDIO_PATH, recordSoundFilePath); // 设置声音存储路径。

        mIat.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false"); //不获取焦点。
      } //if (mIat!=null) //识别器存在。

      return result;
    }

  /**
  * 考虑要不要初始化讯飞语音识别。
  */
  private void assessInitializeMsc()
  {
    long startTimestamp=System.currentTimeMillis(); // 记录开始时间戳。
    Log.w(TAG, "assessInitializeMsc, 1630, enter assessInitializeMsc, timestamp: " + System.currentTimeMillis()); //Debug.
    
    SpeechUtility uitily=SpeechUtility.getUtility(); // 尝试获取语音工具单实例。
    
    if (uitily!=null) //已经初始化。
    {
    } //if (mscIsInitialized) //已经初始化。
    else  //尚未初始化。
    {
      ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

      NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
      boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

      if (isConnected) //网络已经连接。
      {
        Log.w(TAG, "assessInitializeMsc, 1643, assessInitializeMsc, timestamp: " + System.currentTimeMillis()); //Debug.

        initializeMsc(); //初始化讯飞语音识别。

        Log.w(TAG, "assessInitializeMsc, 1647, assessInitializeMsc, timestamp: " + System.currentTimeMillis()); //Debug.
      } //if (isConnected) //网络已经连接。
    } //else  //尚未初始化。
    long endTimestamp=System.currentTimeMillis(); // 记录开始时间戳。
    Log.w(TAG, "assessInitializeMsc, 1649, leave assessInitializeMsc, timestamp: " + System.currentTimeMillis()); //Debug.
    Log.d(TAG, "assessInitializeMsc, 1650, time in assessInitializeMsc: " + (endTimestamp-startTimestamp)); // 报告，onCreate 所花的时间。
  } //private void assessInitializeMsc()
  
  /**
  * 初始化MSC。
  */
  private void initializeMsc()
  {
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    Log.w(TAG, "initializeMsc, 729, timestamp: " + System.currentTimeMillis() + ", active network: " + activeNetwork + ", is connected: " + isConnected); //Debug.

    Log.w(TAG, "initializeMsc, 720, timestamp: " + System.currentTimeMillis()); //Debug.

    SpeechUtility.createUtility(context, SpeechConstant.APPID + "=56e142d3"); //创建工具。
    Log.w(TAG, "initializeMsc, 723, timestamp: " + System.currentTimeMillis()); //Debug.

    mIat= SpeechSynthesizer.createSynthesizer(context, null);
    Log.w(TAG, "initializeMsc, 725, timestamp: " + System.currentTimeMillis()); //Debug.
  } //private void initializeMsc()
}
