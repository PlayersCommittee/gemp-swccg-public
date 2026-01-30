package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCardImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public interface Battles extends Decisions, GameProcedures, PileProperties {

	/**
	 * @return True if the Dark Side player has an available action to initiate battle, false otherwise.
	 */
	default boolean DSCanInitiateBattle() { return DSActionAvailable("Initiate battle"); }

    /**
     * @param location The location to check if battle can be initiated at.
     * @return True if the Dark Side player has an available action to initiate battle, false otherwise.
     */
    default boolean DSCanInitiateBattle(PhysicalCardImpl location) { return DSCardActionAvailable(location,"Initiate battle"); }

	/**
	 * @return True if the Light Side player has an available action to initiate battle, false otherwise.
	 */
	default boolean LSCanInitiateBattle() { return LSActionAvailable("Initiate battle"); }

    /**
     * @param location The location to check if battle can be initiated at.
     * @return True if the Light Side player has an available action to initiate battle, false otherwise.
     */
    default boolean LSCanInitiateBattle(PhysicalCardImpl location) { return LSCardActionAvailable(location,"Initiate battle"); }

    /**
	 * Causes the Dark Side player to initiate battle at the given location.
	 * @param location The location to start battle at.
	 * turn to perform actions.
	 */
	default void DSInitiateBattle(PhysicalCardImpl location) {
        assertTrue("Unable to initiate battle at location",DSCanInitiateBattle(location));
        InitiateBattle(DS, location);
    }
	/**
	 * Causes the Light Side player to initiate battle at the given location.
	 * @param location The location to start battle at.
	 */
	default void LSInitiateBattle(PhysicalCardImpl location) {
        assertTrue("Unable to initiate battle at location",LSCanInitiateBattle(location));
        InitiateBattle(LS, location);
    }

	/**
	 * Causes the given player to initiate battle at the given location.
	 * @param player The player who should initiate.
	 * @param location The location to start battle at.
	 */
	default void InitiateBattle(String player, PhysicalCardImpl location) {
		if(player.equals(DS)) DSUseCardAction(location,"Initiate battle");
		else LSUseCardAction(location,"Initiate battle");
		PassForceUseResponses();
		PassBattleStartResponses();
	}


	/**
	 * Right after battle has been initiated, skips to the start of the Power Segment right before the first player is
	 * given the option to draw battle destiny.
	 */
	default void SkipToPowerSegment() {
		PassBattleStartResponses();
		PassWeaponsSegmentActions();
		PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
	}

	/**
	 * When battle destinies are about to be drawn, this can be used to skip past them.  Both players will be instructed
	 * to draw or not draw destinies as provided. Be sure to use PrepareDSDestiny / PrepareLSDestiny if you are expecting
	 * a particular outcome.
	 * @param drawDestiny True if both players should draw destiny, false if they should both pass.
	 */
	default void SkipBattleDestinyDraws(boolean drawDestiny) {
		var currentPlayer = GetCurrentPlayer();
		var offPlayer = GetOpponent();

		PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
		// current player destiny
		if(DecisionAvailable(currentPlayer, "battle destiny?")) {
			if(drawDestiny) {
				PlayerChooseYes(currentPlayer);
			}
			else {
				PlayerChooseNo(currentPlayer);
			}
			PassDestinyDrawResponses();
		}
		PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");

		// opponent destiny
		if(DecisionAvailable(offPlayer, "battle destiny?")) {
			if(drawDestiny) {
				PlayerChooseYes(offPlayer);
			}
			else {
				PlayerChooseNo(offPlayer);
			}
			PassDestinyDrawResponses();
		}
		PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
		PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
	}

	/**
	 * Right after battle has been initiated, skips to the end of the Power Segment after both players have drawn
	 * battle destiny. Both players will be instructed to draw or not draw destinies as provided. Be sure to use
	 * {@link ZoneManipulation#PrepareDSDestiny(int)} / {@link ZoneManipulation#PrepareLSDestiny(int)} if you are
	 * expecting a particular outcome.
	 * This will arrive at the last point where the total power of both sides can be checked in the BattleState; it
	 * will be cleared to 0 as soon as attrition is calculated.
	 * @param drawDestiny True if both players should draw destiny, false if they should both pass.
	 */
	default void SkipToEndOfPowerSegment(boolean drawDestiny) {
		SkipToPowerSegment();
		SkipBattleDestinyDraws(drawDestiny);
	}

	default void SkipToDamageSegment() { SkipToDamageSegment(false); }
	/**
	 * Right after battle has been initiated, skips to the end of the Power Segment after both players have drawn
	 * battle destiny. Both players will be instructed to draw or not draw destinies as provided. Be sure to use
	 * {@link ZoneManipulation#PrepareDSDestiny(int)} / {@link ZoneManipulation#PrepareLSDestiny(int)} if you are
	 * expecting a particular outcome.
	 * Remember that the power of both sides will be set to 0 in the BattleState at this point.  If you meant to check
	 * the power result of one side or the other, use {@link #SkipToEndOfPowerSegment(boolean)} instead.
	 * @param drawDestiny True if both players should draw destiny, false if they should both pass.
	 */
	default void SkipToDamageSegment(boolean drawDestiny) {
		SkipToEndOfPowerSegment(drawDestiny);
		PassResponses("INITIAL_ATTRITION_CALCULATED");
	}

	/**
	 * After a player has chosen to fire a weapon, this function can be used to skip past all optional responses for each
	 * sub-step of that weapon firing and drawing destiny.  Will only skip past a single destiny drawn.
	 */
	default void PassWeaponFireWithDestinyDraw() { PassWeaponFireWithDestinyDraw(1); }

	/**
	 * After a player has chosen to fire a weapon, this function can be used to skip past all optional responses for each
	 * sub-step of that weapon firing and drawing destiny.  Will only skip past the given number of destiny draws.
	 * @param draws How many destiny draws to execute and skip past.
	 */
	default void PassWeaponFireWithDestinyDraw(int draws) {
		// weapon firing
		PassResponses("Fire ");
		for(int i = 0; i < draws; ++i) {
			PassDestinyDrawResponses();
		}
		PassResponses("ABOUT_TO_BE_HIT");
		PassResponses("HIT -");
		PassResponses("FIRED_WEAPON");
	}

	/**
	 * Any time a destiny is drawn, there are multiple sub-steps of responses.  This will skip past all of them.
	 */
	default void PassDestinyDrawResponses() {
		PassResponses("COST_TO_DRAW_DESTINY_CARD");
		PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
		PassResponses("DESTINY_DRAWN");
		PassResponses("COMPLETE_DESTINY_DRAW");
		PassResponses("DRAWING_DESTINY_COMPLETE");
	}

	/**
	 * After a battle has been initiated but before it has actually begun there is a brief window to respond to it and
	 * cancel it.  If you are just trying to get to the action, this can be used right after battle is initiated to begin.
	 */
	default void PassBattleStartResponses() { PassResponses("BATTLE_INITIATED"); }

	/**
	 * @return True if the game is currently awaiting a weapons segment action decision from the Dark Side player, false otherwise.
	 */
	default boolean AwaitingDSWeaponsSegmentActions() { return DSDecisionAvailable("Choose weapons segment action to play or Pass"); }
	/**
	 * @return True if the game is currently awaiting a weapons segment action decision from the Light Side player, false otherwise.
	 */
	default boolean AwaitingLSWeaponsSegmentActions() { return LSDecisionAvailable("Choose weapons segment action to play or Pass"); }

	/**
	 * @return True if the game is currently awaiting a battle segment action decision from the Dark Side player, false otherwise.
	 */
	default boolean AwaitingDSPowerSegmentActions() { return DSDecisionAvailable("Choose power segment action to play or Pass"); }
	/**
	 * @return True if the game is currently awaiting a battle segment action decision from the Light Side player, false otherwise.
	 */
	default boolean AwaitingLSPowerSegmentActions() { return LSDecisionAvailable("Choose power segment action to play or Pass"); }

	/**
	 * @return True if the game is currently awaiting a damage segment action decision from the Dark Side player, false otherwise.
	 */
	default boolean AwaitingDSDamageSegmentActions() { return DSDecisionAvailable("Choose damage segment action to play or Pass"); }
	/**
	 * @return True if the game is currently awaiting a damage segment action decision from the Light Side player, false otherwise.
	 */
	default boolean AwaitingLSDamageSegmentActions() { return LSDecisionAvailable("Choose damage segment action to play or Pass"); }

	/**
	 * Causes both players to pass weapons segment actions.
	 */
	default void PassWeaponsSegmentActions() { PassResponses("Choose weapons segment action to play or Pass"); }
	/**
	 * Causes both players to pass power segment actions.
	 */
	default void PassPowerSegmentActions() { PassResponses("Choose power segment action to play or Pass"); }
	/**
	 * Causes both players to pass damage segment actions.
	 */
	default void PassDamageSegmentActions() { PassResponses("Choose damage segment action to play or Pass"); }


	/**
	 * @return True if the Dark Side player is currently deciding on what to pay to satisfy unpaid attrition > 0.
	 */
	default boolean AwaitingDSAttritionPayment() {
		return (DecisionAvailable(DS, "Choose Force to lose or a card from battle to forfeit")
					|| DecisionAvailable(DS, "Choose a card from battle to forfeit"))
				&& IsReachedDamageSegment() && GetUnpaidDSAttrition() > 0;
	}
	/**
	 * @return True if the Light Side player is currently deciding on what to pay to satisfy unpaid attrition > 0.
	 */
	default boolean AwaitingLSAttritionPayment() {
		return (DecisionAvailable(LS, "Choose Force to lose or a card from battle to forfeit")
				|| DecisionAvailable(LS, "Choose a card from battle to forfeit"))
				&& IsReachedDamageSegment() && GetUnpaidLSAttrition() > 0;
	}
	/**
	 * @return How much attrition the Dark Side player needs to satisfy.
	 */
	default int GetUnpaidDSAttrition() { return (int) gameState().getBattleState().getAttritionRemaining(game(), DS); }
	/**
	 * @return How much attrition the Light Side player needs to satisfy.
	 */
	default int GetUnpaidLSAttrition() { return (int) gameState().getBattleState().getAttritionRemaining(game(), LS); }


	/**
	 * Pays for 1 or more Force worth of Dark Side attrition by sacrificing the provided card in play, then passes
	 * the responses for it leaving the table.
	 * @param card The DS card in play to sacrifice for attrition.
	 */
	default void DSPayAttritionFromCardInPlay(PhysicalCardImpl card) {
        assertTrue("Not waiting for Attrition payment",AwaitingDSAttritionPayment());
		DSChooseCard(card);
		PassCardLeavingTable();
	}

	/**
	 * Pays for 1 or more Force worth of Light Side attrition by sacrificing the provided card in play, then passes
	 * the responses for it leaving the table.
	 * @param card The LS card in play to sacrifice for attrition.
	 */
	default void LSPayAttritionFromCardInPlay(PhysicalCardImpl card) {
        assertTrue("Not waiting for Attrition payment",AwaitingLSAttritionPayment());
		LSChooseCard(card);
		PassCardLeavingTable();
	}

	/**
	 * @return True if the Dark Side player is currently deciding on what to pay to satisfy unpaid battle damage > 0.
	 */
	default boolean AwaitingDSBattleDamagePayment() {
		return DecisionAvailable(DS, "Choose Force to lose or a card from battle to forfeit")
				&& IsReachedDamageSegment() && GetUnpaidDSBattleDamage() > 0;
	}
	/**
	 * @return True if the Light Side player is currently deciding on what to pay to satisfy unpaid battle damage > 0.
	 */
	default boolean AwaitingLSBattleDamagePayment() {
		return DecisionAvailable(LS, "Choose Force to lose or a card from battle to forfeit")
				&& IsReachedDamageSegment() && GetUnpaidLSBattleDamage() > 0;
	}
	/**
	 * @return How much battle damage the Dark Side player needs to satisfy.
	 */
	default int GetUnpaidDSBattleDamage() { return (int) gameState().getBattleState().getBattleDamageRemaining(game(), DS); }
	/**
	 * @return How much battle damage the Light Side player needs to satisfy.
	 */
	default int GetUnpaidLSBattleDamage() { return (int) gameState().getBattleState().getBattleDamageRemaining(game(), LS); }

	/**
	 * Pays for the remaining Dark Side battle damage using cards on the top of the DS Reserve deck.
	 */
	default void DSPayRemainingBattleDamageFromReserveDeck() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetDSReserveDeckCount() >= GetUnpaidDSBattleDamage());
		DSPayBattleDamageFromReserveDeck(GetUnpaidDSBattleDamage());
	}
	/**
	 * Pays for the given amount of Force worth of Dark Side battle damage using cards on the top of the DS Reserve deck.
	 */
	default void DSPayBattleDamageFromReserveDeck(int amount) {
		for(int i = 0; i < amount; ++i) {
			DSPayBattleDamageFromReserveDeck();
		}
	}
	/**
	 * Pays for 1 Force worth of Dark Side battle damage using the card on the top of the DS Reserve deck.
	 */
	default void DSPayBattleDamageFromReserveDeck() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetDSReserveDeckCount() >= 1);
        DSChooseCard(GetTopOfDSReserveDeck());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 Force worth of Dark Side battle damage using the card on the top of the DS Force Pile.
	 */
	default void DSPayBattleDamageFromForcePile() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        assertTrue("Insufficient cards in Force Pile to lose from",GetDSForcePileCount() >= 1);
        DSChooseCard(GetTopOfDSForcePile());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 Force worth of Dark Side battle damage using the card on the top of the DS Used Pile.
	 */
	default void DSPayBattleDamageFromUsedPile() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        assertTrue("Insufficient cards in Used Pile to lose from",GetDSUsedPileCount() >= 1);
        DSChooseCard(GetTopOfDSUsedPile());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 or more Force worth of Dark Side battle damage by sacrificing the provided card in play, applying
	 * its forfeit value against the battle damage.
	 * @param card The DS card in play to sacrifice for battle damage.
	 */
	default void DSPayBattleDamageFromCardInPlay(PhysicalCardImpl card) {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        DSChooseCard(card);
		PassAllResponses();
	}
	/**
	 * Pays for 1 or more Force worth of Dark Side battle damage by sacrificing the provided card in hand.
	 * @param card The DS card in hand to sacrifice for battle damage.
	 */
	default void DSPayBattleDamageFromCardInHand(PhysicalCardImpl card) {
        assertTrue("Not waiting for Battle Damage payment",AwaitingDSBattleDamagePayment());
        assertEquals("Card to lose was not in hand",Zone.HAND,card.getZone());
        DSChooseCard(card);
		PassAllResponses();
	}

	/**
	 * Pays for the remaining Light Side battle damage using cards on the top of the DS Reserve deck.
	 */
	default void LSPayRemainingBattleDamageFromReserveDeck() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetLSReserveDeckCount() >= GetUnpaidLSBattleDamage());
        LSPayBattleDamageFromReserveDeck(GetUnpaidLSBattleDamage());
	}
	/**
	 * Pays for the given amount of Force worth of Light Side battle damage using cards on the top of the DS Reserve deck.
	 */
	default void LSPayBattleDamageFromReserveDeck(int amount) {
		for(int i = 0; i < amount; ++i) {
			LSPayBattleDamageFromReserveDeck();
		}
	}
	/**
	 * Pays for 1 Force worth of Light Side battle damage using the card on the top of the LS Reserve deck.
	 */
	default void LSPayBattleDamageFromReserveDeck() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetLSReserveDeckCount() >= 1);
		LSChooseCard(GetTopOfLSReserveDeck());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 Force worth of Light Side battle damage using the card on the top of the LS Force Pile.
	 */
	default void LSPayBattleDamageFromForcePile() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
        assertTrue("Insufficient cards in Force Pile to lose from",GetLSForcePileCount() >= 1);
		LSChooseCard(GetTopOfLSForcePile());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 Force worth of Light Side battle damage using the card on the top of the LS Force Pile.
	 */
	default void LSPayBattleDamageFromUsedPile() {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
        assertTrue("Insufficient cards in Used Pile to lose from",GetLSUsedPileCount() >= 1);
		LSChooseCard(GetTopOfLSUsedPile());
		PassCardLeavingTable();
	}
	/**
	 * Pays for 1 or more Force worth of Light Side battle damage by sacrificing the provided card in play.
	 * @param card The LS card in play to sacrifice for battle damage.
	 */
	default void LSPayBattleDamageFromCardInPlay(PhysicalCardImpl card) {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
		LSChooseCard(card);
		PassAllResponses();
	}

	/**
	 * Pays for 1 or more Force worth of Light Side battle damage by sacrificing the provided card in hand.
	 * @param card The LS card in hand to sacrifice for battle damage.
	 */
	default void LSPayBattleDamageFromCardInHand(PhysicalCardImpl card) {
        assertTrue("Not waiting for Battle Damage payment",AwaitingLSBattleDamagePayment());
        assertEquals("Card to lose was not in hand",Zone.HAND,card.getZone());
        LSChooseCard(card);
		PassResponses("FORFEITED_TO_LOST_PILE_FROM_HAND");
		PassCardLeavingTable();
	}


	/**
	 * @return The total battle destiny drawn by the Dark Side player during the current battle.
	 */
	default int GetDSTotalDestiny() { return (int) gameState().getBattleState().getTotalBattleDestiny(game(), DS); }
	/**
	 * @return The total battle destiny drawn by the Light Side player during the current battle.
	 */
	default int GetLSTotalDestiny() { return (int) gameState().getBattleState().getTotalBattleDestiny(game(), LS); }

	/**
	 * @return The total power of the Dark Side during the given battle.  Remember that this will be set to 0 once
	 * attrition/battle damage are calculated, so you must check it before that point.
	 */
	default int GetDSTotalPower() { return (int) gameState().getBattleState().getTotalPower(game(), DS); }
	/**
	 * @return The total power of the Light Side during the given battle.  Remember that this will be set to 0 once
	 * attrition/battle damage are calculated, so you must check it before that point.
	 */
	default int GetLSTotalPower() { return (int) gameState().getBattleState().getTotalPower(game(), LS); }

	/**
	 * @return True if the Dark Side won the current battle, otherwise false.
	 */
	default boolean DSWonBattle() { return gameState().getBattleState().isWinner(DS); }
	/**
	 * @return True if the Light Side won the current battle, otherwise false.
	 */
	default boolean LSWonBattle() { return gameState().getBattleState().isWinner(LS); }

	/**
	 * @return True if there is a current battle that has been initiated and has begun, otherwise false.
	 */
	default boolean IsBattleStarted() { return gameState().getBattleState().isBattleStarted(); }
	/**
	 * @return True if there is a current battle that has been initiated and has been canceled, otherwise false.
	 */
	default boolean IsBattleCanceled() { return gameState().getBattleState().isCanceled(); }
	/**
	 * @return True if there is a current battle that has been initiated and progressed to the power segment, otherwise false.
	 */
	default boolean IsReachedPowerSegment() { return gameState().getBattleState().isReachedPowerSegment(); }
	/**
	 * @return True if there is a current battle that has been initiated and has calculated battle damage, otherwise false.
	 */
	default boolean IsAttritionCalculated() { return gameState().getBattleState().isBaseAttritionCalculated(); }
	/**
	 * @return True if there is a current battle that has been initiated and has progressed to paying battle damage, otherwise false.
	 */
	default boolean IsReachedDamageSegment() { return gameState().getBattleState().isReachedDamageSegment(); }

    /**
     * @return True if the Dark Side player is currently deciding on what to pay to satisfy unpaid Force loss.
     */
    default boolean AwaitingDSForceLossPayment() {
        return DecisionAvailable(DS, "Choose Force to lose");
    }

    /**
     * @return True if the Light Side player is currently deciding on what to pay to satisfy unpaid Force loss.
     */
    default boolean AwaitingLSForceLossPayment() {
        return DecisionAvailable(LS, "Choose Force to lose");
    }

    /**
     * Pays for 1 Force worth of Dark Side Force loss using the card on the top of the DS Reserve deck.
     */
    default void DSPayForceLossFromReserveDeck() {
        assertTrue("Not waiting for Force Loss",AwaitingDSForceLossPayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetDSReserveDeckCount() >= 1);
        DSChooseCard(GetTopOfDSReserveDeck());
        PassCardLeavingTable();
    }

    /**
     * Pays for 1 Force worth of Light Side Force loss using the card on the top of the LS Reserve deck.
     */
    default void LSPayForceLossFromReserveDeck() {
        assertTrue("Not waiting for Force Loss",AwaitingLSForceLossPayment());
        assertTrue("Insufficient cards in Reserve Deck to lose from",GetLSReserveDeckCount() >= 1);
        LSChooseCard(GetTopOfLSReserveDeck());
        PassCardLeavingTable();
    }

    /**
     * Pays for remaining Dark Side Force loss using cards from the top of the DS Reserve deck.
     */
    default void DSPayRemainingForceLossFromReserveDeck() {
        assertTrue("Not waiting for Force Loss",AwaitingDSForceLossPayment());
        for(int i = 0; i < 60; ++i) { //replace 60 with remaining force loss count, if we can access it?
            if(AwaitingDSForceLossPayment()) {
                assertTrue("Insufficient cards in Reserve Deck to lose from",GetDSReserveDeckCount() >= 1);
                DSChooseCard(GetTopOfDSReserveDeck());
                PassCardLeavingTable();
            }

        }
    }

    /**
     * Pays for remaining Light Side Force loss using cards from the top of the LS Reserve deck.
     */
    default void LSPayRemainingForceLossFromReserveDeck() {
        assertTrue("Not waiting for Force Loss",AwaitingLSForceLossPayment());
        for(int i = 0; i < 60; ++i) { //replace 60 with remaining force loss count, if we can access it?
            if(AwaitingLSForceLossPayment()) {
                assertTrue("Insufficient cards in Reserve Deck to lose from",GetLSReserveDeckCount() >= 1);
                LSChooseCard(GetTopOfLSReserveDeck());
                PassCardLeavingTable();
            }

        }
    }

    /**
     * Pays for 1 Force worth of Dark Side Force loss using the card on the top of the DS Used Pile.
     */
    default void DSPayForceLossFromUsedPile() {
        assertTrue("Not waiting for Force Loss",AwaitingDSForceLossPayment());
        assertTrue("Insufficient cards in Used Pile to lose from",GetDSUsedPileCount() >= 1);
        DSChooseCard(GetTopOfDSUsedPile());
        PassCardLeavingTable();
    }

    /**
     * Pays for 1 Force worth of Light Side Force loss using the card on the top of the LS Used Pile.
     */
    default void LSPayForceLossFromUsedPile() {
        assertTrue("Not waiting for Force Loss",AwaitingLSForceLossPayment());
        assertTrue("Insufficient cards in Used Pile to lose from",GetLSUsedPileCount() >= 1);
        LSChooseCard(GetTopOfLSUsedPile());
        PassCardLeavingTable();
    }

    /**
     * Pays for 1 Force worth of Dark Side Force loss using the card on the top of the DS Force Pile.
     */
    default void DSPayForceLossFromForcePile() {
        assertTrue("Not waiting for Force Loss",AwaitingDSForceLossPayment());
        assertTrue("Insufficient cards in Force Pile to lose from",GetDSForcePileCount() >= 1);
        DSChooseCard(GetTopOfDSForcePile());
        PassCardLeavingTable();
    }

    /**
     * Pays for 1 Force worth of Light Side Force loss using the card on the top of the LS Force Pile.
     */
    default void LSPayForceLossFromForcePile() {
        assertTrue("Not waiting for Force Loss",AwaitingLSForceLossPayment());
        assertTrue("Insufficient cards in Force Pile to lose from",GetLSForcePileCount() >= 1);
        LSChooseCard(GetTopOfLSForcePile());
        PassCardLeavingTable();
    }

}
