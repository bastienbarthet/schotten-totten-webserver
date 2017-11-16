package com.boardgames.bastien.schotten_totten.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Bastien on 28/11/2016.
 */

//@JsonSerialize(using = CardSerializer.class)
//@JsonDeserialize(using = CardDeserializer.class)
public class Card {

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum COLOR {
        BLUE("BLUE"),
        CYAN("CYAN"),
        RED("RED"),
        YELLOW("YELLOW"),
        GREEN("GREEN"),
        GREY("GREY");
    	
    	public final String color;
    	
    	private COLOR(final String c) {
    		color = c;
    	}

    	@Override
        public String toString() {
            return String.valueOf(ordinal() + 1);
        }
    }

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum NUMBER {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9);

    	public final int number;
    	
    	private NUMBER(final int n) {
    		number = n;
    	}
    	
    	@Override
        public String toString() {
            return String.valueOf(number);
        }
    }

    private final NUMBER number;

    private final COLOR color;

    public Card(final NUMBER n, final COLOR c) {
        this.number = n;
        this.color = c;
    }
    
//    public Card(final String n, final String c) {
//        this.number = NUMBER.valueOf(n);
//        this.color = COLOR.valueOf(c);
//    }

    public NUMBER getNumber() {
        return number;
    }

    public COLOR getColor() {
        return color;
    }
}
