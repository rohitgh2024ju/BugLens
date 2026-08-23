package dev.rohit.buglens.ParserEngine;

import dev.rohit.buglens.ParserEngine.plugins.SpringBootParser;

// finds the correct parser
public class ParserRegistry {
    String parserClass;

    ParserRegistry(String parserClass) {
        this.parserClass = parserClass;
    }

    public LogParser find() {

        if (parserClass.equals("SpringBootParser")) {
            return new SpringBootParser();
        };

        return null;
    }
}
