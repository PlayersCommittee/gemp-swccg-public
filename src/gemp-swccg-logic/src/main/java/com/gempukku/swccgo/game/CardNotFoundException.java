package com.gempukku.swccgo.game;

public class CardNotFoundException extends Exception {
	public CardNotFoundException(String blueprint) {
		super(blueprint);
	}
}