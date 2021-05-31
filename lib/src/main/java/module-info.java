module com.project.cyoap{
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	
	opens core;	
	opens design;
	opens design.controller;
}