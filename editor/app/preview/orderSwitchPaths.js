//sorts the switch children based on their position on the canvas, highest first

import {getTestNameFromLine} from "../preview/decideSwitchPath.js";

export function getOrderedSwitchChildren(switchElmt) {
  var children = getSwitchChildElmts(switchElmt);
  var defaults = [];
  var nonDefaults = [];

  if(children===null) return null;
  
  for (var i = 0; i < children.length; i++) {
    var testName = getTestNameFromLine(switchElmt.id, children[i].id);

    if (testName === "Default") {
      defaults.push(children[i]);
    } else {
      nonDefaults.push(children[i]);
    }
  }

  nonDefaults = orderChildrenByHeight(nonDefaults);
  defaults = orderChildrenByHeight(defaults);

  var orderedChildren = nonDefaults.concat(defaults);
  return orderedChildren;
}

function getSwitchChildElmts(switchElmt) {
  var childElmts = [];
  var childrenString = switchElmt.getAttribute("children");
  if(childrenString===null) return null;
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

function orderChildrenByHeight(children) {
  children.sort(function(a, b) {
    return getElmtHeight(a) - getElmtHeight(b);
  });
  return children;
}

function getElmtHeight(elmt) {
  var height;
  var style = elmt.getAttribute("style");
  var styleAttributes = style.split(";");
  for (var i = 0; i < styleAttributes.length; i++) {
    var attribute = styleAttributes[i].split(":");
    if (attribute[0] === "top") height = attribute[1].replace("px", "");
  }
  return parseInt(height);
}
