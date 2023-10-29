//create a manifest.json file to be downloaded in a zip file
var manifest = {};

export function makeManifest() {
  var title = document.getElementById("podcastTitleInput").value;

  manifest = {};
  manifest.title = title;

  var imageInput = document.getElementById("imageInput");
  if (imageInput.files.length > 0) {
    manifest.imageryURIs = [
      { large: "IMAGES/large.jpg" },
      { medium: "IMAGES/medium.jpg" },
      { small: "IMAGES/small.jpg" },
    ];
  }

  var credits = getCredits();
  if (getNumberOfCreditPeople() != 0)
    manifest.creditGroups = [{ name: "Credits", credits: credits }];

  if (imageInput.files[0]) {
    var origImageName = imageInput.files[0].name;
    manifest.imageName = origImageName;
  }

  var manifestString = JSON.stringify(manifest, null, 4);
  return manifestString;
}

function getCredits() {
  var credits = [];

  var creditPeople = document.querySelectorAll(".creditsPerson:not(.hidden)");

  for (var i = 0; i < creditPeople.length; i++) {
    credits.push(getPersonObj(creditPeople[i]));
  }

  return credits;
}

function getPersonObj(elmt) {
  var inputs = elmt.querySelectorAll("input");

  var role = inputs[0].value;
  var name = inputs[1].value;

  var credit = role + " - " + name;

  return { name: credit };
}

function getNumberOfCreditPeople() {
  var num = 0;

  var creditPeople = document.querySelectorAll(".creditsPerson:not(.hidden)");

  for (var i = 0; i < creditPeople.length; i++) {
    var inputs = creditPeople[i].querySelectorAll("input");
    if (inputs[0].value != "" && inputs[0].value != "") num++;
  }

  return num;
}
