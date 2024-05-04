package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Alien
 * Title: Zuckuss With Snare Rifle
 */
public class Card223_003 extends AbstractAlien {
    public Card223_003() {
        super(Side.DARK, 1, 4, 2, 4, 4, "Zuckuss With Snare Rifle", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Gand bounty hunter and scout.");
        setGameText("[Pilot] 2. Once per game, may peek at top card of any Reserve Deck. " +
                "Permanent weapon is •Zuckuss' Snare Rifle (may target a character for free; " +
                "draw destiny; target excluded from battle if destiny +1 > target's power).");
        addPersona(Persona.ZUCKUSS);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.PREMIUM, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
        setSpecies(Species.GAND);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.ZUCKUSS_WITH_SNARE_RIFLE__PEEK_AT_TOP_CARD_OF_RESERVE_DECK;

        // Check condition(s)
        if ((GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of any Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Peek at top card of " + cardPileOwner + "'s Reserve Deck");
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, cardPileOwner));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    // Define "Zuckuss' Snare Rifle" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Persona.ZUCKUSS_SNARE_RIFLE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.hasPowerDefined), TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFirePermanentWeaponZuckussSnareRifleAction();
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.RIFLE);
        return permanentWeapon;
    }
}
