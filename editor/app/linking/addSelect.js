//adds select (dropdown) to line on a Switch element
import { addIfTest } from "../box/infoBox.js";
import { tests } from "../box/infoBox.js";


export function addLineSelect(startingX,startingY,endingX,endingY,newLine) {
  var middleX = Math.abs(startingX - endingX) / 2;
  var middleY = Math.abs(startingY - endingY) / 2;

  var select = document.createElement("select");
  select.style.marginTop = -20 + "px";
  select.style.marginLeft = middleX + "px";

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

  newLine.appendChild(select);
  
  return newLine;
}

// called when select value on Switch line is changed
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


