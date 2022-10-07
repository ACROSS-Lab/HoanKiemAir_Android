/**
* Name: Connection
* Description:
* Author: hungnq
* Tags: 
*/

model Connection

import "global_vars.gaml"


global {
	/** Insert the global definitions, variables and actions here */
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
	geometry rect;
	
	init{
		create Server{
			write "port " + port;
			do connect protocol: "tcp_server" port: port raw:true;
		}
	} 
	
	
}


species Server skills:[network] {
	
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
		val <- int(value);
		switch parameter {
			match 'n_cars'{
				n_cars <- val;
				break;
			}
			match 'n_motorbikes'{
				n_motorbikes <- val;
				break;
			}
			match 'display_mode'{
				display_mode <- val = 0;
				break;
			}
			match 'day_time_traffic'{
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
				}
				break;
			}
			match 'Block_of_polygon'{
				do draw_map;
				break;
				
			}
		}
		
	}
	
	action draw_map{
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
					
		rect <- to_GAMA_CRS(polygon(list_points), "EPSG:4326");
		write list_points;						
		
	}
	
	reflex send_data when:(cycle mod 10 = 0){		
		ask Server{
			do send to: list_client contents: list_pollution;
		}
	}
}

