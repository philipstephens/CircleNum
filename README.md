# CircleNum
Circle "graph" based on Vortex / Rodin / Tesla math and Mathologer on youtube.

Facts:

* Language         = Kotlin
* Framework        = Compose MultiPlatform for the desktop
* Mathologer Video = https://www.youtube.com/watch?v=6ZrO90AI0c8 1,567,504 views

Goals:

* Compile CircleNum to the Web if and when possible
* Translate CircleNum code for the web

Description of the math involved:

If you take a circle and divide it into 9 evenly labeled points along the perimeter and double each point the following pattern emerges.

2 x 1 = 2 (draw a line from 1 to 2)
2 x 2 = 4 (draw a line from 2 to 4)
2 x 4 = 8 (draw a line from 4 to 8)
2 x 8 = 16, 1+6 = 7 (draw a line from 8 to 7)
2 x 7 = 14, 1+4 = 5 (7 to 5)
2 x 5 = 10, 1+0 = 1 (5 to 1)
2 x 1 = 2, and the pattern repeats.

Also, for 3 and 6:
2 x 3 = 6
2 x 6 = 12, 1 + 2 = 3

For 9:
2 x 9 = 18, 1 + 8 = 9

2 is called the multiplier and 9 is the modulus.  As you can see from the app, other patterns are possible to create by adjusting the multiplier and the modulus.

CircleNum was programmed with a declarative style paradigm consisting of 3 states, namely the BUTTON_BAR, FULL_SCREEN, and SLIDESHOW states.

Button Bar State:  Shows the multiplier and the modulus of the current circle.  Both are adjustable via the button bar buttons.
Full Screen State: Shows the circle without a button bar.
Slide Show State:  Shows beautiful patterns based on various multiplier and modulus settings.

Footer: When the Button Bar state is active the footer reveals a random circle button used to create random circles.   Otherwise a show parameters button will show the parameters used to create the current circle, whether or not the circle parameters were invoked randomly.
