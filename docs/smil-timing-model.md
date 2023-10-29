# SMIL for adaptive podcasting

Adaptive podcasting uses the W3C’s SMIL timing model - [5. SMIL 3.0 Timing and Synchronization](https://www.w3.org/TR/SMIL3/smil-timing.html#q3).

This a powerful and objective way to create adaptive media. It's been tested over many applications and scenarios and so it made sense to use it rather than create a new model from scratch.

Each element exists within its own timeline when inside a structure element like a parallel, switch or sequence. A new sub timeline is created.

Below we can see a sequence with a parallel inside.

![SMIL demonstration From the W3C](https://user-images.githubusercontent.com/1649922/188498463-a60a0698-273f-463a-b1bf-9fc120df4cce.png)

![Our copy in the editor](https://user-images.githubusercontent.com/1649922/188498503-86b944f7-1dab-4030-8f86-4c23323368e1.png)

Each element or object has its own timeline independent of each other but, once nested, shares the timeline of the outer container.
In this case the text to speech after the parallel will never start until all the elements in the parallel have finished. This is ideal for elements which are dynamic or flexible.

---

There are many additonal elements in SMIL including advanced [content control](https://www.w3.org/TR/SMIL3/smil-content.html), [transitions/effects](https://www.w3.org/TR/SMIL3/smil-transitions.html), [time manipulations](https://www.w3.org/TR/SMIL3/smil-timemanip.html) and [media fallbacks](https://www.w3.org/TR/SMIL3/smil-timemanip.html#TimeManip-MediaFallbacks). In addition, SMIL treats all media a like, providing support for video, live text and animations.

These all point towards a extensible future for development of Adaptive podcasting.