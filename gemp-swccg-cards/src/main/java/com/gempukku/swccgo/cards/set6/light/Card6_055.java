package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: I Must Be Allowed To Speak
 */
public class Card6_055 extends AbstractNormalEffect {
    public Card6_055() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "I Must Be Allowed To Speak", Uniqueness.UNIQUE);
        setLore("'Jedi mod spienko eek.'");
        setGameText("Deploy on a Jabba's Palace site. Luke may deploy at this site regardless of presence or location deployment restrictions. When he is deployed here, relocate Effect to Luke, he is immune to attrition while on Tatooine. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Jabbas_Palace_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whileAttachedToSite = new AttachedCondition(self, Filters.site);
        Condition whileAttachedToLuke = new AttachedCondition(self, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.Luke, whileAttachedToSite, Filters.sameSite(self)));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Luke, new AndCondition(whileAttachedToLuke, new OnCondition(self, Title.Tatooine))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, Filters.Luke, Filters.here(self))
                && GameConditions.isAttachedTo(game, self, Filters.site)) {
            PhysicalCard luke = ((PlayCardResult) effectResult).getPlayedCard();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Relocate to Luke");
            action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(luke));
            // Perform result(s)
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, luke));
            return Collections.singletonList(action);
        }
        return null;
    }
}