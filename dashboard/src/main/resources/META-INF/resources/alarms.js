
const spanTempWarningCount = document.querySelector("#temperatureWarningCount");
const spanImageWarningCount = document.querySelector("#imageWarningCount");

var rabbitAlarms = new EventSource("/dashboard/rabbit-alarms");
rabbitAlarms.onmessage = function(event) {
    // Clear the alarm list
    document.querySelectorAll(`[id^="rabbitAlarms_"]`).forEach(span => {
        span.innerText = 0;
    });


    var alarmList = JSON.parse(event.data);

    if(alarmList.length > 0) {
        spanImageWarningCount.innerText = "" + alarmList.length;
        console.log("# Rabbit alarm: " + alarmList.length);
        alarmList.forEach(alarm => {
            const rabbitAlarmsSpan = document.querySelector("#rabbitAlarms_" + alarm.location);
            rabbitAlarmsSpan.innerText = parseInt(rabbitAlarmsSpan.innerText) + 1;
        });
    }
}

var temperatureAlarms = new EventSource("/dashboard/temperature-alarms");
temperatureAlarms.onmessage = function(event) {

    // Clear the temp alarms
    document.querySelectorAll(`[id^="temperatureAlarms_"]`).forEach(span => {
        span.innerText = 0;
    });


    var alarmList = JSON.parse(event.data);

    if(alarmList.length > 0) {
        spanTempWarningCount.innerText = "" + alarmList.length;
        console.log("# Temperature alarm: " + alarmList.length);




        // Find and update the temp warning device in the device list.
        alarmList.forEach(alarm => {

            const temperatureAlarmsSpan = document.querySelector("#temperatureAlarms_" + alarm.location);
            //const spanLocationNok = document.querySelector("#deviceLocationNok_" + alarm.location);

            //var errorCount = parseInt(spanLocationNok.innerText);
            temperatureAlarmsSpan.innerText = parseInt(temperatureAlarmsSpan.innerText)+1;
            //spanLocationNok.innerText = parseInt(spanLocationNok.innerText)+1;

        });


    }
}