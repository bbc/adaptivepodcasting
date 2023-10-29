import {addLineSelect} from "../linking/addSelect.js";

//create line between the dot being dragged and the user's mouse
export function getMouseLine(blockDot, event) {
  var startingX =
    blockDot.getBoundingClientRect().left +
    blockDot.getBoundingClientRect().width +
    window.scrollX;
  var startingY =
    blockDot.getBoundingClientRect().top +
    blockDot.getBoundingClientRect().height / 2 +
    window.scrollY;

  var mouseX = event.x + window.scrollX;
  var mouseY = event.y + window.scrollY;

  var newLine = createLine(startingX, startingY, mouseX, mouseY);

  return newLine;
}


export function getLinkingLine(firstDot,secondDot,isSwitch){
  var startingX =
    firstDot.getBoundingClientRect().left +
    firstDot.getBoundingClientRect().width +
    window.scrollX;
  var startingY =
    firstDot.getBoundingClientRect().top +
    firstDot.getBoundingClientRect().height / 2 +
    window.scrollY;

   var endingX =
    secondDot.getBoundingClientRect().left +
    secondDot.getBoundingClientRect().width +
    window.scrollX;
  var endingY =
    secondDot.getBoundingClientRect().top +
    secondDot.getBoundingClientRect().height / 2 +
    window.scrollY;
  
  
 var newLine = createLine(startingX, startingY, endingX, endingY);
  
  

if(isSwitch) newLine = addLineSelect(startingX,startingY,endingX,endingY,newLine);
  
  return newLine;
  
}


function createLine(x1, y1, x2, y2) {
  var a = x1 - x2,
    b = y1 - y2,
    c = Math.sqrt(a * a + b * b);

  var sx = (x1 + x2) / 2,
    sy = (y1 + y2) / 2;

  var x = sx - c / 2,
    y = sy;

  var alpha = Math.PI - Math.atan2(-b, a);

  return createLineElement(x, y, c, alpha);
}

function createLineElement(x, y, length, angle) {
  var line = document.createElement("div");
  var styles =
    "border: 1.4px solid black;" +
    "width: " +
    length +
    "px; " +
    "height: 0px; " +
    "-moz-transform: rotate(" +
    angle +
    "rad); " +
    "-webkit-transform: rotate(" +
    angle +
    "rad); " +
    "-o-transform: rotate(" +
    angle +
    "rad); " +
    "-ms-transform: rotate(" +
    angle +
    "rad); " +
    "position: absolute; " +
    "top: " +
    y +
    "px; " +
    "left: " +
    x +
    "px; ";
  line.setAttribute("style", styles);
  line.setAttribute("angle", angle);
  return line;
}


