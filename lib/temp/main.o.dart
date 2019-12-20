import 'package:flutter/material.dart';

void main() {
  runApp(FlutterView());
}

class FlutterView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter View',
      theme: ThemeData(
        primarySwatch: Colors.grey,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  @override
  void initState() {
    super.initState();
  }

  void _handleIncrement() async {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Expanded(
            child: Center(
              child: Text(
                  'Button tapped $_counter time${_counter == 1 ? '' : 's'}.',
                  style: const TextStyle(fontSize: 17.0)),
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
      floatingActionButton: FloatingActionButton(
        onPressed: _handleIncrement,
        child: const Icon(Icons.add),
      ),
    );
  }
}
