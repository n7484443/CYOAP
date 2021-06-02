module com.project.cyoap{
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	
	opens core;	
	opens design;
	opens design.controller;
}