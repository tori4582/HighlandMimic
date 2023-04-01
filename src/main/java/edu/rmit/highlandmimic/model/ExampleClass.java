package edu.rmit.highlandmimic.model;

import javax.annotation.PostConstruct;

public class ExampleClass {

    @PostConstruct
    public void init() {
        System.out.println("Hello World, I'm an ExampleClass");
    }

}
