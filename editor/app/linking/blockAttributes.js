//updates element attributes when they are linked/unlinked

import { breakLinkingLine } from "../linking/drawingLines.js";

import { blockIsMulti } from "../linking/linkingElmts.js";
import { blockIsEndMulti } from "../linking/linkingElmts.js";

import { getChildrenArray } from "../linking/linkingElmts.js";


export function linkBlockAttributes(first, second) {
  //if first element is not a two part element
  if (!blockIsMulti(first)) {
    breakBlockLinkAttributes(first, getNextBlock(first));
    first.setAttribute("nextid", second.id);
  } else {
    //if first element is two part element
    first = addToChildren(first, second.id, false);
  }

  if (!blockIsEndMulti(second)) {
    //if second element is not a two part element
    breakBlockLinkAttributes(getPrevBlock(second), second);
    second.setAttribute("previd", first.id);
  } else {
    //if second element is a two part element
    second = addToChildren(second, first.id, true);
  }
}

//break attribute link between two elements
export function breakBlockLinkAttributes(first, second) {
  if (first) {
    if (blockIsMulti(first)) {
      if (second) first = removeFromChildren(first, second.id,false);
    } else {
      first.removeAttribute("nextId");
    }
  }

  if (second) {
    if (blockIsEndMulti(second)) {
      if (first) second = removeFromChildren(second, first.id,true);
    } else {
      second.removeAttribute("previd");
    }
  }
}

//get element from an elements 'next id'
export function getNextBlock(block) {
  if (!block) return null;
  var nextId = block.getAttribute("nextid");
  var nextBlock = document.getElementById(nextId);
  return nextBlock;
}

//get element from an elements 'prev id'
export function getPrevBlock(block) {
  if (!block) return null;
  var prevId = block.getAttribute("previd");
  var prevBlock = document.getElementById(prevId);
  return prevBlock;
}

//add element to the children of a two part element
function addToChildren(block, id, end) {
  var children = getChildrenArray(block, end);
  if (!children.includes(id)) children.push(id);
  if (end) block.setAttribute("endchildren", children.toString());
  else block.setAttribute("children", children.toString());
  return block;
}
//remove element id from children of a two part element
function removeFromChildren(block, id, end) {
  var children = getChildrenArray(block, end);

  id = parseInt(id);

  if (children.includes(id)) {
    var index = children.indexOf(id);
    children.splice(index, 1);
  }

  if (end) block.setAttribute("endchildren", children.toString());
  else block.setAttribute("children", children.toString());
  return block;
}

//remove one attribute from an element
export function clearOneBlockAttribute(block, dotInt) {
  if (dotInt == 0) breakBlockLinkAttributes(getPrevBlock(block), block);
  else if (dotInt == 1) breakBlockLinkAttributes(block, getNextBlock(block));
}

export function removeAllBlockAttributes(block) {
  var next = getNextBlock(block);
  var prev = getPrevBlock(block);

  if (next) breakBlockLinkAttributes(block, next);
  if (prev) breakBlockLinkAttributes(prev, block);

  if (blockIsMulti(block)) {
    var childrenIds = getChildrenArray(block);
    for (var i = 0; i < childrenIds.length; i++) {
      var child = document.getElementById(childrenIds[i]);
      breakBlockLinkAttributes(block, child);
    }
  } else if (blockIsEndMulti(block)) {
    var childrenIds = getChildrenArray(block);
    for (var i = 0; i < childrenIds.length; i++) {
      var child = document.getElementById(childrenIds[i]);
      breakBlockLinkAttributes(child, block);
    }
  }
}
