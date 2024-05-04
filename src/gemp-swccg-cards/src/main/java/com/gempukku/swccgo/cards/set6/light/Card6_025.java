package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TransfersFreeToTargetModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Laudica
 */
public class Card6_025 extends AbstractAlien {
    public Card6_025() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Laudica", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Corellian gun-runner. Skilled markswoman. Friends with Brindy Truchong and the Tonnika sisters. Romantically involved with Rayc Ryjerd, against her better judgment.");
        setGameText("When in a battle, you may 'react' by deploying any one non-unique blaster (for free) on Laudica from Reserve Deck; reshuffle. When present with your non-unique Corellian at a site, allows your character weapons to transfer for free there.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.CORELLIAN);
        addPersona(Persona.LAUDICA);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.LAUDICA__DOWNLOAD_NON_UNIQUE_BLASTER_AS_REACT;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy blaster as 'react' from Reserve Deck");
            action.setActionMsg("Deploy a non-unique blaster as 'react' on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.blaster), Filters.Laudica, true, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TransfersFreeToTargetModifier(self, Filters.and(Filters.your(self), Filters.character_weapon, Filters.here(self)),
                new AndCondition(new AtCondition(self, Filters.site), new PresentWithCondition(self, Filters.and(Filters.your(self),
                        Filters.non_unique, Filters.Corellian))), Filters.here(self)));
        return modifiers;
    }
}
