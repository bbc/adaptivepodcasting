//when you click 'Test Switch' the editor shows you which path the Switch Element will choose

import { getOrderedSwitchChildren } from "../preview/orderSwitchPaths.js";
import { getTestNameFromLine } from "../preview/decideSwitchPath.js";
import { getTestInfo } from "../preview/decideSwitchPath.js";
import { getPhoneSensorValue } from "../preview/fakePhoneValues.js";

//on Test Switch button clicked
export function testSwitch(elmt) {
  var children = getOrderedSwitchChildren(elmt);

  if (children === null) {
    alert("This Switch is not connected to any other elements.")
    return;
  }

  //the test tthat passes first is the one the Switch element will choose
  var firstPassedTest = null;

  var string="";
  //for each child of the Switch element
  for (var i = 0; i < children.length; i++) {
    var testName = getTestNameFromLine(elmt.id, children[i].id);
    var testInfo = getTestInfo(testName);

    if(testInfo.merchant===null){
      alert("Some tests on this Switch element are blank");
      return;
    }
    //get the actual value for this test from the Tests tab in info box
    var actualValue = getPhoneSensorValue(testInfo.merchant, testInfo.action);
    var passes = testPasses(testInfo.acceptedValues, actualValue);

    if (firstPassedTest === null && passes) firstPassedTest = testName;

    passes=passes.toString().toUpperCase();
    
    //display results in an alert
    if(testName==="Default") string = string+ "Test name: " +
      testName +
      "\nTest passes: " +
      passes+"\n";
    
    else
    string=string+
      "Test name: " +
      testName +
      " \n Accepted Values: " +
      testInfo.acceptedValues.toString() +
      "\n Actual value: " +
      actualValue +
      "\n Test passes: " +
      passes+"\n\n";
    
    
  }

  
  
  if (firstPassedTest === null)
   string = string+"No tests passed, this Switch element won't play any paths";
  
  
  else string = string+ "\nThe Switch will choose the " + firstPassedTest + " path.";
  alert(string);
}
//check if actual value matches an accepted value for this test
function testPasses(acceptedValues, actualValue) {
  var passes = false;
  for (var i = 0; i < acceptedValues.length; i++) {
    if (acceptedValues[i] == actualValue) passes = true;
  }
  return passes;
}
