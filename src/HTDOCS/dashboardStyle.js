function init() {
    setTimeout(displayButtons,2000);
}

function displayButtons(){   
    buttons = document.getElementsByName("components")
    dashboard = document.getElementById("dashboard");
    student = document.getElementById("student");
    hi = document.getElementById("hi")
    logout = document.getElementById("logout")
    page = document.getElementById("page")
    body = document.getElementsByTagName("body")[0]
    logoutComponent = document.getElementById("logoutComponent")
    startTop = dashboard.offsetTop + (0.4 * dashboard.offsetWidth);
    for (i=0;i<buttons.length;++i) {
        if (!(i%2) && i!=0) {
            startTop += (dashboard.offsetWidth*.15)
        }
        buttons[i].style.top = startTop +"px"
        buttons[i].style.width = "11%"; //automatically fixes the height too, and 11 percent of the body is a safe bet.
        i%2?buttons[i].style.right="31%":buttons[i].style.left = "31%";
        buttons[i].style.display ="inline";
    }
    student.style.top = dashboard.offsetTop + (0.1 * dashboard.offsetWidth)+ "px";
    student.style.width = "10.5%"
    student.style.left = (body.offsetWidth/2) - dashboard.offsetWidth *.1 +"px"; // .1 because width of student logo is 10%
    student.style.display ="inline";
    body.offsetWidth>1024?logout.style.fontSize ="2.5ex":logout.style.fontSize ="1.5ex"
    body.offsetWidth>1024?hi.style.fontSize ="2.5ex":hi.style.fontSize ="1.5ex"
    logoutComponent.style.right = (body.offsetWidth - dashboard.offsetWidth)/2 +"px";
    logoutComponent.style.top = (dashboard.offsetTop - dashboard.offsetTop* 0.5)+"px";
    hi.style.display ="inline";
    logout.style.display ="inline"
    copyright = document.getElementById("copyright")
    copyright.style.display ="block";
}
//To tweak things up

function resize() {
    startTop = dashboard.offsetTop + (0.4 * dashboard.offsetWidth);
    for (i=0;i<buttons.length;++i) {
        if (!(i%2) && i!=0) {
            startTop += (dashboard.offsetWidth*.15)
        }
        buttons[i].style.top = startTop +"px"
        buttons[i].style.width = "11%"; //automatically fixes the height too, and 11 percent of the body is a safe bet.
        i%2?buttons[i].style.right="31%":buttons[i].style.left = "31%";
        buttons[i].style.display ="inline";
    }
    student.style.top = dashboard.offsetTop + (0.1 * dashboard.offsetWidth)+ "px";
    student.style.width = "10.5%"
    student.style.left = (body.offsetWidth/2) - dashboard.offsetWidth *.1 +"px"; // .1 because width of student logo is 10%
    student.style.display ="inline";
    body.offsetWidth>1024?logout.style.fontSize ="2.5ex":logout.style.fontSize ="1.5ex"
    body.offsetWidth>1024?hi.style.fontSize ="2.5ex":hi.style.fontSize ="1.5ex"
    logoutComponent.style.right = (body.offsetWidth - dashboard.offsetWidth)/2 +"px";
    logoutComponent.style.top = (dashboard.offsetTop - dashboard.offsetTop* 0.5)+"px";
    hi.style.display ="inline";
    logout.style.display ="inline"
    copyright = document.getElementById("copyright")
    copyright.style.display ="block";
    page.style.top = (body.offsetWidth/2)+"px";
}