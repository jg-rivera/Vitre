	Chart.pluginService.register({
		beforeDraw: function (chart) {
			if (chart.config.options.elements.center) {
        //Get ctx from string
        var ctx = chart.chart.ctx;
        
				//Get options from the center object in options
        var centerConfig = chart.config.options.elements.center;
      	var fontStyle = centerConfig.fontStyle || 'Arial';
				var txt = centerConfig.text;
        var color = centerConfig.color || '#000';
        var sidePadding = centerConfig.sidePadding || 20;
        var sidePaddingCalculated = (sidePadding/100) * (chart.innerRadius * 2)
        //Start with a base font of 30px
        ctx.font = "30px " + fontStyle;
        
				//Get the width of the string and also the width of the element minus 10 to give it 5px side padding
        var stringWidth = ctx.measureText(txt).width;
        var elementWidth = (chart.innerRadius * 2) - sidePaddingCalculated;

        // Find out how much the font can grow in width.
        var widthRatio = elementWidth / stringWidth;
        var newFontSize = Math.floor(20 * widthRatio);
        var elementHeight = (chart.innerRadius * 2);

        // Pick a new font size so it will not be larger than the height of label.
        var fontSizeToUse = Math.min(newFontSize, elementHeight);

				//Set font settings to draw it correctly.
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2);
        var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 2);
        ctx.font = fontSizeToUse+"px " + fontStyle;
        ctx.fillStyle = color;
        
        //Draw text in center
        ctx.fillText(txt, centerX, centerY);
			}
		}
	});
		function shadeColor2(color, percent) {   
    var f=parseInt(color.slice(1),16),t=percent<0?0:255,p=percent<0?percent*-1:percent,R=f>>16,G=f>>8&0x00FF,B=f&0x0000FF;
    return "#"+(0x1000000+(Math.round((t-R)*p)+R)*0x10000+(Math.round((t-G)*p)+G)*0x100+(Math.round((t-B)*p)+B)).toString(16).slice(1);
}

		var inputData = [92, 93, 90, 95, 82];
		
		function getColors() {
			var lastColor;
			var baseColor = "#ffd700";
			for (i = 0; i < inputData.length; i++) {
				if (!lastColor)
					lastColor = baseColor;
				colors[i] = shadeColor2(lastColor, -0.05);
				lastColor = colors[i];
			}	
		};
		var colors = [];
		getColors();
		
		
		
		var config = {
			type: 'doughnut',
			data: {
				labels: [
				  "UCSP",
				  "General Biology",
				  "Earth Science",
				  "Bananaliksik",
				  "Research in Daily Life"
				],
				datasets: [{
					data: inputData,
					backgroundColor: colors,
					hoverBackgroundColor: colors
				}]
			},
		options: {
			responsive: true,
			maintainAspectRation: false,
			legendCallback: function (chart) {
					var text = [];
			text.push('<ul class="' + chart.id + '-legend">');
					var data = chart.data;
					var datasets = data.datasets;
					var labels = data.labels;

					if (datasets.length) {
						for (var i = 0; i < datasets[0].data.length; ++i) {
							if (labels[i]) {
								text.push('<li class="list-group-item">');
								text.push('<span class="chart-legend" style="font-size:30px; -webkit-text-stroke: 1px black;color:' + datasets[0].backgroundColor[i] +'">&#x25CF; </span>');
								text.push(labels[i] + ': ' + datasets[0].data[i]);
								text.push('</li>');
							}
							
						}
					}
					text.push('</ul>');
					return text.join('');
				},       
			legend: {
				display: false,
				position: 'bottom',
				labels: {
					lineWidth: 10,
					boxWidth: 12
				}
			},
    	cutoutPercentage: 75,
			elements: {
				center: {
					text: '91',
					color: '#551a8b', // Default is #000000
					fontStyle: 'Nunito', // Default is Arial
					sidePadding: 20 // Defualt is 20 (as a percentage)
				}
			}
		}
	};

		var ctx = document.getElementById("myChart").getContext("2d");
		var myChart = new Chart(ctx, config);
		var legend = myChart.generateLegend();
		document.getElementById("legend").innerHTML = legend;