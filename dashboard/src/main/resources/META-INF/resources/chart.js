const aapl = d3.dsv(",","aapl.csv",(d) => {
    return {
        date: d.date,
        close: d.close
    };
});

console.log(aapl);