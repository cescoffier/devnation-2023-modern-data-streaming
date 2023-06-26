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
const messageCountTextItem = document.querySelector("#MessageCountCurrentVolume");
const imageProcessedTextItem = document.querySelector("#imageProcessedCount");

// Add X axis --> it is a date format
var x = d3.scaleLinear()
  .domain([0,10])
  .range([ 0, width ]);

// Add Y axis
var y = d3.scaleLinear()
  .domain([0, 500])
  .range([ height, 0 ]);

var x2 = d3.scaleLinear()
   .domain([0,10])
   .range([ 0, width ]);

// Add Y axis
var y2 = d3.scaleLinear()
   .domain([0, 200])
   .range([ height, 0 ]);

appendGraphs()

// Message stats
var averageTemperatureEvent = new EventSource("/dashboard/stats/average-temperature-enrichment");
averageTemperatureEvent.onmessage = function(event) {
    var count = event.data;

    messageCountTextItem.innerText = " " + Math.round(count) + " m/s";
    
    messageStatData.push(count)
    messageStatData.shift();
    // do stuff based on received message
    updateMessageChart();
  
}

// Image stats
var imagestats = new EventSource("/dashboard/stats/average-image-processed");
imagestats.onmessage = function(event) {
  var average = event.data;
  imageProcessedTextItem.innerText = " " + average + " images per minute"

  imageStatData.push(average);
  imageStatData.shift();


  updateImageChart();
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
    .x(function(d,index) { return x2(index) })
    .y0(function(d) { return height })
    .y1(function(d) { return y2(d) })
    );

  // Add the line
  imageSvg.append("path")
  .data([imageStatData])
  .attr("fill", "none")
  .attr("stroke", "steelblue")
  .attr("stroke-width", 1.5)
  .attr("class","topline")
  .attr("d", d3.line()
    .x(function(d,index) { return x2(index) })
    .y(function(d) { return y2(d) })
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
    .x(function(d,index) { return x2(index) })
    .y(function(d) { return y2(d) })
  )

  // Add the area  
  imageSvg.selectAll('.fillarea')
    .data([imageStatData])
    .transition()
    .duration(200)
    .attr("d", d3.area()
      .x(function(d,index) { return x2(index) })
      .y0(function(d) { return height })
      .y1(function(d) { return y2(d) })
      );

}

