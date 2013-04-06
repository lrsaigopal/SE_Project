function positionLoginComponent(event)
{
    var loginComponent = document.getElementById("loginComponent");
    if (loginComponent) {
        var totalWidth = window.innerWidth;
        var loginComponentWidth = parseInt(loginComponent.offsetWidth);
        if (loginComponentWidth) {
            loginComponent.style.left = (totalWidth-loginComponentWidth)/2 +"px";
        }
        loginComponent.style.top = (window.innerHeight-loginComponent.offsetHeight )/2 -15 +"px";
    }
    copyright = document.getElementById("copyright")
    copyright.style.left= (window.innerWidth - copyright.offsetWidth)/2 +"px";
}

