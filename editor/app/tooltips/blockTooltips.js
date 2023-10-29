//called whenever a new element is added, refreshes all tips
export function refreshBlockTooltips() {
  tippy(".audioTip", {
    content: "Choose an audio file to play",
  });

  tippy(".ttsTip", {
    content: "Add text to be read aloud. Drag the bottom right corner of the textbox for more space.",
   
  });

  tippy(".dynqueryTip", {
    content: "Reads aloud the values in the Previewer Values tab in the editor, and the values from your phone on the Android App",
  });

  tippy(".switchTip", {
    content: "This element will choose its path based on the tests and values in the Previewer Values tab. This element can connect to multiple elements.",
  });

  tippy(".switchEndTip", {
    content: "Put element paths in between the Switch Start and Switch End elements to have the previewer choose one path.",
  });

  tippy(".parTip", {
    content: "Add elements in between the Parallel Start and Parallel End Elements. This element can connect to multiple elements, see tutorial for more.",
  });

  tippy(".parEndTip", {
    content: "Add elements in between the Parallel Start and Parallel End Elements to play the paths simultaneously.",
  });

  tippy(".pauseTip", {
    content: "Enter length of pause in seconds",
  });

  tippy(".vibrateTip", {
    content: "Enter length of vibrate in seconds (in this editor it will pause, in the Android app it will vibrate)",
  });
  
  tippy(".changeTimeTip", {
    content: "The audio will start at 0:00 and play to the end by default. Change the Start time and/or Duration below. Enter times in minutes.",
  });  
 
}
