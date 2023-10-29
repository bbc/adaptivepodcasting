import { linkBoxes } from "../linking/drawingLines.js";
import { removeLine } from "../linking/drawingLines.js";
import { drawMouseLine } from "../linking/drawingLines.js";
import { clearMouseLines } from "../linking/drawingLines.js";
import {elementUnconnected}from "../linking/drawingLines.js";

var drawingLine = false;

var conA;
var conB;

export function onLineClick(elmt) {
  conA = elmt;
  drawingLine = true;
}

document.addEventListener("mousemove", function(event) {
  //draw line from box to mouse
  if (conA) drawMouseLine(event, conA);
});
document.addEventListener("mouseup", function(event) {
  if (drawingLine) {
    clearMouseLines(conA);
   // console.log(conB);
     if(conB===null)elementUnconnected(conA);
    drawingLine = false;
    conA = null;
   
    
  }
});

export function onMouseUp(elmt) {
  if (drawingLine) {
    clearMouseLines(conA);
    conB = elmt;
    connection();
  }
}

function connection() {
  linkBoxAttributes(conA, conB);
  linkBoxes(conA, conB);
}

function linkBoxAttributes(boxA, boxB) {
  if (boxA !== boxB) {
    if (!boxA.className.includes("switch") && !boxA.className.includes("par")) {
      var nextBlock = nextElement(boxA);
      var lastBlock = lastElement(boxB);

      if (nextBlock) breakConnection(boxA, nextBlock);
      // if(lastBlock){
      //   if(!lastBlock.className.includes("end")) breakConnection(lastBlock,boxB);
      // }
      boxA.setAttribute("nextid", boxB.id);
      boxB.setAttribute("lastid", boxA.id);
    } else if (boxA.className.includes("switch")) {
      // is Switch
      var currentChildren = "";

      if (boxA.getAttribute("switchChildren")) {
        //if already there
        currentChildren = boxA.getAttribute("switchChildren");
        var childrenArray = arrayFromString(currentChildren);

        if (childrenArray.indexOf(parseInt(boxB.id)) == -1)
          //only add id to array if it's not there already
          boxA.setAttribute("switchChildren", currentChildren + "," + boxB.id);
      } else if (boxA.className.includes("switch")) {
        // if no children yet
        boxA.setAttribute("switchChildren", "" + boxB.id.toString());
      }
      boxB.setAttribute("lastid", boxA.id);
    } else if (boxA.className.includes("par")) {
      // is par
      var currentChildren = "";

      if (boxA.getAttribute("parChildren")) {
        //if already there
        currentChildren = boxA.getAttribute("parChildren");
        var childrenArray = arrayFromString(currentChildren);

        // console.log(boxA.getAttribute("parChildren"));
        if (childrenArray.indexOf(parseInt(boxB.id)) == -1)
          //only add id to array if it's not there already
          boxA.setAttribute("parChildren", currentChildren + "," + boxB.id);
      } else if (boxA.className.includes("par")) {
        // if no children yet
        boxA.setAttribute("parChildren", "" + boxB.id.toString());
      }
      boxB.setAttribute("lastid", boxA.id);
    }
    //if box b is end switch or end par
    if (boxB.className.includes("end")) {
      //  console.log("linked to end element");

      var endChildren = boxB.getAttribute("endChildren");

      if (endChildren) {
        var childrenArray = arrayFromString(endChildren);
        var index = childrenArray.indexOf(parseInt(boxA.id)); //if not already in array
        if (index == -1)
          boxB.setAttribute("endChildren", endChildren + "," + boxA.id);
      } else {
        boxB.setAttribute("endChildren", boxA.id);
      }
    }
  } else {
    //if element is trying to link to itself
    removeBlockConnections(boxA);
  }
}

export function removeBlockConnections(elmt) {
  var next = nextElement(elmt);
  if (next) breakConnection(elmt, next);

  var last = lastElement(elmt);
  if (last) breakConnection(last, elmt);

  if (elmt.className.includes("switch")) {
    var childrenIds = arrayFromString(elmt.getAttribute("switchChildren"));
    for (var v = 0; v < childrenIds.length; v++) {
      var child = document.getElementById(childrenIds[v]);
      if (child) breakConnection(elmt, child);
    }
  }

  if (elmt.className.includes("par")) {
    var childrenIds = arrayFromString(elmt.getAttribute("parChildren"));
    for (var v = 0; v < childrenIds.length; v++) {
      var child = document.getElementById(childrenIds[v]);
      if (child) breakConnection(elmt, child);
    }
  }

  if (elmt.className.includes("end")) {
    var childrenIds = arrayFromString(elmt.getAttribute("endChildren"));
    for (var v = 0; v < childrenIds.length; v++) {
      var child = document.getElementById(childrenIds[v]);
      if (child) breakConnection(child, elmt);
    }
  }
}

