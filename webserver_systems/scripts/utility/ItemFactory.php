<?php

function createContent($desc, $date, $score, $items) {
	echo '<li class="list-group-item">';
	echo '<p class="mb-1" style="display: inline">' . $desc . '</p>';
	echo '<div class="row">';
	echo '<div class="col-sm">';
	if ($score >= 0) {
		echo '<small style="color:green;"><i class="fas fa-check"></i> Taken </small>';
	} else {
		echo '<small style="color:red;"><i class="fas fa-times"></i> Missing </small>';
	}
	echo '<img class="icon" src="/images/icons/cloud-upload.svg" alt="Uploaded"> </img>';
	echo '<small class="text-muted">' . $date . '</small>';
	echo '</div>';
	echo '<div class="col-sm">';
	echo '<div class="float-right">';
	echo '<div id="score"> Score </div>';
	echo '<span style="font-size: 140%;">' . $score . ' / ' . $items . '</span>';
	echo '</div></div></div>';
	echo '</li>';
}

function createNoAct() {
	echo '<div class="no-act">';
	echo '<i class="fa fa-ellipsis-h" style="color:#808080;"></i>';
	echo '<small class="text-muted"> No activities. </small></div>';
}

function createFeed($s_id, $subject, $teacher, $itemName, $desc, $stamp, $scoreItem, $totalItem) {
	echo "<p style='margin-top: -5px;'> </p><div data-id=$s_id class='list-group-item list-group-item-action flex-column align-items-start'>";
	echo '<div class="d-flex w-100 justify-content-between">';
	echo '<h5 class="mb-1">' . $subject . '</h5>';
	//echo '<small><img class="icon" src="/images/icons/pulse.svg" alt="Uploaded"></small>';
	echo '</div>';
	echo '<div class="float-right"><span style="font-size: 140%;">'. $scoreItem . '</span>';
	echo '<span style="font-size: 105%;"> / ' . $totalItem . '</span></div>';
	echo '<p class="mb-1">' .$desc . '</p>';
	echo '<img class="icon" src="/images/icons/person.svg" alt="Teacher"> ';
	echo '<small class="text-muted">' . $teacher . '</small> ';
	echo '<img class="icon" src="/images/icons/cloud-upload.svg" alt="Uploaded"> ';
	echo '<small class="text-muted">' . $stamp . '</small> <br>';
	
	if ($scoreItem >= 0)
		echo '<span class="badge badge-success">Taken</span> ';
	else
		echo '<span class="badge badge-danger">Missing</span> ';
	
	echo '<span class="badge badge-info">'. $itemName . '</span>  </div>';
}

function createSubjectCard($s_id, $subject, $teacher, $track, $semester, $icon) {
	echo "<div class='s-card'>";
	echo "<div data-id=$s_id class='card border-secondary' style='cursor: pointer;'>";
	echo '<div class="row no-gutters">';
	echo '<div class="col">';
	echo '<div class="card-block px-3">';
	echo '<h4 class="card-title">' . $subject . '</h4>';
	echo '<img class="icon" src="/images/icons/person.svg" title="Teacher" alt="Teacher"> ';
	echo '<small class="text-muted">' . $teacher . '</small> <br>';							
	echo '<img class="icon" src="/images/icons/tags.svg" title="Tags" alt="Tags"> </img>';
	echo '<span class="badge badge-info">' . $semester .'</span> ';
	echo '<span class="badge badge-warning">' . $track . '</span> </div> </div>';
	echo '<div class="col-auto">';
	echo '<span class="helper"> </span>';
	echo '<img id="cardImg" src="' .  $icon . '" class="img-fluid" alt=""> </div> </div> </div>';
	echo '<div style="padding-bottom: 0.5em;"> </div>';
	echo '</div>';
}
?>