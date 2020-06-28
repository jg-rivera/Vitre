<?php

	echo '<div class="form-group">
		  <label for="name">Name</label>
		  <input type="text" class="form-control" id="name">
		</div>';
	
	echo '<div class="form-group">
		  <label for="honorific">Honorific</label>
		  <select class="form-control" id="honorific">';
		// Available honorifics
		echo '<option>Mr.</option>';
		echo '<option>Mrs.</option>';
		echo '<option>Ms.</option>';
		echo '<option>Mx.</option>';
		echo '<option>Prof.</option>';
		echo '<option>Engr.</option>';
		echo '<option>Dr.</option>';
	echo '</select></div>';
?>