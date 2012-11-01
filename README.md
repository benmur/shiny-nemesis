shiny-nemesis
=============

Video mood extractor

What is this?
-------------

shiny-nemesis is an experiment about how to express the visual mood of a video. Being an experiment, it won't work on all video files without a few fixes, and won't be exciting for more than a few minutes.

If you would like to hack on shiny-nemesis to add new visualization ideas, make it more stable, faster, easier to use, integrated with media center software, or whatever, Github pull requests are the way to go.

What does it look like?
----------------------

Two quick steps to see for yourself (provided you have sbt 0.12 installed):

1. sbt assembly
2. java -jar target/shinynemesis-VERSION-fat.jar /path/to/videofile1 [[/path/to/videofile2] ...]

Images will then be output in the current directory.

No really, what does it look like?
---------------------------------

[image preview todo]

Will it work?
-------------

shiny-nemesis currently uses Xuggler for video decoding, which in turn uses ffmpeg. Some media files won't decode at all, some will make shiny-nemesis loop indefinitely. Both issues are fixable indeed, pull requests are very welcome.

Authors/Licensing?
------------------

shiny-nemesis is:

- (c) 2012 Rached Ben Mustapha <rached@benmur.net>
- licensed under the MIT license, please see the LICENSE file for details.

What is that name about?
------------------------

Github name suggestions for new repositories sometimes hit the bull's eye.

