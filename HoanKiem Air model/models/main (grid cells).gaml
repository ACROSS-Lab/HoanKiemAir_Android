/***
* Name: maingridcells
* Author: minhduc0711 + hungnq
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model main

import "agents/traffic.gaml"
import "agents/pollution.gaml"
import "agents/visualization.gaml" 

global skills:[network] {
	
	map<string, param_indicator> VISUAL_INDICATORS <- [];
	
	int port <- 3000;
	string fct;
	int val;
	string param;
	unknown client;
	bool exist <- false;
	message mess;
	list<unknown> list_client;
	string str_mess;
	list<float>list_pollution;
	// Benchmark execution time
	bool benchmark <- false;
	float time_absorb_pollutants;
	float time_diffuse_pollutants;
	float time_create_congestions;

	float step <- 16#s;
	date starting_date <- date(starting_date_string,"HH mm ss");
	
	// Load shapefiles
	string resources_dir <- "../includes/bigger_map/";
	shape_file map_boundary_rectangle_shape_file <- shape_file(resources_dir + "resize_rectangle.shp");	
	shape_file roads_shape_file <- shape_file(resources_dir + "full_roads.shp");
	shape_file dummy_roads_shape_file <- shape_file(resources_dir + "small_dummy_roads.shp");
	shape_file buildings_shape_file <- shape_file(resources_dir + "buildings.shp");
	shape_file buildings_admin_shape_file <- shape_file(resources_dir + "buildings_admin.shp");
	shape_file naturals_shape_file <- shape_file(resources_dir + "naturals.shp");


	
	geometry shape <- envelope(buildings_shape_file);
	closed_roads_graphics crg;
	list<road> open_roads;
	list<pollutant_cell> active_cells;
	
	init {
		create closed_roads_graphics{
			myself.crg <- self;
		}
		
		
		create boundary from: map_boundary_rectangle_shape_file;		
		create road from: roads_shape_file {
			// Create a reverse road if the road is not oneway
			if (!oneway) {
				create road {
					shape <- polyline(reverse(myself.shape.points));
					name <- myself.name;
					type <- myself.type;
				}
			}
		}
		open_roads <- list(road);
		map<road, float> road_weights <- road as_map (each::each.shape.perimeter); 
		road_network <- as_edge_graph(road) with_weights road_weights;
		geometry road_geometry <- union(road accumulate (each.shape));
		active_cells <- pollutant_cell overlapping road_geometry;
		
		// Additional visualization
		create building from: buildings_shape_file {
			p_cell <- pollutant_cell closest_to self;
		}
		create decoration_building from: buildings_admin_shape_file;
		create dummy_road from: dummy_roads_shape_file;
		create natural from: naturals_shape_file;
		
//		create background with: [x::-1350, y::1000, width::1300, height::1100, alpha::0.6];
//		create param_indicator with: [x::-1300, y::1100, size::20, name::"Time", value::"00:00:00"];
//		create progress_bar with: [x::-1300, y::1300, width::500, height::100, max_val::500, title::"Cars",  left_label::"0", right_label::"500"];
//		create progress_bar with: [x::-1300, y::1650, width::500, height::100, max_val::1000, title::"Motorbikes", left_label::"0", right_label::"1500"];
//		create param_indicator with: [x::-1300, y::1950, size::20, name::"Road scenario", value::"no blocked roads"];
//		create param_indicator with: [x::-1300, y::2050, size::20, name::"Display mode", value::"traffic"];

		create progress_bar    with: [x::3100, y::1200, width::350, height::100, max_val::500, title::"Cars",  left_label::"0", right_label::"Max"];
		create progress_bar    with: [x::3100, y::1550, width::500, height::100, max_val::1000, title::"Motorbikes", left_label::"0", right_label::"Max"];
		create param_indicator with: [x::3100, y::1850, size::22, name::"Road scenario", value::"No blocked roads", with_RT::true];
		create param_indicator with: [x::3100, y::2050, size::22, name::"Display mode", value::"Traffic"];
		
//		create background with: [x::2450, y::1000, width::1250, height::1500, alpha::0.6];
//		create line_graph with: [x::2500, y::1400, width::1200, height::1000, label::"Hourly AQI"];
		create line_graph_aqi with: [x::2500, y::2300, width::1100, height::500, label::"Hourly AQI"];
//		create indicator_health_concern_level with: [x::2800, y::2803, width::800, height::200];
		create param_indicator with: [x::2500, y::2803, size::30, name::"Time", value::"00:00:00", with_box::true, width::1100, height::200];		
		
		VISUAL_INDICATORS <- param_indicator as_map ((each.name)::each);
		write "port " + port;
		do connect protocol: "tcp_server" port: port raw:true;
		
		
		
	}
	
	
	reflex receive when: has_more_message() {
		loop while: has_more_message() {
			
			mess <- fetch_message();
			str_mess <- string(mess.contents);
			client <- mess.sender;
		
			if(client in list_client){
				exist <- true;
			}else{
				list_client << client;
				
			}
			
			do pre_process_message(str_mess);
	
		}
				
	
	}
	
	action pre_process_message(string mess1){
		if(mess1 = "Hello Server"){
			write mess1;
		}else{
			
			fct <- (mess1 split_with ';')[0];
			let idx <- fct index_of '{';
			fct <- copy_between(fct, idx + 1 , length(fct));
		
			param <- (mess1 split_with ';')[1];
			idx <- param index_of '}';
			param <- copy_between(param, 0 , idx);
			write 'The parameter:' + fct + ' , value:' + param;
		
			do handleMess(fct,param);	
		}
		
				
	}
	
	action handleMess(string parameter, string value) {
		switch parameter {
			match 'n_cars'{
				val <- int(value);
				n_cars <- val;
				break;
			}
			match 'n_motorbikes'{
				val <- int(value);
				n_motorbikes <- val;
				break;
			}
			match 'display_mode'{
				val <- int(value);
				display_mode <- val = 0;
				break;
			}
			match 'day_time_traffic'{
				val <- int(value);
				if(val = 1){
						day_time_traffic <- true;
						refreshing_rate_plot <- 1#h;
						starting_date_string <- "05 00 00";
						step <- 5#mn;		
				}else{
						day_time_traffic <- false;
						refreshing_rate_plot <- 1#mn;
						starting_date_string <- "00 00 00";
						step <- 16#s;
						n_cars <- 0;
						n_motorbikes <- 0;
				}
				break;
			}
			match 'Block_of_polygon'{
				let list_coord <- param replace("lat/lng:", "") 
									replace("[", "") 
									replace("]", "") 
									replace(")", "") 
									replace(" ", "");
				list<point> list_points;
				loop coord over: list_coord split_with "(" {
					let list_val <- coord split_with ",";
					let p <- {float(list_val[1]), float(list_val[0])};
					list_points <- list_points + p;
				}
					
				ask crg {
					rect <- to_GAMA_CRS(polygon(list_points), "EPSG:4326");
					write list_points;						
				}
				break;
				
			}
		}
		
	}
	
	
	reflex list_pollution when:(cycle mod 5 = 0){
		
		ask line_graph_aqi {
			list_pollution <- val_list;
		}
		
		ask world{
			do send to: list_client contents: list_pollution;
		}
		
	
	}
	
	
	action update_vehicle_population(string type, int delta) {
		list<vehicle> vehicles <- vehicle where (each.type = type);
		if (delta < 0) {
			ask -delta among vehicles {
				do die;
			}
		} else {
			create vehicle number: delta with: [type::type];
		}
	}
	
	reflex update_vehicle_population_according_to_daytime when:day_time_traffic {
		float t_rate <- general_traffic_daytime();
		n_cars <- int(max_number_of_cars * t_rate);
		n_motorbikes <- int(max_number_of_motorbikes * t_rate);
	}
	
	action update_car_population  {
		int delta_cars <- n_cars - length(vehicle count (each.type = "car"));
		do update_vehicle_population("car", delta_cars);
		ask first(progress_bar where (each.title = "Cars")) {
			do update(float(n_cars));
		}
	}
	
	action update_motorbike_population  {
		int delta_motorbikes <- n_motorbikes - length(vehicle count (each.type = "motorbike"));
		do update_vehicle_population("motorbike", delta_motorbikes);
		ask first(progress_bar where (each.title = "Motorbikes")) {
			do update(float(n_motorbikes));
		}
	}

	action update_road_scenario {
		string param_val;
		
		ask crg{
			closed_roads <- road overlapping rect;
		}
		
		open_roads <- road - crg.closed_roads;
		
		// Recreate road network
		map<road, float> road_weights <- open_roads as_map (each::each.shape.perimeter); 
		road_network <- as_edge_graph(open_roads) with_weights road_weights;
		
		ask vehicle {
			recompute_path <- true;
		}
		
		// Change the display of roads
		ask open_roads {
			closed <- false;
		}
		ask crg.closed_roads {
			closed <- true;
		}

		// Choose the active cells again
		geometry road_geometry <- union(open_roads accumulate each.shape);
		active_cells <- pollutant_cell overlapping road_geometry;
		
		ask VISUAL_INDICATORS["Road scenario"] {
			do update(param_val);
		}
	}
	
	action update_display_mode  {
		string param_val <- display_mode ? "Traffic":"Pollution"; 
		ask VISUAL_INDICATORS["Display mode"] {
			do update(param_val);
		}
	}
	
	reflex update_time {
		int h <- current_date.hour;
		int m <- current_date.minute;
		int s <- current_date.second;
		string hh <- ((h < 10) ? "0" : "") + string(h);
		string mm <- ((m < 10) ? "0" : "") + string(m);
		string ss <- ((s < 10) ? "0" : "") + string(s);
		string t <- hh + ":" + mm + ":" + ss;
		ask VISUAL_INDICATORS["Time"] {
			do update(t);
		}
	}
	
	reflex update_building_aqi {
		ask building parallel: true {
			aqi <- pollutant_cell(p_cell).aqi;
		}
	}
	
	reflex create_congestions {
		float start <- machine_time; 
		ask open_roads {
			list<vehicle> vehicles_on_road <- vehicle at_distance 1;
			int n_cars_on_road <- vehicles_on_road count (each.type = "car");
			int n_motorbikes_on_road <- vehicles_on_road count (each.type = "motorbike");
			do update_speed_coeff(n_cars_on_road, n_motorbikes_on_road);
		}
		
		map<float, float> road_weights <- open_roads as_map (each::(each.shape.perimeter / each.speed_coeff));
		road_network <- road_network with_weights road_weights;
		time_create_congestions <- machine_time - start;
	}
	
	matrix<float> mat_diff <- matrix([
		[pollutant_diffusion,pollutant_diffusion,pollutant_diffusion],
		[pollutant_diffusion, (1 - 8 * pollutant_diffusion) * pollutant_decay_rate, pollutant_diffusion],
		[pollutant_diffusion,pollutant_diffusion,pollutant_diffusion]]);

		
	reflex produce_pollutant {
		float start <- machine_time;
		// Absorb pollutants emitted by vehicles
		ask active_cells parallel: true {
			list<vehicle> vehicles_in_cell <- vehicle inside self;
			loop v over: vehicles_in_cell {
				if (is_number(v.real_speed)) {
					float dist_traveled <- v.real_speed * step / #km;
	
					co <- co + dist_traveled * EMISSION_FACTOR[v.type]["CO"];
					nox <- nox + dist_traveled * EMISSION_FACTOR[v.type]["NOx"];
					so2 <- so2 + dist_traveled * EMISSION_FACTOR[v.type]["SO2"];
				    pm <- pm + dist_traveled * EMISSION_FACTOR[v.type]["PM"];
				}
			}
		}
		time_absorb_pollutants <- machine_time - start;
		
		// Diffuse pollutants to neighbor cells
		start <- machine_time;
		diffuse var: co on: pollutant_cell matrix: mat_diff;
		diffuse var: nox on: pollutant_cell matrix: mat_diff;
		diffuse var: so2 on: pollutant_cell matrix: mat_diff;
		diffuse var: pm on: pollutant_cell matrix: mat_diff;
		time_diffuse_pollutants <- machine_time - start;
	}
	
	reflex calculate_aqi when: every(refreshing_rate_plot) { //every(1 #minute) {
		 float aqi <- max(pollutant_cell accumulate each.aqi);
		 ask line_graph_aqi {
		 	do update(aqi);
		 }
		 ask indicator_health_concern_level {
		 	do update(aqi);
		 }
	}
	
	// ---------- DAYTIME CYCLES ---------- //
	
	/*
	 * Compute a background color according to day time
	 */
	rgb day_time_color <- #black;
	reflex general_color_brew when:day_time_color_blender{
		if(day_time_colors.keys one_matches (each.hour = current_date.hour 
			and each.minute = current_date.minute and each.second = current_date.second
		)){
			day_time_color <- day_time_colors[day_time_colors.keys first_with (each.hour = current_date.hour)]; 
		} else {
			date fd <- (day_time_colors.keys where (each.hour > current_date.hour)) with_min_of (each.hour - current_date.hour);
			if(fd = nil){ fd <- first(day_time_colors.keys); }
			date pd <- (day_time_colors.keys where (each.hour <= current_date.hour)) with_min_of (current_date.hour - each.hour);
			if(pd = nil){ pd <- last(day_time_colors.keys); }
			
			float time_to_go_next <- ((fd.hour#h+fd.minute#mn) - (current_date.hour#h+current_date.minute#mn));
			float dist_between_time <- ((fd.hour#h+fd.minute#mn) - (pd.hour#h+pd.minute#mn));
			float blend_factor <- time_to_go_next / dist_between_time;
			
			day_time_color <- blend(day_time_colors[fd],day_time_colors[pd],1-blend_factor);
		}
		day_time_color <- blend(#black,day_time_color,1-day_time_color_blend_factor);
	}
	
	
	float general_traffic_daytime {
		if(daytime_trafic_peak.keys one_matches (each.hour = current_date.hour 
			and each.minute = current_date.minute and each.second = current_date.second)){
			// Only work when only one peak per hour
			return daytime_trafic_peak[daytime_trafic_peak.keys first_with (each.hour = current_date.hour)];	
		} else {
			date fd <- (daytime_trafic_peak.keys where (each.hour > current_date.hour)) with_min_of (each.hour - current_date.hour);
			if(fd = nil){ fd <- first(daytime_trafic_peak.keys); }
			date pd <- (daytime_trafic_peak.keys where (each.hour <= current_date.hour)) with_min_of (current_date.hour - each.hour);
			if(pd = nil){ pd <- last(daytime_trafic_peak.keys); }
			
			float time_to_go_next <- ((fd.hour#h+fd.minute#mn) - (current_date.hour#h+current_date.minute#mn));
			float dist_between_time <- ((fd.hour#h+fd.minute#mn) - (pd.hour#h+pd.minute#mn));
			float blend_factor <- time_to_go_next / dist_between_time;
			
			return daytime_trafic_peak[fd] * (1-blend_factor) + daytime_trafic_peak[pd] * blend_factor;
		}	
	}

	// ---------- BENCHMARK ---------- //
	
	reflex benchmark when: benchmark and every(10 #cycle) {
		write "Vehicles move: " + time_vehicles_move;
		write "Path recomputed: " + nb_recompute_path;
		write "Create congestions: " + time_create_congestions;
		write "Absorb pollutants: " + time_absorb_pollutants;
		write "Diffuse pollutants: " + time_diffuse_pollutants;
		time_vehicles_move <- 0.0;
	}
}


experiment exp autorun: true {
	parameter "Number of cars" var: n_cars <- 0 min: 0 max: 500 on_change: {ask simulation {do update_car_population;}};
	parameter "Number of motorbikes" var: n_motorbikes <- 0 min: 0 max: 1000 on_change: {ask simulation {do update_motorbike_population;}};
	parameter "Close roads" var: road_scenario <- 0 min: 0 max: 2 on_change: {ask simulation {do update_road_scenario;}};
	parameter "Display mode" var: display_mode <- true labels: ["Traffic","Pollution"] on_change: {ask simulation {do update_display_mode;}};
	parameter "Refreshing time plot" var: refreshing_rate_plot init: 2#mn min:1#mn max: 1#h;
	
	output {
		display main type: opengl fullscreen: false toolbar: false background: day_time_color 
		// draw_env: true
//camera_location: {983.1376,1519.9429,3978.7622} camera_target: {983.1376,1519.8784,-0.0026} camera_orientation: {0.0,1.0,0.0}
//keystone: [{-0.024201832069909168,-0.02964181368886576,0.0},{-0.019361465655927335,1.011474250460207,0.0},{0.9965425954185845,1.0076495003068047,0.0},{0.9965425954185843,0.006693312768453641,0.0}]
		{
			species boundary;			
			species vehicle;
			species road;
			species natural;
			species building;
			species decoration_building;
			species dummy_road;
		//  grid pollutant_cell transparency: (display_mode = 0) ? 1.0 : 0.4 elevation: norm_pollution_level * 10 triangulation: true;
			
		//	species background;
			species progress_bar;
			species param_indicator;
	   //	species line_graph;
			species line_graph_aqi;
			species indicator_health_concern_level;
			species closed_roads_graphics;
		}
	}
	
	
	
}
