import { addIfTest } from "../box/infoBox.js";
import { tests } from "../box/infoBox.js";

export function linkBoxes(boxA, boxB) {
  var selectValue;
  if (boxA !== boxB) {
    var boxId = boxA.id;
    var lineId = boxId + "line";
    if (boxA.className.includes("switch") || boxA.className.includes("par"))
      lineId = boxA.id + "line" + boxB.id;

    if (boxA.className.includes("switch")) {
      //if switch get value
      var selectId = lineId + "select";
      var selectElmt = document.getElementById(selectId);

      if (selectElmt) {
        selectValue = selectElmt.value;
      }
    }

    if (document.getElementById(lineId) != undefined) {
      document
        .getElementById(lineId)
        .parentNode.removeChild(document.getElementById(lineId));
    }

    var boxADot = boxA.getElementsByClassName("lineDot")[1];
    var boxBDot = boxB.getElementsByClassName("lineDot")[0];

    boxADot.className = "lineDot connectedLineDot";
    boxBDot.className = "lineDot connectedLineDot";

    if (directionHorizontal) {
      //       var startingX = parseInt(boxA.style.left) + boxA.clientWidth;
      //       var startingY = parseInt(boxA.style.top) + boxA.clientHeight / 2;

      //       var endingX = parseInt(boxB.style.left);
      //       var endingY = parseInt(boxB.style.top) + boxB.clientHeight / 2;

      var startingX =
        boxADot.getBoundingClientRect().left +
        boxADot.getBoundingClientRect().width +
        window.scrollX;
      var startingY =
        boxADot.getBoundingClientRect().top +
        boxADot.getBoundingClientRect().height / 2 +
        window.scrollY;

      var endingX = boxBDot.getBoundingClientRect().left + window.scrollX;
      var endingY =
        boxBDot.getBoundingClientRect().top +
        boxBDot.getBoundingClientRect().height / 2 +
        window.scrollY;
    }

    var newLine = createLine(startingX, startingY, endingX, endingY);
    // newLine.setAttribute("onclick", "onLineClick(this)");
    newLine.setAttribute("id", lineId);
    newLine.setAttribute("class", "line");

    newLine = styleLine(newLine, boxA, endingX, endingY, selectValue);

    document.body.appendChild(newLine);
  }
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

function styleLine(newLine, boxA, endingX, endingY, selectValue) {
  // var selectValue;
  var startingX = parseInt(boxA.style.left) + boxA.clientWidth;
  var startingY = parseInt(boxA.style.top) + boxA.clientHeight / 2;

  //if switch
  if (boxA.className.includes("switch")) {
    newLine.style.borderColor = "orange";
    // newLine.className = "line orange";

    var middleX = Math.abs(startingX - endingX) / 2;
    var middleY = Math.abs(startingY - endingY) / 2;

    var select = document.createElement("select");
    select.style.marginTop = -20 + "px";
    select.style.marginLeft = middleX + "px";
    //  select.setAttribute("onchange", "selectOnClick(this)");

    select.addEventListener("change", function () {
      selectOnClick(event.target);
    });

    select.setAttribute("id", newLine.id + "select");
    select.setAttribute("class", "select");
    var lineAngle = newLine.getAttribute("angle");
    rotateElement(select, -lineAngle);

    for (var j = 0; j < tests.length; j++) {
      var option = document.createElement("option");
      option.setAttribute("value", tests[j]);
      option.innerHTML = tests[j];
      select.appendChild(option);
    }

    if (selectValue) select.value = selectValue;
    newLine.appendChild(select);
  }
  //if par
  if (boxA.className.includes("par")) newLine.style.borderColor = "purple";

  return newLine;
}

function selectOnClick(elmt) {
  if (elmt.value == "Add test...") {
    elmt.value = "Default";
    addIfTest(true);
  }
}

function rotateElement(elmt, rad) {
  // Code for Safari
  elmt.style.WebkitTransform = "rotate(" + rad + "rad)";
  // Code for IE9
  elmt.style.msTransform = "rotate(" + rad + "rad)";
  // Standard syntax
  elmt.style.transform = "rotate(" + rad + "rad)";
}

export function removeLine(lineId) {
  var line = document.getElementById(lineId);
  if (line) line.parentNode.removeChild(line);
}

//currently not called
function onSwitchLineClick(elmt) {
  console.log("switch line click");
}

export function drawMouseLine(event, elmt) {
  if (directionHorizontal) {
    // var startingX = parseInt(elmt.style.left) + elmt.clientWidth;
    // var startingY = parseInt(elmt.style.top) + elmt.clientHeight / 2;
    var elmtDot = getDot(elmt, 1);
    elmtDot.className = "lineDot connectingLineDot";

    var startingX =
      elmtDot.getBoundingClientRect().left +
      elmtDot.getBoundingClientRect().width +
      window.scrollX;
    var startingY =
      elmtDot.getBoundingClientRect().top +
      elmtDot.getBoundingClientRect().height / 2 +
      window.scrollY;
  } else {
    // var startingX = parseInt(elmt.style.left) + elmt.clientWidth / 2;
    // var startingY = parseInt(elmt.style.top) + elmt.clientHeight;
  }
  clearMouseLines(elmt);

  var mouseX = event.x + window.scrollX;
  var mouseY = event.y + window.scrollY;

  var mouseLineId = elmt.id + "mouseLine";
  var newLine = createLine(startingX, startingY, mouseX, mouseY);
  newLine.id = mouseLineId;
  newLine = styleLine(newLine, elmt, mouseX, mouseY);
  document.body.appendChild(newLine);

  if (!elmt.className.includes("switch") && !elmt.className.includes("par")) {
    var oldLineId = elmt.id + "line";
    removeLine(oldLineId);
  }
}

export function clearMouseLines(elmt) {
  var mouseLineId = elmt.id + "mouseLine";
  removeLine(mouseLineId);

function getDot(elmt, dotInt) {
  var dot = elmt.getElementsByClassName("lineDot")[dotInt];
  return dot;
}

export function elementUnconnected(elmt){
  var leftDot = getDot(elmt,0);
  var rightDot=getDot(elmt,1);
  leftDot.className="lineDot";
  rightDot.className="lineDot";
}