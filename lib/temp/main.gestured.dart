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

  bool _enabled = false;
  int _counter = 0;
  int _scrollCount = 0;
  PageController _scrollController = PageController();
  // int _numPages = 3;
  PageView pageView;
  List<Widget> widgets = [];
  // var _lastPageReached = false;

  void dragCallback(DragEndDetails e) {
    print(_scrollCount);
    if (e.primaryVelocity > 0) {
      print("left");
      print(widgets.length);
      widgets.removeAt(widgets.length - 2);
      setState(() {});
      // remove gesture detector
    } else {
      print("right");
      // enable scrolling
      // _handleScroll(true);
      // setState(() {});
    }
  }

  GestureDetector gestureDetector;
  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
    _handleScroll(_enabled);
    // var _desired = _numPages - 1 - 0.4;
    // var _threshold = 0.03;
    _scrollController.addListener(() {
      print(_scrollController.page);
    });
    pageView = PageView(
      controller: _scrollController,
      children: <Widget>[
        Container(color: Colors.red.withOpacity(0.5)),
        Container(color: Colors.green.withOpacity(0.5)),
        Container(color: Colors.blue.withOpacity(0.5)),
      ],
    );
    gestureDetector = GestureDetector(
      child: Container(color: Colors.transparent),
      behavior: HitTestBehavior.deferToChild,
      onHorizontalDragCancel: () {
        print("details");
        // if (_scrollController.page.floor() == _numPages - 2) {}
      },
      onHorizontalDragStart: (e) => print(e),
      onHorizontalDragDown: (e) {
        print(e);
      },
      onHorizontalDragEnd: dragCallback,
    );
    initChildren();
    // _scrollController.addListener(() {
    //   if (_scrollController.page <= _desired + _threshold &&
    //       _scrollController.page >= _desired - _threshold) {
    //     print(_scrollController.page);
    //     print("last page reached");
    //     if (!_lastPageReached) {
    //       _handleScroll(true);
    //       setState(() {});
    //     }
    //     _lastPageReached = true;
    //   } else {
    //     if (_lastPageReached) {
    //       // _handleScroll(false);
    //       setState(() {});
    //     }
    //     _lastPageReached = false;
    //   }
    // });
  }

  Future<String> _handlePlatformIncrement(String message) async {
    // print("Received $message");
    if (message == "ping") {
      setState(() {
        _counter++;
      });
    } else if (message == "scrolled") {
      _scrollCount++;
      // if (_scrollCount >= 2) {
      // TODO: If received 'scrolled' more than once
      // That implies scrolling left in the flutter view
      // _handleScroll(false);
      // setState(() {
      //   _scrollCount = 0;
      // });
      // }
    }
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    platform.send(_pong);
  }

  void _handleScroll(bool value) {
    _enabled = value;
    platform.send("${value ? 'enable' : 'disable'}Scroll");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: widgets,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _sendFlutterIncrement,
        child: const Icon(Icons.add),
      ),
    );
  }

  void initChildren() {
    // if (_scrollController.page.floor() < _numPages)
    if (_scrollController.hasClients) {
      print("YEYEYEE");
    } else {
      print("NOOOO");
    }

    widgets = <Widget>[
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
      NotificationListener<OverscrollIndicatorNotification>(
        onNotification: ((notification) {
          if (notification is OverscrollIndicatorNotification) {
            if (notification.depth == 0) {
              if (notification.leading) {
                print("leading");
              } else {
                print("trailing");
                _handleScroll(true);
                setState(() {});
              }
            }
          }
          return true;
        }),
        child: pageView,
      ),
      gestureDetector,
      Center(
        child: Switch(
          value: _enabled,
          onChanged: (bool value) {
            _handleScroll(value);
            setState(() {
              print("set state for switch");
            });
          },
        ),
      ),
    ];
    // if (_scrollController.page.floor() < _numPages) {
    //   print(_scrollController.page);
    // }
  }
}
