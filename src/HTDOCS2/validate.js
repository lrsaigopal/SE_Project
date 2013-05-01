
// validates and displays appropriate errors
function validate(event) {
    event.preventDefault();
    var login = document.getElementById("login-name");
    var pwd = document.getElementById("login-pass");
    var error = document.getElementById("error");
    var errorMessage = document.getElementById("error-message")
    var validatePattern =/[\s\t\r\f\n\v]/;
    if (login.value == "" && pwd.value == ""){
        errorMessage.innerHTML = "Please Enter a valid Username and Password"
        error.style.display ="block";
    }
    else if (login.value == "" || login.value.match(validatePattern) != null) {
        errorMessage.innerHTML = "Please Enter a valid Username"
        error.style.display ="block";
    }
    else if (pwd.value == "" || pwd.value.match(validatePattern) != null) {
        errorMessage.innerHTML = "Please Enter a valid Password"
        error.style.display ="block";
    }
    else {
        var form = document.getElementById("form");
        form.submit();
    }
    