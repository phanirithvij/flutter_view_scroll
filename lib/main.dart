import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_view/utils.dart';

void main() {
  runApp(FlutterView());
}

class FlutterView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter View',
      theme: buildAmoledTheme(),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const String _channel = 'increment';
  static const String _pong = 'pong';
  static const String _emptyMessage = '';
  static const BasicMessageChannel<String> platform =
      BasicMessageChannel<String>(_channel, StringCodec());

  var _enabled = false;
  int _counter = 0;

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
    _handleScroll(_enabled);
  }

  Future<String> _handlePlatformIncrement(String message) async {
    setState(() {
      _counter++;
    });
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    platform.send(_pong);
  }

  void _handleScroll(bool value) {
    platform.send("${value ? 'enable' : 'disable'}Scroll");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: <Widget>[
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Expanded(
                child: Center(
                  child: Text(
                    'Platform button tapped $_counter time${_counter == 1 ? '' : 's'}.',
                    style: const TextStyle(fontSize: 17.0),
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.only(bottom: 15.0, left: 5.0),
                child: Row(
                  children: <Widget>[
                    FlutterLogo(size: 43),
                    const Text('Flutter', style: TextStyle(fontSize: 30.0)),
                  ],
                ),
              ),
            ],
          ),
          PageView(
            children: <Widget>[
              Container(color: Colors.amber.withOpacity(0.5)),
              Container(color: Colors.orange.withOpacity(0.5)),
              Container(color: Colors.red.withOpacity(0.5)),
            ],
          ),
          Center(
            child: Switch(
              value: _enabled,
              onChanged: (bool value) {
                _handleScroll(value);
                setState(() => _enabled = value);
              },
            ),
          )
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _sendFlutterIncrement,
        child: const Icon(Icons.add),
      ),
    );
  }
}
