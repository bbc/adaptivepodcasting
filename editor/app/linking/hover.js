import { activeDotInt } from "../linking/linkingElmts.js";
import { activeBlock } from "../linking/linkingElmts.js";
import { onBlockMove } from "../linking/linkingElmts.js";


//add a listener to all elements for when the user's mouse moves over them
export function assignBlockHoverListeners() {
  var blocks = document.querySelectorAll(".block:not(.hidden):not(.info)");

  for (var i = 0; i < blocks.length; i++) {
    if (blocks[i] !== activeBlock) {
      blocks[i].addEventListener("mouseenter", onBlockHover);
      blocks[i].addEventListener("mouseleave", endBlockHover);
    }
  }
}

//remove listeners after user has finished dragging
export function removeBlockHoverListeners() {
  var blocks = document.querySelectorAll(".block:not(.hidden):not(.info)");

  for (var i = 0; i < blocks.length; i++) {
    if (blocks[i] !== activeBlock) {
      blocks[i].removeEventListener("mouseenter", onBlockHover);
      blocks[i].removeEventListener("mouseleave", endBlockHover);
    }
  }
}

// when element is hovered over, change the colour of the correct dot
function onBlockHover(event) {
  var targetBlock = event.target;

  if (targetBlock == activeBlock) return;
  var targetBlockDots = targetBlock.getElementsByClassName("lineDot");

  if (activeDotInt == 0)
    targetBlockDots[1].className = "lineDot connectingLineDot";
  else if (activeDotInt == 1)
    targetBlockDots[0].className = "lineDot connectingLineDot";
}

function endBlockHover(event) {
  var targetBlock = event.target;
  var targetBlockDots = targetBlock.getElementsByClassName("lineDot");
  targetBlockDots[0].className = "lineDot";
  targetBlockDots[1].className = "lineDot";

  //reset block to colour before hover
  onBlockMove(targetBlock);
}
