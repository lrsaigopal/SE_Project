function init() {
    setTimeout(displayButtons,1050);
}

function displayButtons(){   
    buttons = document.getElementsByName("components")
    off = document.getElementById("dashboard");
    student = document.getElementById("student");
    hi = document.getElementById("hi")
    logout = document.getElementById("logout")
    body = document.getElementsByTagName("body")[0]
    startTop = body.offsetHeight/20;
    //startLeft = 32;
    for (i=0;i<buttons.length;++i) {
        //buttons[i].style.left = startLeft +"%"
        if (!(i%2)) {
            startTop += body.offsetHeight/29;
        }
        buttons[i].style.top = startTop +"%"
        buttons[i].style.width = body.offsetHeight *(160)/588 +"px";
        buttons[i].style.display ="inline";
    }
    buttons[0].style.left = ((body.offsetWidth-off.offsetWidth)/2) +(off.offsetWidth/2 -buttons[0].offsetWidth)/2 +"px";
    buttons[1].style.right = (body.offsetWidth-off.offsetWidth)/2 +(off.offsetWidth/2 -buttons[0].offsetWidth)/2  +"px";
    buttons[2].style.left = ((body.offsetWidth-off.offsetWidth)/2) +(off.offsetWidth/2 -buttons[0].offsetWidth)/2 +"px";
    student.style.left = (body.offsetWidth-off.offsetWidth)/2 + (off.offsetWidth/2) -50 +"px"
    student.style.top = "100px";
    student.style.display ="inline";
    hi.style.top = (off.offsetHeight)/16 + "px"
    hi.style.left = (body.offsetWidth-off.offsetWidth)/2 +(off.offsetWidth - hi.offsetWidth) + "px"
    hi.style.display ="inline";
    logout.style.top = (off.offsetHeight)/10 + "px"
    logout.style.left = (body.offsetWidth-off.offsetWidth)/2 +(off.offsetWidth - logout.offsetWidth) + "px"
    logout.style.display ="inline";
    copyright = document.getElementById("copyright")
    copyright.style.display ="block";
    
}