"use strict";

/**
 * Initializes dom objects
 */
function setupInitialization()
{
    setAlertStyle("light");

    let lightTheme = document.getElementById("light-theme");
    let darkTheme = document.getElementById("dark-theme");
    let submit = document.getElementById("submit");

    lightThemeSquare = document.getElementById("light-theme-square");
    darkThemeSquare = document.getElementById("dark-theme-square");

    serverName = document.getElementById("server-name");
    port = document.getElementById("port");
    enableFog = document.getElementById("fog-toggle");
    backgroundColor = document.getElementById("color-selector");

    setupXHR = new XMLHttpRequest();

    lightTheme.addEventListener("click", function(event) {changeTheme(event.target || event.srcElement)});
    darkTheme.addEventListener("click", function(event) {changeTheme(event.target || event.srcElement)});
    submit.addEventListener("click", function(event) {sendSetupRequest(event.target || event.srcElement)});
    enableFog.addEventListener("change", function(event) {toggleFog()});
    backgroundColor.addEventListener("input", function(event) {changeBackgroundColor()});
}

/**
 * Changes theme
 */
function changeTheme(element)
{
    if (String(element.id) == "light-theme")
    {
        html.setAttribute("theme", "light");
        setAlertStyle("light");

        lightThemeSquare.style.animation = "fade-in-square 0.5s forwards";
        darkThemeSquare.style.animation = "fade-out-square 0.5s forwards";

        background.setOptions
        ({
            highlightColor: 0xCAC7E8,
            midtoneColor: 0xBBB7ED,
            lowlightColor: 0xE4E3EF,
            baseColor: 0xE4E3EF
        });
    }
    else
    {
        html.setAttribute("theme", "dark");
        setAlertStyle("dark");

        darkThemeSquare.style.visibility = "visible";

        lightThemeSquare.style.animation = "fade-out-square 0.5s forwards";
        darkThemeSquare.style.animation = "fade-in-square 0.5s forwards";

        background.setOptions
        ({
            highlightColor: 0x797979,
            midtoneColor: 0xFFFFFF,
            lowlightColor: 0xBCBCBC,
            baseColor: 0xBCBCBC
        });
    }
    let color = html.getAttribute("theme") == "light" ? "#e5e5e5" : "#303030";
    html.setAttribute("backgroundColor", color);
    backgroundColor.value = color;
    document.body.style.backgroundColor = color;
}

/**
 * Changes alert style
 *
 * @param {*} style name
 */
function setAlertStyle(styleName)
{
    let links = document.getElementsByTagName("link");

    for (let i = 0; i < links.length; i++)
    {
        if ((links[i].getAttribute("title") == "light") || (links[i].getAttribute("title") == "dark"))
        {
            links[i].disabled = (links[i].getAttribute("title") != styleName);
        }
    }
}

/**
 * Sends settings request
 */
function sendSetupRequest()
{
    // Disable button with delay
    submit.disabled = true;
    submit.value = "LAUNCHING...";
    submit.style.opacity = "0.7";

    setupXHR.open("POST", "/api/setup");
    setupXHR.setRequestHeader("Content-Type", "application/json");

    setupXHR.onreadystatechange = function()
    {
        if (this.readyState === 4)
        {
            if (this.status === 200)
            {
                setTimeout(function() {
                    window.location = `http://${window.location.hostname}:${port.value}`;
                }, 2500);
            }
            else
            {
                submit.disabled = false;
                submit.value = "LAUNCH";
                submit.style.opacity = "1";
                
                const message = {
                    text: "Fill the form correctly",
                    type: ("")
                }
                dhtmlx.message(message);
            }
        }
    }

    const data = {
        serverName: serverName.value,
        theme: html.getAttribute("theme"),
        port: port.value,
        enableFog: String(enableFog.checked),
        backgroundColor: backgroundColor.value
    }

    setupXHR.send(JSON.stringify(data));
}

/**
 * Toggles fog
 */
function toggleFog()
{
    html.setAttribute("enableFog", String(enableFog.checked));
    backgroundInitialization();
    backgroundColor.disabled = enableFog.checked;
}

/**
 * Change background color
 */
function changeBackgroundColor()
{
    html.setAttribute("backgroundColor", backgroundColor.value);
    document.body.style.backgroundColor = backgroundColor.value;
}