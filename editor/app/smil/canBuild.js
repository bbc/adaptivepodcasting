//Checks if everything is okay to create smil file or play preview
import { idCount } from "../interface/creating.js";
import { getAudioElmtFile } from "../zip/tempFiles.js";
import { allTimeFormatsCorrect } from "../tooltips/errorTips.js";
import { createElementArray } from "../smil/smilCreation.js";

export var startingId;


export function checkAllConnected() {
  startingId = undefined; //reset starting element
  var unconnectedElements = 0;
  var blankAudio = false;
  for (var j = 0; j < idCount + 1; j++) {
    var element = document.getElementById(j);
    if (element) {
      var nextId = element.getAttribute("nextid");
      var lastId = element.getAttribute("previd");
      var endChildren = element.getAttribute("endChildren");
      if (!lastId && !endChildren && startingId===undefined) startingId = element.id; //find the starting element
      var children = element.getAttribute("children");

      if (!nextId && !children) unconnectedElements++;
      //check audio elements have audio chosen
      if (element.className.includes("audio")) {
        var audioFile = getAudioElmtFile(element);

        if (audioFile === null || audioFile === undefined) {
          blankAudio = true;
        }
      }
    }
  }
  if (idCount == 0) {
    alert("There are no elements. Please add some elements.");
    return false;
  } else if (startingId == null || startingId == undefined) {
    alert(
      "You must have a starting element that does not have a previous connection."
    );
    return false;
  } else if (unconnectedElements > 1) {
    var text =
      "Not all elements are connected. Please delete or connect unconnected elements.";
    alert(text);
    return false;
  } else if (blankAudio) {
    var text = "Some audio elements don't have a file selected.";
    alert(text);
    return false;
  } else if (!allTimeFormatsCorrect()) {
    alert("Some audio elements have Start or End time format errors");
    return false;
  } else if (createElementArray() === null) {
    alert("You have a loop somewhere. Make sure your sequence has an ending");
    return false;
  } else return true;
}