function breakConnection(boxA, boxB) {
  //if a is switch or par
  if (boxA.className.includes("switch")) {
    removeLineFromAttribute(boxA, "switchChildren", boxB.id);
    var lineId = boxA.id + "line" + boxB.id;
  } else if (boxA.className.includes("par")) {
    removeLineFromAttribute(boxA, "parChildren", boxB.id);
    var lineId = boxA.id + "line" + boxB.id;
  } else {
    boxA.removeAttribute("nextid");
    var lineId = boxA.id + "line";
  }

  //boxB
  if (boxB.className.includes("end")) {
    removeLineFromAttribute(boxB, "endChildren", boxA.id);
  }
  boxB.removeAttribute("lastid");

  removeLine(lineId);
}

function removeLineFromAttribute(elmt, attribute, oldId) {
  var string = elmt.getAttribute(attribute);
  var array = arrayFromString(string);
  var index = array.indexOf(parseInt(oldId));
  if (index != null && index != undefined) array.splice(index, 1);

  if (array.length != 0) elmt.setAttribute(attribute, array.toString());
  else elmt.removeAttribute(attribute);
}

function generalMouseUp() {
  drawingLine = false;
  console.log("up drawing line = " + drawingLine);
  conA = null;
}

export function movingBox(elmt) {
  if (elmt.className.includes("switch")) {
    switchMovingBox(elmt, true, "switch");
  }
  if (elmt.className.includes("par")) switchMovingBox(elmt, true, "par");

  var nextId = elmt.getAttribute("nextid");
  var lastId = elmt.getAttribute("lastid");
  if (lastId) var lastElmt = document.getElementById(lastId);

  //console.log(elmt);
 // console.log("next id = "+nextId);
  
  // if(elmt.className.includes
  if (nextId != undefined && nextId != null) {
    linkBoxes(elmt, document.getElementById(nextId));
  }
  if (lastElmt) {
    if (lastElmt.className.includes("switch")) {
      switchMovingBox(elmt, false, "switch");
    } else if (lastElmt.className.includes("par")) {
      switchMovingBox(elmt, false, "par");
    } else {
      if (lastId != undefined && lastId != null) {
        //  console.log("lastId "+lastId);
        linkBoxes(document.getElementById(lastId), elmt);
      }
    }
  }
  if (elmt.className.includes("end")) {
    //get all children
    //for each child
    //link

    var endChildren = elmt.getAttribute("endChildren");
    if (endChildren) {
      var childrenIds = arrayFromString(endChildren);

      for (var v = 0; v < childrenIds.length; v++) {
        var child = document.getElementById(childrenIds[v]);
        linkBoxes(child, elmt);
      }
    }

    //      if (childrenArray.indexOf(parseInt(boxB.id))==-1) //only add id to array if it's not there already
    //        boxB.setAttribute("endChildren",endChildren+","+boxA.id);
  }
}

function switchMovingBox(elmt, isSwitch, type) {
  if (isSwitch) {
    var childrenString = elmt.getAttribute(type + "Children");
    var childrenArray = arrayFromString(childrenString);
    for (var j = 0; j < childrenArray.length; j++) {
      var child = document.getElementById(childrenArray[j]); //get child
      if (child) linkBoxes(elmt, child);
    }
  } else {
    // if not switch box
    var parentId = elmt.getAttribute("lastid");
    var parent = document.getElementById(parentId);
    if (parent) linkBoxes(parent, elmt);
  }
}

function blockDoubleClick(elmt) {
  // window.scrollTo(500,500);
  // var infoBox=document.getElementById("infoContent");
  // infoBox.scrollTo(500,500);
  //  console.log("Double clicked "+elmt.className);
  console.log(
    "id = " +
      elmt.id +
      " last = " +
      elmt.getAttribute("lastid") +
      " next = " +
      elmt.getAttribute("nextid") +
      " switchChildren = " +
      elmt.getAttribute("switchChildren") +
      " parChildren = " +
      elmt.getAttribute("parChildren") +
      " endChildren = " +
      elmt.getAttribute("endChildren")
  );
}

function nextElement(current) {
  var nextId = current.getAttribute("nextid");
  var next = document.getElementById(nextId);
  return next;
}

function lastElement(current) {
  var lastId = current.getAttribute("lastid");
  var last = document.getElementById(lastId);
  return last;
}

function arrayFromString(string) {
  var array = JSON.parse("[" + string + "]");
  return array;
}
