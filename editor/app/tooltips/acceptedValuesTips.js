import { getAvText } from "../tooltips/getAvTipText.js";

//when any test dropdown is edited, show a tooltip with the correct options 
export function onTestDropdownChange(test) {
  var merchant = test.getElementsByTagName("select")[0];
  var action = test.getElementsByTagName("select")[1];
  var avInput = test.getElementsByTagName("input")[1];

  var text = "Options: \n" + getAvText(merchant.value, action.value);

  showTip(avInput, text);
}

function showTip(elmt, text) {
  tippy(elmt, {
    content: text,
    trigger: "manual",
    interactive: true,
    hideOnClick: true,
    theme: "av-tip",
    placement: "bottom",
    arrow: true,
  });

  var instance = elmt._tippy;
  instance.show();
}

export function testCreatedTip() {
  var infoBox = document.getElementById("infoBox");
  var text = "Test created";

  tippy(infoBox, {
    content: text,
    trigger: "manual",
    interactive: true,
    hideOnClick: true,
    theme: "av-tip",
    placement: "left",
    arrow: true,
  });

  var instance = infoBox._tippy;
  instance.show();
}
