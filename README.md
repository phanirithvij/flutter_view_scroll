This was taken from [flutter/examples](https://github.com/flutter/flutter/tree/master/examples/flutter_view/)

- There are 3 pages in a ViewPager
  - Page 0 is an android view (p0)
  - Page 1 is the flutter view (p1)
    - FlutterView has 3 pages and a translucent listener (l0) in a Stack
      - f0, f1, f2
  - Page 2 is an android view (p2)

Curently the scroll is implemented like this:

- App is initialized and the `vs`(viewpager scrolling) is enabled.
- `l0` listens for any horizontal pointer movements.
- After scrolling from p0 to p1 the `l0` will respond like this:
  - if finger is moving left (scrolling right)
    - `vs` is enabled if in `f0`
    - `vs` is disabled if in `f2`
  - if finger is moving right (scrolling left)
    - `vs` is disabled if in `f0`
    - `vs` is enabled if in `f2`
- When scrolled from `f1` to `f0`, `vs` is enabled
- When scrolled from `f1` to `f2`, `vs` is enabled
- Whenever `vs` is disabled the pointer will be read by pageview which will scroll the flutter pages.
- Whenever `vs` is enabled the pointer will be read by viewpager which will scroll the android views

Theoretically this should work but it isn't.
