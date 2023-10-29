//handles lines between elements
import { getMouseLine } from "../linking/getLine.js";
import { getLinkingLine } from "../linking/getLine.js";

//Draw line from lineDot to user's mouse
export function drawMouseLine(event, blockDot, block) {
  blockDot.className = "lineDot connectingLineDot";

  removeOldLine("mouseLine");

  var newLine = getMouseLine(blockDot, event);

  if (block.className.includes("switch")) newLine.style.borderColor = "orange";
  else if (block.className.includes("par"))
    newLine.style.borderColor = "purple";

  newLine.id = "mouseLine";
  document.body.appendChild(newLine);
}

function removeOldLine(id) {
  var oldLine = document.getElementById(id);
  if (oldLine) oldLine.parentNode.removeChild(oldLine);
}

function getDot(block, dotInt) {
  var dot = block.getElementsByClassName("lineDot")[dotInt];
  return dot;
}

export function linkFailed(blockDot, block) {
  removeOldLine("mouseLine");
  blockDot.className = "lineDot";
}

//draw a line between two elements
export function drawLinkingLine(first, second) {
  var firstDot = first.getElementsByClassName("lineDot")[1];
  var secondDot = second.getElementsByClassName("lineDot")[0];

  firstDot.className = "lineDot connectedLineDot";
  secondDot.className = "lineDot connectedLineDot";

  removeOldLine("mouseLine"); // remove the line that was drawn to the user's mouse

  var lineId = first.id + "line" + second.id;
  var selectId = lineId + "select";
  var oldSelect = document.getElementById(selectId);
  var oldSelectValue;
  if (oldSelect) oldSelectValue = oldSelect.value;

  removeOldLine(lineId);
  var isSwitch = first.className.includes("switch");

  var line = getLinkingLine(firstDot, secondDot, isSwitch);
  line.id = lineId;

  if (first.className.includes("switch")) {
    line.style.borderColor = "orange";

    var newSelect = line.getElementsByTagName("select")[0];
    newSelect.id = selectId;
    if (oldSelectValue) newSelect.value = oldSelectValue;
  } else if (first.className.includes("par")) line.style.borderColor = "purple";
  document.body.appendChild(line);
}

export function breakLinkingLine(first, second) {
  if (!first || !second) return;

  var lineId;
  lineId = first.id + "line" + second.id;
  removeOldLine(lineId);

//get the right dot of the first element, and the left dot of the second
  var firstDot = first.getElementsByClassName("lineDot")[1];
  var secondDot = second.getElementsByClassName("lineDot")[0];

  firstDot.className = "lineDot";
  secondDot.className = "lineDot";
}
