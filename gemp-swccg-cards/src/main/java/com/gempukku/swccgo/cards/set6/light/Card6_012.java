package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NotUniqueModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Elom
 */
public class Card6_012 extends AbstractAlien {
    public Card6_012() {
        super(Side.LIGHT, 4, 3, 1, 2, 3, "Elom", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Many Elom commit sabotage and theft against the Empire to avenge the invasion of their homeworld. Experts in adapting stolen equipment for use by the Rebellion.");
        setGameText("Power +3 at same site as an Imperial. For remainder of game, Plastoid Armor is an Effect, is not unique, is immune to Alter while on table, and it deploys only on a Rebel or alien at same mobile site as Elom (character is now 'disguised').");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.ELOM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Imperial), 3));
        return modifiers;
    }

    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ELOM__MODIFY_PLASTOID_ARMOR_FOR_REMAINDER_OF_GAME;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText(null);
            action.skipInitialMessageAndAnimation();
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new ChangeCardSubtypeModifier(self, Filters.Plastoid_Armor, CardSubtype._), null));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new NotUniqueModifier(self, Filters.Plastoid_Armor), null));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new ImmuneToTitleModifier(self, Filters.and(Filters.Plastoid_Armor, Filters.onTable), Title.Alter), null));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self, Filters.Plastoid_Armor, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT), null));

            return Collections.singletonList(action);
        }
        return null;
    }
}
