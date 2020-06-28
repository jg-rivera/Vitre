var canvas = document.getElementById("imminentGrade");
	var ctx = canvas.getContext("2d");
	window.onload=function() {
		var grade = "92";
		var height = 210;
		ctx.imageSmoothingEnabled = true;
		ctx.font='170px Nunito';
		ctx.fillStyle = "#438196";
		ctx.textAlign = "center";
		ctx.lineWidth = 3;
		ctx.strokeStyle="#ffd166";
		ctx.fillText(grade, canvas.width/2, height);
		ctx.strokeText(grade, canvas.width/2, height);
		ctx.font = '30px Nunito';
		ctx.fillStyle = "#ffd166";
		ctx.fillText("Imminent Grade", canvas.width / 2, 65);
	};
	var podium = new Image();
	podium.onload = function() {
		ctx.imageSmoothingEnabled = true;
		ctx.drawImage(podium, 0, 0, podium.width, podium.height, 0, 0, canvas.width , canvas.height);
	};
	podium.src = "images/trophy/default_trophy_top.png";
	