import { getOrderedSwitchChildren } from "../preview/orderSwitchPaths.js";

import { getPhoneSensorValue } from "../preview/fakePhoneValues.js";

export function getCorrectSwitchPath(switchElmt) {
  var orderedChildren = getOrderedSwitchChildren(switchElmt);

  var startingChild = getStartingChild(orderedChildren, switchElmt);
  if (startingChild == null) return null;
  var pathArray = getPathElementsArray(
    startingChild,
    switchElmt.getAttribute("endSwitch")
  );

  return pathArray;
}

//Decides which path the switch should take and returns the first child of that path
function getStartingChild(children, switchElmt) {
  for (var i = 0; i < children.length; i++) {
    var testName = getTestNameFromLine(switchElmt.id, children[i].id);

    var testInfoObj = getTestInfo(testName);

    var phoneSensorValue = getPhoneSensorValue(
      testInfoObj.merchant,
      testInfoObj.action
    );
    if (testInfoObj.acceptedValues.includes(phoneSensorValue.toString())) {
      return children[i];
    }
  }
  return null;
}

//returns the test name from the dropdown of the switch path
export function getTestNameFromLine(switchId, childId) {
  var select = document.getElementById(switchId + "line" + childId + "select");
  return select.value;
}

//returns info about a test from its name
export function getTestInfo(testName) {
  var testsContent = document.getElementById("testsContent");
  var tests = testsContent.querySelectorAll(".if:not(.hidden)");

  for (var i = 0; i < tests.length; i++) {
    var inputs = tests[i].getElementsByTagName("input");
    var thisTestName = inputs[0].value;
    if (thisTestName === testName) {
      var selects = tests[i].getElementsByTagName("select");

      return {
        testName: testName,
        merchant: selects[0].value,
        action: selects[1].value,
        acceptedValues: getValuesArray(inputs[1].value),
      };
    }
  }

  if (testName === "Default") {
    return {
      testName: testName,
      merchant: "ELSE",
      action: null,
      acceptedValues: ["else"],
    };
  }

  return null;
}

function getValuesArray(valuesString) {
  var array = valuesString.split("/");
  return array;
}
//for a switch/par child, get all the next elements until the switch end/par end
export function getPathElementsArray(startingElmt, endId) {
  var elements = [];

  var current = startingElmt;
  var finished = false;

  while (!finished) {
    elements.push(current);
    var nextId = current.getAttribute("nextid");
    var endSwitch = current.getAttribute("endSwitch");
    var endPar = current.getAttribute("endPar");

    if (nextId != null && nextId != undefined) {
      var next = document.getElementById(nextId);

      if (next.id === endId) {
        finished = true;
      } else {
        current = next;
      }
    } else if (endPar) {
      current = document.getElementById(endPar);
    } else if (endSwitch) {
      current = document.getElementById(endSwitch);
    } else {
      finished = true;
    }
  }
  return elements;
}
