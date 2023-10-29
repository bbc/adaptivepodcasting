//gets all the paths(arrays of sequential elements) from all the children elements inside a parallel element

import { getPathElementsArray } from "../preview/decideSwitchPath.js";

//returns an array of paths inside a parallel element, each path an array of elements
export function getParElmtPaths(parElmt) {
  var children = getParChildElmts(parElmt);

  var paths2dArray = [];

  for (var i = 0; i < children.length; i++) {
    paths2dArray.push(getPathElementsArray(children[i], parElmt.getAttribute("endPar")));
  }
  return paths2dArray;
}
//gets all child elements inside a parallel element
function getParChildElmts(parElmt) {
  var childElmts = [];
  var childrenString = parElmt.getAttribute("children");
  var childrenIds = arrayFromString(childrenString);
  for (var i = 0; i < childrenIds.length; i++) {
    var child = document.getElementById(childrenIds[i]);
    childElmts.push(child);
  }
  return childElmts;
}

function arrayFromString(string) {
  var array = JSON.parse("[" + string + "]");
  return array;
}
