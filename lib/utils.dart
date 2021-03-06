import 'package:flutter/material.dart';

/// An Amoled theme (kinda)
ThemeData buildAmoledTheme() {
  // reference : https://github.com/bimsina/wallpaper/blob/master/lib/bloc/utils.dart#L34
  final ThemeData base = ThemeData.dark();
  return base.copyWith(
    // Floating action theme is me just testing it
    floatingActionButtonTheme: FloatingActionButtonThemeData(
      backgroundColor: Colors.black,
      // Color.lerp(
      //   Colors.green.shade500,
      //   Colors.blue.shade500,
      //   .5,
      // ),
      foregroundColor: Colors.white,
    ),
    primaryColor: Colors.black,
    accentColor: Colors.white,
    canvasColor: Colors.transparent,
    primaryIconTheme: IconThemeData(color: Colors.black),
    textTheme: TextTheme(
      headline: TextStyle(color: Colors.white, fontSize: 24),
      body1: TextStyle(color: Colors.white, fontSize: 24),
      body2: TextStyle(color: Colors.white, fontSize: 18),
    ),
  );
}
