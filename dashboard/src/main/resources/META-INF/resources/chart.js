var margin = {top: 30, right: 0, bottom: 10, left: 30},
width = 320 - margin.left - margin.right,
height = 130 - margin.top - margin.bottom;

var data = [
  0,0,0,0,0,0,0,0,0,0
];

var svg = d3.select("#messageCountChart").append("svg");


// Add X axis --> it is a date format
var x = d3.scaleLinear()
  .domain([0,10])
  .range([ 0, width ]);

// Add Y axis
var y = d3.scaleLinear()
  .domain([0, 500])
  .range([ height, 0 ]);

appendGraph()



var source = new EventSource("/dashboard/message-stats");
source.onmessage = function(event) {
    var message = JSON.parse(event.data);
    
    data.push(message.avaragePerSecond)
    data.shift();
    // do stuff based on received message
    updateGraph(data)
  
}

function appendGraph() {
  // Add the area  
  svg.append("path")
  .data([data])
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
  svg.append("path")
  .data([data])
  .attr("fill", "none")
  .attr("stroke", "steelblue")
  .attr("stroke-width", 1.5)
  .attr("class","topline")
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  );

}

function updateGraph() {
  svg.selectAll('.topline')
  .data([data])
  .transition()
  .duration(200)
  .attr("d", d3.line()
    .x(function(d,index) { return x(index) })
    .y(function(d) { return y(d) })
  )

  // Add the area  
  svg.selectAll('.fillarea')
    .data([data])
    .transition()
    .duration(200)
    .attr("d", d3.area()
      .x(function(d,index) { return x(index) })
      .y0(function(d) { return height })
      .y1(function(d) { return y(d) })
      );

}