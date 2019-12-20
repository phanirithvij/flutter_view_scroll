import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

void main() {
  runApp(FlutterView());
}

class FlutterView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter View',
      theme: ThemeData.dark(),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  PageController _scrollController = PageController();
  int _numPages = 3;
  PageView pageView;
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Rainbow(
            scrollController1: _scrollController,
          ),
          SmallContainer(),
          BigContainer(),
        ],
      ),
    );
  }
}

class Rainbow extends StatelessWidget {
  const Rainbow({
    Key key,
    @required PageController scrollController1,
  })  : _scrollController1 = scrollController1,
        super(key: key);

  final PageController _scrollController1;

  @override
  Widget build(BuildContext context) {
    return PageView(
      controller: _scrollController1,
      children: <Widget>[
        Container(color: Color.fromARGB(255, 148, 0, 211)),
        Container(color: Color.fromARGB(255, 75, 0, 130)),
        Container(color: Color.fromARGB(255, 0, 0, 255)),
        Container(color: Color.fromARGB(255, 0, 255, 0)),
        Container(color: Color.fromARGB(255, 255, 255, 0)),
        Container(color: Color.fromARGB(255, 255, 127, 0)),
        Container(color: Color.fromARGB(255, 255, 0, 0)),
      ],
    );
  }
}

class SmallContainer extends StatelessWidget {
  const SmallContainer({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 100,
        height: 100,
        child: GestureDetector(
          onDoubleTap: () => print('dtap on the smaller one'),
          onLongPress: () => print('hold on the smaller one'),
          behavior: HitTestBehavior.translucent,
          child: IgnorePointer(
            child: Container(
              color: Colors.black38,
              child: Container(
                child: Align(
                    alignment: Alignment.topCenter,
                    child: Text("Tap here", style: TextStyle(fontSize: 16))),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class BigContainer extends StatelessWidget {
  const BigContainer({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 300,
        height: 300,
        child: Listener(
          onPointerDown: (e) => print(e),
          onPointerMove: (e) => print(e),
          onPointerCancel: (e) => print(e),
          // onPointerSignal: (e) => print(e),
          onPointerUp: (e) => print(e),
          // onHorizontalDragStart: (e) {},
          behavior: HitTestBehavior.translucent,
          child: IgnorePointer(
            child: Container(
              color: Colors.black38,
              child: Container(
                child: Center(
                    child: Text("Drag on this container",
                        style: TextStyle(fontSize: 25))),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
