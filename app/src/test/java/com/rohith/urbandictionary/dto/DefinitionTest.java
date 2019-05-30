package com.rohith.urbandictionary.dto;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefinitionTest {


    @Test
    public void testThumbsUpASCComparator() {
        Definition def1 = new Definition();
        def1.setDefid(1111);
        def1.setThumbsUp(51);

        Definition def2 = new Definition();
        def2.setDefid(2222);
        def2.setThumbsUp(50);

        int result = Definition.THUMBSUP_ASC.compare(def1,def2);
        assertTrue("expected to be greater than",result == 1);
    }

    @Test
    public void testThumbsDownASCComparator() {
        Definition def1 = new Definition();
        def1.setDefid(1111);
        def1.setThumbsDown(50);

        Definition def2 = new Definition();
        def2.setDefid(2222);
        def2.setThumbsDown(50);

        int result = Definition.THUMBSDOWN_ASC.compare(def1,def2);
        assertTrue("expected to be equal",result == 0);
    }

}
