function validate()
{
    username = document.getElementById("username");
    password = document.getElementById("password");
    var re = /[\&\*\s]/
    if (username && password) {
        if (username.value == "" || re.test(username.value)) {
            usernameError = document.getElementById("usernameError");
            if (usernameError) {
                alert("Please Enter  a valid username ");
                username.value ="";
            }
        }else if (password.value =="" || re.test(password.value)) {
            passwordError = document.getElementById("passwordError");
            if (passwordError) {
                alert("Please Enter  a valid password ");
                password.value ="";
            }
        }
        else document.getElementById("myForm").submit();
    }
    
}