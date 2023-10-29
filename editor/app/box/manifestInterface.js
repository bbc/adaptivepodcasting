var exampleCreditsPerson = document.getElementById("exampleCreditsPerson");
var people = document.getElementById("people");

//adds a new line to the credits space and assigns js listeners
export function addCreditsPerson() {
  var newPerson = exampleCreditsPerson.cloneNode(true);
  newPerson.className = newPerson.className.replace("hidden ", "");
  newPerson.removeAttribute("id");

  var deletePersonButton =
    newPerson.getElementsByClassName("deletePersonButton")[0];
  deletePersonButton.addEventListener("click", function () {
    deleteNameRow(deletePersonButton);
  });

  people.appendChild(newPerson);
  return newPerson;
}

// deletes a line in credits space
function deleteNameRow(deleteButton) {
  var nameRow = deleteButton.parentNode;
  people.removeChild(nameRow);
}
