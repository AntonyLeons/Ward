"use strict";

/**
 * Initializes uptime, labels and chart values
 */
function indexInitialization()
{
    showCards();

    currentClockSpeed = document.getElementById("currentClockSpeed");
    currentProcCount = document.getElementById("currentProcCount");
    currentTotalStorage = document.getElementById("currentTotalStorage");
    currentDiskCount = document.getElementById("currentDiskCount");

    cloudLeft = document.getElementById("cloud-left");
    cloudRight = document.getElementById("cloud-right");

    days = document.getElementById("uptime-days");
    hours = document.getElementById("uptime-hours");
    minutes = document.getElementById("uptime-minutes");
    seconds = document.getElementById("uptime-seconds");

    usageXHR = new XMLHttpRequest();
    infoXHR = new XMLHttpRequest();
    uptimeXHR = new XMLHttpRequest();

    sendUsageRequest();
}

/**
 * Changes cards opacity with random sequence
 */
function showCards()
{
    let cards = document.getElementsByClassName("card");
    let versionLabel = document.getElementById("project-version");

    let randomSequenceArray = getRandomSequenceArray();

    for (let i = 0; i < cards.length; i++)
    {
        setTimeout(function()
        {
            cards[randomSequenceArray[i]].style.opacity = "1";

            if (randomSequenceArray[i] == 4)
            {
                versionLabel.style.opacity = "1";
            }
        }, 70 * i);
    }
}

/**
 * Generates random sequence
 */
function getRandomSequenceArray()
{
    let buffer = [];

    while (buffer.length < 5)
    {
        let randomNumber = Math.floor(Math.random() * 5);

        if ((buffer.indexOf(randomNumber) === -1))
        {
            buffer.push(randomNumber);
        }
    }

    return buffer;
}

/**
 * Sending ajax request to receive usage info
 */
function sendUsageRequest()
{
    usageXHR.onreadystatechange = function()
    {
        if ((this.readyState == 4) && (this.status == 200))
        {
            let response = JSON.parse(this.response);

            labelsTick(response);
            chartTick(response);

            sendInfoRequest();
        }
    }

    usageXHR.open("GET", "/api/usage");
    usageXHR.send();
}

/**
 * Sending ajax request to receive info about server
 */
function sendInfoRequest()
{
    infoXHR.onreadystatechange = function()
    {
        if ((this.readyState == 4) && (this.status == 200))
        {
            let response = JSON.parse(this.response);

            currentClockSpeed.innerHTML = response.processor.clockSpeed;
            currentProcCount.innerHTML = response.machine.procCount;
            currentTotalStorage.innerHTML = response.storage.total;
            currentDiskCount.innerHTML = response.storage.diskCount;

            sendUptimeRequest();
        }
    }

    infoXHR.open("GET", "/api/info");
    infoXHR.send();
}

/**
 * Sending ajax request to receive server uptime
 */
function sendUptimeRequest()
{
    infoXHR.onreadystatechange = function()
    {
        if ((this.readyState == 4) && (this.status == 200))
        {
            let response = JSON.parse(this.response);

            days.innerHTML = response.days;
            hours.innerHTML = response.hours;
            minutes.innerHTML = response.minutes;
            seconds.innerHTML = response.seconds;

            sendUsageRequest()
        }
    }

    infoXHR.open("GET", "/api/uptime");
    infoXHR.send();
}

/**
 * Animates clouds
 *
 * @param {*} new page
 */
function setCloudAnimation(newSquareScale)
{
    switch (newSquareScale)
    {
        case 1:
        {
            cloudLeft.style.animation = "fade-in-cloud-left 0.3s forwards";
            cloudRight.style.animation = "fade-in-cloud-right 0.3s forwards";
            break;
        }
        case 2:
        {
            cloudLeft.style.animation = "fade-out-cloud-left 0.3s forwards";
            cloudRight.style.animation = "fade-out-cloud-right 0.3s forwards";
            break;
        }
    }
}

/**
 * Changes opacity of control
 *
 * @param {*} new page
 */
function setControlOpacity(newSquareScale)
{
    switch (newSquareScale)
    {
        case 1:
        {
            firstControl.style.opacity = "0.5";
            secondControl.style.opacity = "1";
            break;
        }
        case 2:
        {
            firstControl.style.opacity = "1";
            secondControl.style.opacity = "0.5";
            break;
        }
    }
}