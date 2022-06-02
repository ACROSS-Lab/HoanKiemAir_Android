/**
* Name: Testtcp
* Based on the internal empty template. 
* Author: Mr Ngoc
* Tags: 
*/


model Testtcp

/* Insert your model definition here */

global skills:[network]{
	
	init {
	
		do connect protocol: "tcp_server" port:3001;
	
	}
	
	
	reflex receive when: has_more_message() {
		loop while: has_more_message() {
			message mm <- fetch_message();
			write name + " received : " + mm.contents color: color;
		}

	}
	
}


experiment test {}