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
  int _numPages = 3;
  var _controller = PageController();

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
    _handleScroll(_enabled);
  }

  Future<String> _handlePlatformIncrement(String message) async {
    if (message == "ping") {
      setState(() {
        _counter++;
      });
    } else if (message == "scrolled") {
      // Scrolled out of android view
      print("object");
    }
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    platform.send(_pong);
  }

  void _handleScroll(bool value) {
    platform.send("${value ? 'enable' : 'disable'}Scroll");
    _enabled = value;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: <Widget>[
          TempWidget(counter: _counter),
          PageView(
            controller: _controller,
            children: <Widget>[
              Page(color: Colors.amber.withOpacity(0.5)),
              Page(color: Colors.orange.withOpacity(0.5)),
              Page(color: Colors.red.withOpacity(0.5)),
            ],
          ),
          Center(
            child: Switch(
              value: _enabled,
              onChanged: (bool value) {
                _handleScroll(value);
                setState(() {});
              },
            ),
          ),
          Listener(
            // onHorizontalDragUpdate: (e) => print(e),
            onPointerMove: _onPointerMove,
            behavior: HitTestBehavior.translucent,
            child: IgnorePointer(child: Container(color: Colors.transparent)),
          )
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _sendFlutterIncrement,
        child: const Icon(Icons.add),
      ),
    );
  }

  void _onPointerMove(PointerMoveEvent event) {
    if (event.delta.dx.abs() >= event.delta.dy.abs()) {
      // Horizontal movement
      if (event.delta.dx < 0) {
        // Drag left
        if (_controller.hasClients) {
          // print(_controller.page);
          if (_controller.page >= _numPages - 1 - 0.5) {
            // print("drag left in last page");
            _handleScroll(true);
            setState(() {});
          }
        }
      } else {
        if (_controller.hasClients) {
          // print(_controller.page);
          if (_controller.page >= _numPages - 2) {
            // print("drag right in last page");
            _handleScroll(false);
            setState(() {});
          }
        }
      }
    }
  }
}

class Page extends StatelessWidget {
  final Color color;
  const Page({
    Key key,
    @required this.color,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      color: color,
    );
  }
}

class TempWidget extends StatelessWidget {
  const TempWidget({
    Key key,
    @required int counter,
  })  : _counter = counter,
        super(key: key);

  final int _counter;

  @override
  Widget build(BuildContext context) {
    return Column(
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
    );
  }
}
