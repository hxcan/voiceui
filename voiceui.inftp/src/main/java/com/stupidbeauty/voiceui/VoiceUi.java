package com.stupidbeauty.voiceui;

import com.google.gson.Gson;
import com.stupidbeauty.hxlauncher.bean.VoicePackageMapJsonItem;
import com.stupidbeauty.hxlauncher.bean.VoicePackageUrlMapData;
import com.stupidbeauty.hxlauncher.bean.WakeLockPackageNameSetData;
import com.stupidbeauty.hxlauncher.datastore.RuntimeInformationStore;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
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
import com.stupidbeauty.voiceui.R;

public class VoiceUi
{
  private VoicePackageUrlMapData voicePackageUrlMapData; //!<语音识别结果与软件包下载地址之间的映射。
  private int recognizeCounter=0; //!<识别计数器．
  private ErrorListener errorListener=null; //!< Error listener.
  private FtpServerErrorListener ftpServerErrorListener=null; //!< The ftp server error listner. Chen xin.
  private int port=1421; //!< Port.
  private FtpServer ftpServer=null; //!< Ftp server object.
  private boolean allowActiveMode=true; //!<  Whether to allow active mode.
  private static final String TAG="VoiceUi"; //!< 输出调试信息时使用的标记。
  private SpeechSynthesizer mIat; //!< 语言合成器。
  private String recordSoundFilePath; //!< 录音文件路径．
  private MediaPlayer mediaPlayer;
  private static final float BEEP_VOLUME = 0.20f;
  private HashMap<String, String> voiceUiTextSoundFileMap=null; //!< 声音内容与声音文件名之间的映射关系。

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
  
  /**
  * 载入映射文件。
  */
  private HashMap<String, String> loadVoiceUiTextSoundFileMap() 
  {
    HashMap<String, String> packageNameApplicationNameMap=new HashMap<>(); // 结果。
    
      try 
    {
      String qrcFileName="voiceSoundMap.json"; //文件名。
//       String qrcFileName=voiceUiTextSoundFileMap.get(text); // 声音文件名。

      String fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
      
      int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。
      int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", context.getPackageName()); //获取索引文件编号。


//       mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。

//       byte[] fileContent= qrcHtmlFile.getFileContent(); //将照片文件内容全部读取。
      String fileContent=qrcHtmlFile.getFileTextContent(); //获取文件的完整内容。

      Gson gson=new Gson();

      voicePackageUrlMapData = gson.fromJson(fileContent, VoicePackageUrlMapData.class); //解析。

// 		voicePackageUrlMap=new HashMap<>(); //创建映射。
// 		packageNameUrlMap=new HashMap<>(); //创建映射
// 		packageNameVersionNameMap=new HashMap<>(); // 创建映射。陈欣
		packageNameApplicationNameMap=new HashMap<>(); //创建映射

		if (voicePackageUrlMapData!=null) //解析得到的映射数据不为空。
		{
          for(VoicePackageMapJsonItem currentItem: voicePackageUrlMapData.getVoicePackageMapJsonItemList()) //一个个地添加。
          {
//             voicePackageUrlMap.put(currentItem.voiceCommand, currentItem.packageUrl); //加入映射。
//             packageNameUrlMap.put(currentItem.getPackageName(), currentItem.packageUrl); //加入映射。
//             packageNameVersionNameMap.put(currentItem.getPackageName(), currentItem.versionName); // 加入映射。
            packageNameApplicationNameMap.put( currentItem.getPackageName(),currentItem.voiceCommand); //加入映射，包名与应用程序名的映射
          } //for(VoicePackageMapJsonItem currentItem: voicePackageUrlMapData.getVoicePackageMapJsonItemList()) //一个个地添加。
		} //if (voicePackageUrlMapData!=null) //解析得到的映射数据不为空。
    }
    catch (Exception ioe) 
    {
      Log.w(TAG, ioe);
      mediaPlayer = null;
    }

    return packageNameApplicationNameMap;
  } // private void loadVoiceUiTextSoundFileMap()

  private MediaPlayer buildMediaPlayer(Context activity, String text)
  {
    voiceUiTextSoundFileMap = loadVoiceUiTextSoundFileMap(); // 载入映射文件。
  
    MediaPlayer mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.victoriafreshdata_voiceui); //提示音。
    
    try 
    {
//       String qrcFileName="voicePackageNameMap.ost"; //文件名。
      String qrcFileName=voiceUiTextSoundFileMap.get(text); // 声音文件名。

      String fullQrcFileName=":/VoiceUi/"+qrcFileName; //构造完整的qrc文件名。
      
      int victoriaFreshDataFileId=context.getResources().getIdentifier("victoriafreshdata_voiceui", "raw", context.getPackageName()); //获取数据文件编号。
      int victoriaFreshIndexFileId=context.getResources().getIdentifier("victoriafresh_voiceui", "raw", context.getPackageName()); //获取索引文件编号。


//       mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      VFile qrcHtmlFile=new VFile(context, victoriaFreshIndexFileId, victoriaFreshDataFileId, fullQrcFileName); //qrc网页文件。

      int soundLength=qrcHtmlFile.getLength();
      int soundStartOffset=qrcHtmlFile.getStartOffset();

      mediaPlayer.setDataSource(file.getFileDescriptor(), soundStartOffset, soundLength); // 设置数据源。
      
      file.close();
      mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
      mediaPlayer.prepare();
    }
    catch (IOException ioe) 
    {
      Log.w(TAG, ioe);
      mediaPlayer = null;
    }
    return mediaPlayer;
  }

  public VoiceUi(Context context) 
  {
    this.context = context;
  }

  private Context context; //!< Context.

  public void say(String text)
  {
//     assessInitializeMsc(); // 考虑要不要初始化讯飞语音合成。

    mediaPlayer = buildMediaPlayer(context, text);

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
}
