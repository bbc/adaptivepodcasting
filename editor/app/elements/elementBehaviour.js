import { removeBlockConnections } from "../linking/linkingElmts.js";
import { movingBox } from "../linking/linkingElmts.js";

//second dropdown options depending on first dropdown value
var timeActions = ["hours-and-minutes", "of-day", "hours", "minutes"];
var batteryActions = ["percentage", "level", "is-charging", "charging-method"];
var user_locationActions = ["country", "city"];
var mediaVolumeActions = ["percentage", "level"];
var user_languageActions = ["language-name", "language-code"];
var dateActions = ["day","date","month","season","year"];

var horSpaceText = document.getElementById("horSpaceText");
var verSpaceText = document.getElementById("verSpaceText");
initSpaceText();


//make element draggable
export function dragElement(elmt) {
  var pos1 = 0,
    pos2 = 0,
    pos3 = 0,
    pos4 = 0;

  if (elmt.getElementsByClassName("blockheader")[0]) {
    /* if present, the header is where you move the DIV from:*/
    elmt.getElementsByClassName("blockheader")[0].onmousedown = dragMouseDown;
  } else {
    /* otherwise, move the DIV from anywhere inside the DIV:*/
    elmt.onmousedown = dragMouseDown;
  }

  function dragMouseDown(e) {
    e = e || window.event;
    e.preventDefault();
    // get the mouse cursor position at startup:
    pos3 = e.clientX;
    pos4 = e.clientY;
    document.onmouseup = closeDragElement;
    // call a function whenever the cursor moves:
    document.onmousemove = elementDrag;
  }

  function elementDrag(e) {
    e = e || window.event;
    e.preventDefault();
    // calculate the new cursor position:
    pos1 = pos3 - e.clientX;
    pos2 = pos4 - e.clientY;
    pos3 = e.clientX;
    pos4 = e.clientY;
    // set the element's new position:
    elmt.style.top = elmt.offsetTop - pos2 + "px";
    elmt.style.left = elmt.offsetLeft - pos1 + "px";
    movingBox(elmt);
  }

  function closeDragElement() {
    /* stop moving when mouse button is released:*/
    document.onmouseup = null;
    document.onmousemove = null;
  }
}

export function closeButtonPressed(element) {
  if (confirm("Delete this element?")) {
  

    var thisClass = element.getAttribute("class");
    if (thisClass.includes("switch")) {
      var endSwitchId = element.getAttribute("endSwitch");
      var end = document.getElementById(endSwitchId);
      deleteElement(end);
    } else if (thisClass.includes("par")) {
      var endParId = element.getAttribute("endPar");
      var end = document.getElementById(endParId);
      deleteElement(end);
    }
      deleteElement(element);
    
  }
}

function deleteElement(element) {
  removeBlockConnections(element);
  element.parentNode.removeChild(element);
}

//updates dropdown values
export function updateDynqueryDropdown(elmt) {
  let selects = elmt.getElementsByTagName("select");
  let actionsSelect = selects[1];

  var category = selects[0].value;
  var actions;

  if (category == "TIME") actions = timeActions;
  else if (category == "BATTERY") actions = batteryActions;
  else if (category == "USER_LOCATION") actions = user_locationActions;
  else if (category == "MEDIA_VOLUME") actions = mediaVolumeActions;
  else if (category == "USER_LANGUAGE") actions = user_languageActions;
  else if (category == "DATE") actions = dateActions;
  else {
    actions = [];   
  }
  var newSelect = document.createElement("select");

  for (var i = 0; i < actions.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", actions[i]);
    option.innerHTML = actions[i];
    newSelect.appendChild(option);
  }
  actionsSelect.parentNode.replaceChild(newSelect, actionsSelect);
  if(actions.length===0){
    newSelect.className="hidden";
  }
  
}



function initSpaceText() {
  horSpaceText.style.left = horSpaceText.offsetLeft + "px";
  verSpaceText.style.top = verSpaceText.offsetTop + "px";
}

export function addSpace() {
  horSpaceText.style.left = horSpaceText.offsetLeft + 700 + "px";
  window.scrollBy(700, 0);
}

export function addSpaceVertical() {
  verSpaceText.style.top = verSpaceText.offsetTop + 500 + "px";
  window.scrollBy(0, 500);
}