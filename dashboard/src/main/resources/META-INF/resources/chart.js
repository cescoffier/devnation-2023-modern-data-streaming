var margin = {top: 30, right: 0, bottom: 10, left: 30},
width = 320 - margin.left - margin.right,
height = 130 - margin.top - margin.bottom;

var messageStatData = [
  0,0,0,0,0,0,0,0,0,0
];

var imageStatData = [
  0,0,0,0,0,0,0,0,0,0
];

const messageSvg = d3.select("#messageCountChart").append("svg");
const imageSvg = d3.select("#imageProcessChart").append("svg");


// Add X axis --> it is a date format
var x = d3.scaleLinear()
  .domain([0,10])
  .range([ 0, width ]);

// Add Y axis
var y = d3.scaleLinear()
  .domain([0, 500])
  .range([ height, 0 ]);

appendGraphs()

const messageCountTextItem = document.querySelector("#MessageCountCurrentVolume");
const imageProcessedTextItem = document.querySelector("#imageProcessedCount");
const displayCriticalCount = document.querySelector("#criticalCount");
const displayWarningCount = document.querySelector("#warningCount");
const displayInformationCount = document.querySelector("#informationCount");


console.log(imageProcessedTextItem);



// Message stats
var messagestats = new EventSource("/dashboard/message-stats");
messagestats.onmessage = function(event) {
    var message = JSON.parse(event.data);

    messageCountTextItem.innerText = " " + Math.round(message.avaragePerSecond) + " m/s";
    
    messageStatData.push(message.avaragePerSecond)
    messageStatData.shift();
    // do stuff based on received message
    updateMessageChart();
  
}

// Image stats
var imagestats = new EventSource("/dashboard/image-processed-stats");
imagestats.onmessage = function(event) {
  var imagestat = JSON.parse(event.data);
  imageProcessedTextItem.innerText = " " + Math.round(imagestat.totalProcessed) + " images"

  imageStatData.push(imagestat.averageProcessed);
  imageStatData.shift();

  updateImageChart();
}


var alarms = new EventSource("/dashboard/alarms");
alarms.onmessage = function(event) {
  var alarmList = JSON.parse(event.data);
  console.log([alarmList]);
  // console.log([alarmList].filter(a => a.type==="INFORMATION"));
  // console.log([alarmList].filter(a => a.type==='WARNING'));
  // console.log([alarmList].filter(a => a.type==="CRITICAL"));
  // console.log(alarmList.filter(function(x) { console.log(x.type); return true}))
  var numberOfCriticalAlarms = alarmList.filter(a => a.type==="CRITICAL").length
  var numberOfWarnings = alarmList.filter(a => a.type==="WARNING").length
  var numberOfInformation = alarmList.filter(a => a.type==="INFORMATION").length


  displayCriticalCount.innerText = "" + numberOfCriticalAlarms;
  displayWarningCount.innerText = "" + numberOfWarnings;
  displayInformationCount.innerText = "" + numberOfInformation;

  
  
  


}




function appendGraphs() {
  // Add the area  
  messageSvg.append("path")
  .data([messageStatData])
  .attr("fill", "cyan")
  // .attr("stroke", "steelblue")
  // .attr("stroke-width", 1.5)
  .attr("class","fillarea")
  .attr("d", d3.area()
    .x(function(d,index) { return x(index) })
    .y0(function(d) { return height })
    .y1(function(d) { return y(d) })
    );

  // Add the line
  messageSvg.append("path")
  .data([messageStatData])
  .attr("fill", "none")
  .attr("stroke", "steelblue")
  .attr("stroke-width", 1.5)
  .attr("class","topline")
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  );

  // Add the area  
  imageSvg.append("path")
  .data([imageStatData])
  .attr("fill", "cyan")
  // .attr("stroke", "steelblue")
  // .attr("stroke-width", 1.5)
  .attr("class","fillarea")
  .attr("d", d3.area()
    .x(function(d,index) { return x(index) })
    .y0(function(d) { return height })
    .y1(function(d) { return y(d) })
    );

  // Add the line
  imageSvg.append("path")
  .data([imageStatData])
  .attr("fill", "none")
  .attr("stroke", "steelblue")
  .attr("stroke-width", 1.5)
  .attr("class","topline")
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  );

}

function updateMessageChart() {
  messageSvg.selectAll('.topline')
  .data([messageStatData])
  .transition()
  .duration(200)
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  )

  // Add the area  
  messageSvg.selectAll('.fillarea')
    .data([messageStatData])
    .transition()
    .duration(200)
    .attr("d", d3.area()
      .x(function(d,index) { return x(index) })
      .y0(function(d) { return height })
      .y1(function(d) { return y(d) })
      );

}


function updateImageChart() {
  imageSvg.selectAll('.topline')
  .data([imageStatData])
  .transition()
  .duration(200)
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  )

  // Add the area  
  imageSvg.selectAll('.fillarea')
    .data([imageStatData])
    .transition()
    .duration(200)
    .attr("d", d3.area()
      .x(function(d,index) { return x(index) })
      .y0(function(d) { return height })
      .y1(function(d) { return y(d) })
      );

}

