//loads data from manifest.json in an uploaded zip file into the editor
import { addCreditsPerson } from "../box/manifestInterface.js";

export function loadManifestFile(obj) {
  var title = document.getElementById("podcastTitleInput");

  title.setAttribute("value", obj["title"]);

  var creditGroups = obj["creditGroups"];

  var creditsExample =  document.querySelectorAll(".creditsPerson:not(.hidden)")[0];
  if(creditsExample) creditsExample.remove();

  if (creditGroups) {
    for (var i = 0; i < creditGroups.length; i++) {
      loadCredits(creditGroups[i]);
    }

    loadImageName(obj.imageName);
  }
}

function loadCredits(creditGroup) {
  var names = creditGroup.credits;

  for (var j = 0; j < names.length; j++) {
    var text = names[j].name;

    var parts = text.split(" - ");

    var person = addCreditsPerson();

    var inputs = person.querySelectorAll("input");

    inputs[0].value = parts[0];
    inputs[1].value = parts[1];
  }
}

function loadImageName(name) {
  if (name !== null && name !== undefined) {
    var imageInput = document.getElementById("imageInput");
    imageInput.className = "hidden";

    var uploadedImage = document.getElementById("uploadedImage");
    uploadedImage.className = "";

    var fileNameElmt = document.getElementById("fileName");
    fileNameElmt.innerText = name;
  }
}
