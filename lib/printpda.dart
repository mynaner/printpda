/*
 * @Date: 2024-05-28 11:26:52
 * @LastEditors: dengxin 994386508@qq.com
 * @LastEditTime: 2024-08-09 15:01:46
 * @FilePath: /ywtg_patrol/plugins/printpda/lib/printpda.dart
 */
import 'package:flutter/services.dart';

class Printpda {
  final methodChannel = const MethodChannel('print_flutter_plugin');

  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  Future<bool?> init() async {
    return methodChannel.invokeMethod<bool>('init');
  }

  Future<bool?> printText(PrintTextVo params) async {
    return methodChannel.invokeMethod<bool>('printText', params.toJson());
  }

  Future<bool?> printQR(PrintQRVo params) async {
    return methodChannel.invokeMethod<bool>('printQR', params.toJson());
  }
}

class PrintTextVo {
  int align = 0;
  int size = 0;
  String? text;

  PrintTextVo({this.align = 0, this.size = 0, this.text});

  PrintTextVo.fromJson(Map<String, dynamic> json) {
    align = json['align'];
    size = json['size'];
    text = json["text"];
  }

  Map<String, dynamic> toJson() {
    final data = <String, dynamic>{};

    data['align'] = align;
    data['size'] = size;
    data['text'] = text;

    return data;
  }
}

class PrintQRVo {
  int offset = 5;
  int height = 384;
  String? text;

  PrintQRVo({this.offset = 5, this.height = 384, this.text});

  PrintQRVo.fromJson(Map<String, dynamic> json) {
    offset = json['offset'];
    height = json['height'];
    text = json["text"];
  }

  Map<String, dynamic> toJson() {
    final data = <String, dynamic>{};

    data['offset'] = offset;
    data['height'] = height;
    data['text'] = text;

    return data;
  }
}
