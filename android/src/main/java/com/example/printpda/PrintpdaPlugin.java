package com.example.printpda;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.util.GeneratedPluginRegister;

import android.content.Context;
import android.hardware.PrintLinkDev;
import android.os.SystemClock;
import com.jz.printerlibrary.common.Printer_Params;
import java.util.Map;
import io.flutter.Log;

/** PrintpdaPlugin */
public class PrintpdaPlugin  extends FlutterActivity implements FlutterPlugin, MethodCallHandler {

  private MethodChannel channel;
  private Context context;
  private static final String TAG = "打印机";
  private static PrintLinkDev mPrintLinkDev;
  boolean isOpen = false;

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    GeneratedPluginRegister.registerGeneratedPlugins(flutterEngine);
  }
 

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "print_flutter_plugin");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
    mPrintLinkDev = new PrintLinkDev(context);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    final Map<String, Object> arguments = call.arguments();
    switch (call.method){
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      // 初始化
      case "init":
        initPrint(result);
        break;
      case "printText":   int align = (int) arguments.get("align");
        int size = (int) arguments.get("size");
        String text = (String) arguments.get("text");
        printText(result,align,size,text);
        break;
//        二维码
      case "printQR":
        int offset = (int) arguments.get("offset");
        int height = (int) arguments.get("height");
        text = (String) arguments.get("text");
        printQR(result,offset,height,text);
        break;
      default:
        result.notImplemented();
        break;
    }
  }



  //  初始化
  public void initPrint(Result result){
    try {
      if(!isOpen) {
        mPrintLinkDev.OpenPrint();
        SystemClock.sleep(600);
        isOpen = mPrintLinkDev.openPort();
        result.success(isOpen);
      }
    }catch (Exception e){
      result.error(TAG,"初始化失败",null);
    }
  }

  //  printText
  private void printText(Result result, int align, int size,  String text) {
    try {

      if(align==0){
        mPrintLinkDev.PrintSetLayout(Printer_Params.ALIGN.LEFT);
      }else if(align==1){
        mPrintLinkDev.PrintSetLayout(Printer_Params.ALIGN.CENTER);
      }else if(align==2){
        mPrintLinkDev.PrintSetLayout(Printer_Params.ALIGN.RIGHT);
      }

      if(size==0){
        mPrintLinkDev.PrintFontMagnify(Printer_Params.TEXT_ENLARGE.NORMAL);
      }else{
        mPrintLinkDev.PrintFontMagnify(Printer_Params.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE);
      }

      if(size==0){
        mPrintLinkDev.PrintFontMagnify(Printer_Params.TEXT_ENLARGE.NORMAL);
      }else{
        mPrintLinkDev.PrintFontMagnify(Printer_Params.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE);
      }
      mPrintLinkDev.PrintChars(text);
      result.success(true);
    }catch (Exception e){
      result.error(TAG,"printText",e);
    }
  }


  private void printQR(Result result,int offset, int height, String content){
    try {

      mPrintLinkDev.PrintQRWidth(height);
      mPrintLinkDev.PrintQRPixel(offset);
      mPrintLinkDev.PrintQR(content);
      result.success(true);
    }catch (Exception e){
      result.error(TAG,"printQR",e);
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    context = null;
    if(isOpen) {
      int isclose = mPrintLinkDev.closePort();
      mPrintLinkDev.ClosePrint();
      isOpen = (isclose != 0);
      mPrintLinkDev = null;
    }
    channel.setMethodCallHandler(null);

  }
}
