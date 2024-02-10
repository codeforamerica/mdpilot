document.addEventListener("DOMContentLoaded", setUpPage);
window.addEventListener("pageshow", setUpPage);

function setUpPage() {
  var sameAsAddressBox = document.getElementById("sameAsHomeAddress-true-label");
  var noPermanentAddress = document.getElementById("noHomeAddress-true-label");
  var mailFields = document.getElementById("hidden-mailing-fields");
  var homeFields = document.getElementById("hidden-home-address-fields");

  // Function to toggle mail fields display
  function toggleMailFields() {
    var selectedClass = document.getElementsByClassName('is-selected');
    if (mailFields) {
      mailFields.style.display = selectedClass.length > 0 ? "none" : "block";
    }
  }

  // Function to toggle home fields display
  function toggleHomeFields() {
    var selectedClass = document.getElementsByClassName('is-selected');
    if (homeFields) {
      homeFields.style.display = selectedClass.length > 0 ? "none" : "block";
    }
  }

  if (sameAsAddressBox) {
    sameAsAddressBox.addEventListener("click", toggleMailFields);
  }

  if (noPermanentAddress) {
    noPermanentAddress.addEventListener("click", toggleHomeFields);
  }

  // Check and apply on page load and on page show
  toggleMailFields();
  toggleHomeFields();
}
