import { drawMouseLine } from "../linking/drawingLines.js";
import { linkFailed } from "../linking/drawingLines.js";
import { drawLinkingLine } from "../linking/drawingLines.js";
import { breakLinkingLine } from "../linking/drawingLines.js";

import { assignBlockHoverListeners } from "../linking/hover.js";
import { removeBlockHoverListeners } from "../linking/hover.js";

import { linkBlockAttributes } from "../linking/blockAttributes.js";
import { clearOneBlockAttribute } from "../linking/blockAttributes.js";
import { getNextBlock } from "../linking/blockAttributes.js";
import { getPrevBlock } from "../linking/blockAttributes.js";

import {
  removeAllBlockAttributes,
  breakBlockLinkAttributes,
} from "../linking/blockAttributes.js";

var mouseDrawingLine = false;
export var activeDot = null;
export var activeBlock = null;
export var activeDotInt = null;

document.addEventListener("mousemove", function (event) {
  if (mouseDrawingLine) {
    drawMouseLine(event, activeDot, activeBlock);
  }
});

document.addEventListener("mouseup", function (event) {
  if (mouseDrawingLine) {
    mouseLineDragFinished(event);
    mouseDrawingLine = false;
    activeDot = null;
    activeBlock = null;
    activeDotInt = null;
    removeBlockHoverListeners();
  }
});



//Called on click and drag on line dot
export function onLineDotMouseDown(block, dotInt) {
  var dot = block.getElementsByClassName("lineDot")[dotInt];
  activeDot = dot;
  mouseDrawingLine = true;
  activeBlock = block;
  activeDotInt = dotInt;

  assignBlockHoverListeners();

  if (blockIsMulti(block)) return;

  if (dotInt === 1) {
    breakLinkingLine(block, getNextBlock(block));
    clearOneBlockAttribute(block, 1);
    clearOneBlockAttribute(getNextBlock(block), 0);
  } else if (dotInt === 0) {
    breakLinkingLine(getPrevBlock(block), block);
    clearOneBlockAttribute(block, 0);
    clearOneBlockAttribute(getPrevBlock(block), 1);
  }
}


function mouseLineDragFinished(event) {
  var targetBlock = getParentBlock(event.target);

  if (
    targetBlock &&
    targetBlock !== activeBlock &&
    targetBlock.id !== "infoBox"
  ) {
    if (activeDotInt === 0) linkTwoBlocks(targetBlock, activeBlock); 
    else if (activeDotInt === 1) linkTwoBlocks(activeBlock, targetBlock);
  } else {
    linkFailed(activeDot);
    clearOneBlockAttribute(activeBlock, activeDotInt);
    onBlockMove(activeBlock);
  }

}

//find the element that the HTML element is a child of
function getParentBlock(innerElmt) {
  var parent = innerElmt;

  while (parent != null) {
    if (parent.className) {
      if (parent.className.includes("block ")) {
        return parent;
        break;
      } else {
        parent = parent.parentNode;
      }
    } else {
      parent = parent.parentNode;
    }
  }

  return null;
}

//draw lines between two elements
function linkTwoBlocks(first, second) {
  if (!blockIsMulti(first)) breakLinkingLine(first, getNextBlock(first));

  if (!blockIsEndMulti(second)) breakLinkingLine(getPrevBlock(second), second);

  linkBlockAttributes(first, second);
  drawLinkingLine(first, second);
}

//when element is dragged, uplate its lines
export function onBlockMove(block) {
  if (blockIsMulti(block)) onMultiBlockMove(block, false);
  else if (blockIsEndMulti(block)) onMultiBlockMove(block, true);

  var next = getNextBlock(block);
  var prev = getPrevBlock(block);

  if (next) drawLinkingLine(block, next);
  if (prev) drawLinkingLine(prev, block);
}

//on a two part element dragged, update all of its lines
function onMultiBlockMove(block, end) {
  var children = getChildrenArray(block, end);
  for (var i = 0; i < children.length; i++) {
    var childBlock = document.getElementById(children[i]);
    if (!end) drawLinkingLine(block, childBlock);
    else drawLinkingLine(childBlock, block);
  }
}

export function movingBox(elmt) {
  onBlockMove(elmt);
}
export function removeBlockConnections(block) {
  if (!blockIsMulti(block)) breakLinkingLine(block, getNextBlock(block));

  if (!blockIsEndMulti(block)) {
    breakLinkingLine(getPrevBlock(block), block);
  }
  if (blockIsMulti(block)) {
    var childrenIds = getChildrenArray(block);
    for (var i = 0; i < childrenIds.length; i++) {
      var child = document.getElementById(childrenIds[i]);
      breakLinkingLine(block, child);
    }
  } else if (blockIsEndMulti(block)) {
    var childrenIds = getChildrenArray(block,true);
    for (var i = 0; i < childrenIds.length; i++) {
      var child = document.getElementById(childrenIds[i]);
      breakLinkingLine(child, block);
      breakBlockLinkAttributes(child, block);
    }
  }

  removeAllBlockAttributes(block);
}

export function blockIsMulti(block) {
  if (block.className.includes("switch") || block.className.includes("par"))
    return true;
  else return false;
}

export function blockIsEndMulti(block) {
  if (block.className.includes("endS") || block.className.includes("endP"))
    return true;
  else return false;
}

export function getChildrenArray(block, end) {
  if (!block) return null;
  var string;
  if (end) var string = block.getAttribute("endchildren");
  else var string = block.getAttribute("children");
  var array = JSON.parse("[" + string + "]");
  if (string == null) array = [];
  return array;
}

