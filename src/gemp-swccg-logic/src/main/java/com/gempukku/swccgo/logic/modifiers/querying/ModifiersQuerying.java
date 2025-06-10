package com.gempukku.swccgo.logic.modifiers.querying;

/**
 * This interface defines tons of different ways to retrieve card state in a game-aware manner.  Rather than
 * naively ask a card what its own Ability is, for example, you can use the Ability-related functions in this interface
 * to instead get an answer that takes all currently-active modifiers (both positive and negative), rulings, and
 * edge cases like modifiers-canceling-other-modifiers into account for you.
 * Due to the sheer size, amount, and breadth of these functions, they are further split into smaller interfaces in a
 * conceptual organization.  There is no real rhyme or reason to why these particular divisions exist except to break
 * the field down into manageable chunks.
 * Many of these interfaces themselves reference other parts of the interface ecosystem, so you may find that certain
 * functions are in a particular oddball interface to avoid circular dependencies.
 * In addition to the function definitions themselves, all non-private functions are defined as a default function with
 * its body inside these interfaces rather than on the ModifiersLogic that is the ultimate concrete implementation. In
 * cases where this cannot be done, the functions are defined as normal for an interface to be implemented in
 * ModifiersLogic.
 */
public interface ModifiersQuerying extends BaseQuery, Ability, Armor, Attacks, Attributes, Battle, BattleDestiny, Captives,
		Cards, CardTraits, Defense, Deploy, Destiny, Duels, EpicEvents, Flags, Force, ForceDrains, Forfeit, GameText,
		Hyperspeed, Icons, JediTests, Keywords, Landspeed, Limits, Locations, Maneuver, MovementCosts, MovementRestrictions,
		Piles, Piloting, Podracing, Politics, Power, Presence, Prohibited, Reacts, Sabacc, Ferocity, LocationControl,
		Targeting, Values, Weapons,
		ModifiersState {

}
