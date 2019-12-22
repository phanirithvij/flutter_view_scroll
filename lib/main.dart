import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter_view/utils.dart';

void main() {
  runApp(FlutterView());
}

class FlutterView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Page View concat',
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
  static const String _channel = 'com.example.view/increment';
  static const String _pong = 'pong';
  static const String _emptyMessage = '';
  static const BasicMessageChannel<String> platform =
      BasicMessageChannel<String>(_channel, StringCodec());

  var _enabled = true;
  int _counter = 0;
  int _numPages = 3;
  var _controller = PageController();
  var _lastPageReached = false;
  var _firstPageReached = true;
  bool _scrolledLeftInLastPage = false;
  bool _scrolledRightInFirstPage = false;

  var _noScroll = NeverScrollableScrollPhysics();
  var _canScroll = PageScrollPhysics();

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
    // Explicitly disable initial scrolling
    _handleScroll(_enabled);
    _controller.addListener(() {
      if (_controller.position.userScrollDirection == ScrollDirection.reverse) {
        // Scroll to the right
        if (_controller.page >= _numPages - 1.5) {
          // If page is last page
          // TODO: CANTFIX: A bug
          // When scrolling from page 3 to android
          // The scrolling jerks for a second
          // If setState is called

          if (!_lastPageReached) {
            // Send the scroll enable event only once
            _handleScroll(true);
            setState(() {
              // print("setState 59");
            });
          }
          _lastPageReached = true;
          _scrolledLeftInLastPage = false;
        } else {
          // If not in last page
          _lastPageReached = false;
        }
      } else {
        // Scroll to the left
        if (_controller.page <= 0.5) {
          // If page is first page
          if (!_firstPageReached) {
            // Send the scroll enable event only once
            _handleScroll(true);
            setState(() {
              print("setState 59");
            });
          }
          _firstPageReached = true;
          _scrolledRightInFirstPage = false;
        } else {
          // If not in first page
          _firstPageReached = false;
        }
      }
    });
  }

  Future<String> _handlePlatformIncrement(String message) async {
    if (message == "ping") {
      setState(() {
        _counter++;
      });
    }
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    platform.send(_pong);
  }

  // Enable/Disable the global viewpager scrolling
  void _handleScroll(bool value) {
    // print("Scroll event...");
    platform.send("${value ? 'enable' : 'disable'}Scroll");
    _enabled = value;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // To make the whole app look transparent
      backgroundColor: Colors.transparent,
      body: Stack(
        children: <Widget>[
          TempWidget(counter: _counter),
          PageView(
            // physics: _enabled ? _noScroll : _canScroll,
            onPageChanged: (pno) {
              print("Page changed to $pno");
            },
            controller: _controller,
            children: <Widget>[
              Page(color: Colors.amber.withOpacity(0.5)),
              Page(color: Colors.orange.withOpacity(0.5)),
              Page(color: Colors.red.withOpacity(0.5)),
            ],
          ),
          // A simple siwtch to see when it is getting enabled
          // And also manually enable/disable scrolling
          // Which will be immediately overriden by the subsequent scrolls
          Center(
            child: Switch(
              value: _enabled,
              onChanged: (bool value) {
                _handleScroll(value);
                setState(() {
                  //   // print("setState");
                });
              },
            ),
          ),
          // GestureDetector doesn't work here
          // That is explained in my comment in this issue
          // https://github.com/flutter/flutter/issues/47434#issuecomment-567820204
          Listener(
            onPointerMove: _onPointerMove,
            behavior: HitTestBehavior.translucent,
          )
        ],
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: "increment",
        splashColor: Colors.white54,
        onPressed: _sendFlutterIncrement,
        child: const Icon(Icons.add),
      ),
    );
  }

  // A callback for the Listener's on PointerMoveEvent
  void _onPointerMove(PointerMoveEvent event) {
    // print(_controller.page);
    // Look at event.delta's explaination
    if (_controller.hasClients) {
      // If the PageView is attached to this controller
      if (event.delta.dx.abs() >= event.delta.dy.abs()) {
        // X offset is greater than Y offset => Horizontal movement
        if (event.delta.dx > 0) {
          // Pointer moved along +ve x axis
          // Drag right i.e. scroll left
          // print(_controller.page);
          if (_controller.page > _numPages - 2) {
            // If the page is the last one
            // print("scroll left in last page $_scrolledLeftInLastPage");
            // Send disable scroll event to android
            _handleScroll(false);
            if (!_scrolledLeftInLastPage) {
              // Set state only once to prevent many rebuilds
              setState(() {
                // print("set state 189");
              });
            }
            _scrolledLeftInLastPage = true;
          } else {
            _scrolledLeftInLastPage = false;
          }

          print(_controller.page);
          if (_controller.page == 0) {
            // DONE: A Bug
            // hot restart
            // scroll from 1, 2, 1 then slightly scroll in 2's direction
            // the scroll gets disabled
            // So if user scrolls right
            // Scrolling right in last page
            // But the global scroll is disabled
            print("exactly ${0}");
            _handleScroll(true);
            setState(() {});
          }
        } else {
          if (_controller.page < 1) {
            // If not scrolled right in firsts page
            print("else if $_scrolledRightInFirstPage");
            _handleScroll(false);
            if (!_scrolledRightInFirstPage) {
              setState(() {});
            }
            _scrolledRightInFirstPage = true;
          } else {
            // If not scrolled right in first page
            _scrolledRightInFirstPage = false;
          }

          if (_controller.page == _numPages - 1) {
            // DONE: A Bug
            // hot restart
            // scroll from 1, 2, 3 then slightly scroll in 2's direction
            // the scroll gets disabled
            // So if user scrolls right
            // Scrolling right in last page
            // But the global scroll is disabled
            print("exactly ${_numPages - 1}");
            _handleScroll(true);
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

// A temporary widget to show the data transfer b/w flutter and jvm
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
