<!-- START -->
    <nav class="navbar navbar-expand navbar-dark bg-dark static-top">
		
      <a class="navbar-brand mr-1" href="index.php">
	    <img src="imgs/favicon.png" width="100" height="35" alt="">
		Administrator
	  </a>

      <button class="btn btn-link btn-sm text-white order-1 order-sm-0" id="sidebarToggle" href="#">
        <i class="fas fa-bars"></i>
      </button>

      <!-- Navbar Search -->
      <form class="d-none d-md-inline-block form-inline ml-auto mr-0 mr-md-3 my-2 my-md-0">
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Search for..." aria-label="Search" aria-describedby="basic-addon2">
          <div class="input-group-append">
            <button class="btn btn-primary" type="button">
              <i class="fas fa-search"></i>
            </button>
          </div>
        </div>
      </form>

      <!-- Navbar -->
      <ul class="navbar-nav ml-auto ml-md-0">
        <li class="nav-item dropdown no-arrow mx-1">
          <a class="nav-link dropdown-toggle" href="#" id="messagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <i class="fas fa-envelope fa-fw"></i>
            <span class="badge badge-danger">1</span>
          </a>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="messagesDropdown">
            <a class="dropdown-item" href="#">View messages</a>
            <a class="dropdown-item" href="#">New message</a>
            <div class="dropdown-divider"></div>
            <a class="dropdown-item" href="#">Something else here</a>
          </div>
        </li>
        <li class="nav-item dropdown no-arrow">
          <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <i class="fas fa-user-circle fa-fw"></i>
          </a>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
            <a class="dropdown-item" href="#">Settings</a>
            <a class="dropdown-item" href="#">Activity Log</a>
            <div class="dropdown-divider"></div>
            <a class="dropdown-item" href="#" data-toggle="modal" data-target="#logoutModal">Logout</a>
          </div>
        </li>
      </ul>
    </nav>
	
	<!-- Wrapper -->
    <div id="wrapper">

      <!-- Sidebar -->
      <ul class="sidebar navbar-nav">
        <li class="nav-item active">
          <a class="nav-link" href="index.php">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span>
          </a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="dropDownDb" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <i class="fas fa-fw fa-users"></i>
            <span>Database Registry</span>
          </a>
          <div class="dropdown-menu" aria-labelledby="dropDownDb">
            <a class="dropdown-item" href="registry_students">
				<i class="fas fa-fw fa-user-graduate"></i> Students
			</a>
            <a class="dropdown-item" href="registry_teachers">
				<i class="fas fa-fw fa-chalkboard-teacher"></i> Teachers
			</a>
			<a class="dropdown-item" href="registry_classes">
				<i class="fas fa-fw fa-users"></i> Classes
			</a>
			<a class="dropdown-item" href="registry_subjects">
				<i class="fas fa-fw fa-apple-alt"></i> Subjects
			</a>
			<a class="dropdown-item" href="registry_curricula">
				<i class="fas fa-fw fa-book-reader"></i> Curricula
			</a>
          </div>
        </li>
		<li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="dropDownTools" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <i class="fas fa-fw fa-toolbox"></i>
            <span>Tools</span>
          </a>
			<div class="dropdown-menu" aria-labelledby="dropDownTools">
				<a class="dropdown-item" href="signature_generator">
					<i class="fas fa-fw fa-qrcode"></i> Sig. Generator
				</a>
				<a class="dropdown-item" href="patch_payloader">
					<i class="fas fa-fw fa-asterisk"></i> Patch Payloader
				</a>
			</div>
		</li>
        <li class="nav-item">
          <a class="nav-link" href="cache">
            <i class="fas fa-fw fa-archive"></i>
            <span>Cache Files</span></a>
        </li>
		<li class="nav-item">
          <a class="nav-link" href="tickets">
            <i class="fas fa-fw fa-life-ring"></i>
            <span>Support Tickets</span></a>
        </li>
		<li class="nav-item">
          <a class="nav-link" href="usage_analytics">
            <i class="fas fa-fw fa-chart-area"></i>
            <span>Usage Analytics</span></a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="tables.html">
            <i class="fas fa-fw fa-table"></i>
            <span>Tables</span></a>
        </li>
      </ul>
	<!-- Leave hanging div -->
	<!-- END NAV -->	