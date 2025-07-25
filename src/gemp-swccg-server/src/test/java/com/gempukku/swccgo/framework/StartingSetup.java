package com.gempukku.swccgo.framework;

import java.util.HashMap;

public interface StartingSetup {
	HashMap<String, String> Cards();
	void Setup(VirtualTableScenario scn);

	/**
	 * An inert startup process that has no cards and does nothing.
	 */
	StartingSetup DoNothingSetup = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() { return new HashMap<>(); }

		@Override
		public void Setup(VirtualTableScenario scn) { }
	};

	/**
	 * A process that is a wrapper for DS starting location deployment
	 */
	static StartingSetup DSStartingLocation(String id) {
		return new StartingSetup() {
			@Override
			public HashMap<String, String> Cards() { return new HashMap<>() {{ put("starting-location", id); }}; }

			@Override
			public void Setup(VirtualTableScenario scn) { scn.DSChooseCard(scn.GetDSCard("starting-location")); }
		};
	}

	/**
	 * A process that is a wrapper for LS starting location deployment
	 */
	static StartingSetup LSStartingLocation(String id) {
		return new StartingSetup() {
			@Override
			public HashMap<String, String> Cards() { return new HashMap<>() {{ put("starting-location", id); }}; }

			@Override
			public void Setup(VirtualTableScenario scn) { scn.LSChooseCard(scn.GetLSCard("starting-location")); }
		};
	}


	/**
	 * An empty outside-of-deck pile communicating that the Dark Side will have no shields or other out-of-game cards
	 * for a particular test scenario.
	 */
	StartingSetup NoDSShields = DoNothingSetup;
	/**
	 * An empty outside-of-deck pile communicating that the Light Side will have no shields or other out-of-game cards
	 * for a particular test scenario.
	 */
	StartingSetup NoLSShields = DoNothingSetup;


	/*
	 * The default locations to be played by either player.  This will be included in the deck and automatically played
	 * at the start of the game.
	 *
	 * When choosing default locations, be sure to pick ones that leave both sides with identical lightsaber icons,
	 * whether that means that both are the same or that one is 1/2 and the other is 2/1.  This way testers do not need
	 * to remember nuances around default activation amounts being different per-side.
	 *
	 * Also ensure that any defaults that are chosen avoid altering the game state; ideally the sites would be blank,
	 * but if that cannot be arranged do not choose cards that grant modifiers (granting +/- X to any stat), as these
	 * inevitably are rakes that future tests will step on. Instead, fall back on locations that have optional actions
	 * that can be ignored.
	 */

	/**
	 * The default ground location used by Dark Side.  This will be played at the start of the game automatically.
	 */
	StartingSetup DefaultDSGroundLocation = DSStartingLocation("12_176");  // Tatooine: Marketplace

	/**
	 * The default ground location used by Light Side.  This will be played at the start of the game automatically.
	 */
	StartingSetup DefaultLSGroundLocation = LSStartingLocation("5_079");  // Cloud City: Chasm Walkway

	/**
	 * The default space system used by Dark Side.  This will be played at the start of the game automatically.
	 */
	StartingSetup DefaultDSSpaceSystem = DSStartingLocation("1_282"); // Dantooine

	/**
	 * The default space system used by Light Side.  This will be played at the start of the game automatically.
	 */
	StartingSetup DefaultLSSpaceSystem = LSStartingLocation("6_087"); // Tibrin


	/**
	 * Testers can use this to choose an alternate one-off starting location. If using this frequently for the same
	 * location, consider adding it above as a static location setup reference.
	 */
	class StartingLocation implements StartingSetup {
		HashMap<String, String> _cards = new HashMap<>();
		public StartingLocation(String id) {
			_cards.put("starting-location", id);
		}

		@Override
		public HashMap<String, String> Cards() { return _cards; }

		@Override
		public void Setup(VirtualTableScenario scn) {
			if(scn.DSDecisionAvailable("Choose starting location")) {
				scn.DSChooseCard(scn.GetDSCard("starting-location"));
			}
			else if(scn.LSDecisionAvailable("Choose starting location")) {
				scn.LSChooseCard(scn.GetLSCard("starting-location"));
			}
		}
	}


	/**
	 * An empty collection communicating that the Dark Side will have no starting interrupts played at
	 * the start of a particular test scenario.
	 */
	StartingSetup NoDSStartingInterrupts = DoNothingSetup;

	/**
	 * An empty collection communicating that the Light Side will have no starting interrupts played at
	 * the start of a particular test scenario.
	 */
	StartingSetup NoLSStartingInterrupts = DoNothingSetup;



	/**
	 * The Dark Side objective Bring Him Before Me / Take Your Father's Place and associated cards.
	 */
	StartingSetup BHBMObjective = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("bhbm", "9_151"); // Objective
				put("throne", "9_147"); // Death Star II: Throne Room
				put("rebellion", "9_127"); // Insignificant Rebellion
				put("destiny", "9_134"); // Your Destiny
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
//			if(scn.DSDecisionAvailable("On which side")) {
//				scn.DSChoose("Left");
//			}
//
//			if(scn.DSDecisionAvailable("Choose a location to deploy ")) {
//				scn.DSChooseCard(scn.GetDSCard("chamber"));
//				scn.DSChoose("Left");
//			}
		}
	};

	/**
	 * The Dark Side objective Carbon Chamber Testing / My Favorite Decoration and associated cards.
	 */
	StartingSetup CarbonChamberObjective = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("testing", "7_296"); // Objective
				put("chamber", "5_166"); // Cloud City: Carbonite Chamber
				put("tower", "5_172"); // Cloud City: Security Tower
				put("console", "5_107"); // Carbonite Chamber Console
				put("prize", "10_42"); // Jabba's Prize
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			if(scn.DSDecisionAvailable("On which side")) {
				scn.DSChoose("Left");
			}

			if(scn.DSDecisionAvailable("Choose a location to deploy ")) {
				scn.DSChooseCard(scn.GetDSCard("chamber"));
				scn.DSChoose("Left");
			}
		}
	};

	/**
	 * The Light Side objective You Can Either Profit By This... / Or Be Destroyed and associated cards.
	 */
	StartingSetup ProfitObjective = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("profit", "110_4"); // Objective
				put("palace", "7_131"); // Tatooine: Jabba's Palace
				put("chamber", "6_81"); // Jabba's Palace: Audience Chamber
				put("han", "1_11"); // Han Solo
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			if(scn.LSDecisionAvailable("On which side")) {
				scn.LSChoose("Left");
			}

			if(scn.DSDecisionAvailable("Choose alien(s) to deploy to Audience Chamber")) {
				scn.DSPass();
			}
		}
	};

	/**
	 * The Light Side objective Rescue The Princess (V) / Sometimes I Amaze Even Myself (V) and associated cards.
	 */
	StartingSetup RescueThePrincessVObjective = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("rescue", "215_17"); // Objective
				put("core", "215_6"); // Death Star: Central Core (Light)
				put("corridor", "215_7"); // Detention Block Corridor (V)
				put("compactor", "215_9"); // Trash Compactor (V)
				put("power", "215_2"); // A Power Loss
				put("prisoner", "220_9"); // Prisoner 2187 (V)
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			//Central Core goes down first
			//Then Detention Block Corridor goes to the right of Core
			if(scn.LSDecisionAvailable("On which side")) {
				scn.LSChoose("Right");
			}

			//Then we place Trash Compactor to the left of Core
			if(scn.LSDecisionAvailable("Choose a location")) {
				scn.LSChooseCard(scn.GetLSCard("core"));
			}

			if(scn.LSDecisionAvailable("On which side")) {
				scn.LSChoose("Left");
			}
		}
	};

	/**
	 * The Light Side objective There Is Good In Him / I Can Save Him and associated cards.
	 */
	StartingSetup ThereIsGoodInHimObjective = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("tigih", "9_61"); // Objective
				put("hut", "8_71"); // Endor: Chief Chirpa's Hut
				put("platform", "8_76"); // Endor: Landing Platform (Docking Bay)
				put("lsjk", "9_24"); // Luke Skywalker, Jedi Knight
				put("lightsaber", "9_90"); //Luke's Lightsaber
				put("conflict", "9_34"); //I Feel The Conflict
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			if(scn.LSDecisionAvailable("On which side")) {
				scn.LSChoose("Left");
			}
		}
	};
}

