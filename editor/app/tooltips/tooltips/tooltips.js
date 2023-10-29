import { refreshBlockTooltips } from "../tooltips/blockTooltips.js";

var buttonTipWidth = 200;
var buttonTipPlacement = "right";
var buttonTipTheme = "centered";

export function initTooltips() {
  refreshBlockTooltips();
  refreshTestTips();
  initButtonTooltips();
  initOtherTooltips();
}

function initButtonTooltips() {
  tippy(".previewButtonTip", {
    content: "Previews your podcast using the values in the 'Player' tab",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".createFileButtonTip", {
    content: "Creates and downloads a zip file of your adaptive podcast",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".audioButtonTip", {
    content: "Audio elements play audio files",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".ttsButtonTip", {
    content: "Text to Speech elements read text aloud",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".dynqueryButtonTip", {
    content:
      "Queries the listener's phone data and reads out value - while previewing uses values in Previewer Values tab",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".switchButtonTip", {
    content: "Switch elements choose between different options based on tests",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".testButtonTip", {
    content:
      "Tests are used by Switch elements to decide which path to take. Tests are created in the Tests tab",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".parButtonTip", {
    content:
      "Elements inbetween Parallels are played at the same time. Connect the Parallel element to multiple elements",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".pauseButtonTip", {
    content: "Pauses for specified time",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });
  tippy(".vibrateButtonTip", {
    content: "Vibrates phone for specified time",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });
  
    tippy(".skipButtonTip", {
    content: "Skips element when previewing",
    placement: buttonTipPlacement,
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });
}

function initOtherTooltips() {
  tippy("#addSpace", {
    content: "Add more space",
    placement: "left",
    maxWidth: buttonTipWidth,
    theme: buttonTipTheme,
  });

  tippy(".infoBoxTip", {
    content: "These tabs let you add tests, change previewer values, edit your podcast's manfest and upload existing zip files",
  });
  
  
}

export function refreshTestTips(){
   tippy(".testNameTip", {
    content: "Make up a name for this test",
  });
   tippy(".selectTip", {
    content: "Use the dropdowns to choose which sensor the test should use",
  });
   tippy(".acceptedValuesTip", {
    content: "Enter the accepted value. Add multiple accepted values using /, eg 'afternoon/night'",
  });
}