package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_2_056_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
			new HashMap<>()
			{{
				put("sabotage", "2_56");
				put("spy", "7_5"); // Bothan Spy
				put("merc", "2_36"); // Merc Sunlet (thief skill)
				put("lsBlaster", "1_152"); // LS Blaster, Use 1
				put("lsTrooper", "1_28"); // Rebel Trooper, warrior
				put("undercover", "2_40"); // Undercover (2_40), LS Effect
			}},
			new HashMap<>()
			{{
				put("trooper", "1_194"); // Stormtrooper
				put("blaster", "1_317"); // Imperial Blaster (Use 1)
				put("blaster2", "1_317");
				put("vader", "1_168");
				put("saber", "1_314"); // Dark Jedi Lightsaber
				put("saber2", "1_314");
				put("liftTube", "1_308"); // vehicle, deploy 1
				put("droid", "1_163"); // 5D6-RA-7
				put("bolt", "1_205"); // Restraining Bolt (no Use X)
				put("pondaBlaster", "7_323"); // free on smuggler / 2 on warrior
				put("ponda", "1_190"); // Ponda Baba, smuggler+warrior
				put("dsSpy", "1_177"); // Garindan
				put("dsUndercover", "2_129"); // Undercover (2_129), DS Effect
				put("mountains", "104_4"); // Hoth: Mountains, combat vehicles -1
				put("scout", "3_156"); // Blizzard Scout 1, deploy 3
				put("blizzard2", "3_155"); // Blizzard 2 AT-AT, permanent pilot ability 2
				put("speeder", "8_169"); // Speeder Bike, jump off if lost
				put("swilla", "2_126"); // Swilla Corey, prevent theft on weapon
				put("surprise", "5_156"); // Dark Surprise, retarget interrupt
				put("disruptor", "7_319"); // Disruptor Pistol: 2 / 1 on non-unique warrior
				put("maul", "11_54"); // Darth Maul (Tatooine)
				put("maulSaber", "13_75"); // Maul's Double-Bladed Lightsaber (Reflections III)
			}},
			10,
			10,
			StartingSetup.DefaultLSGroundLocation,
			StartingSetup.DefaultDSGroundLocation,
			StartingSetup.NoLSStartingInterrupts,
			StartingSetup.NoDSStartingInterrupts,
			StartingSetup.NoLSShields,
			StartingSetup.NoDSShields,
			VirtualTableScenario.Open
		);
	}
