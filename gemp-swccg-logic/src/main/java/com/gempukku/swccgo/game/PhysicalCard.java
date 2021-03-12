package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// This interface represents an individual physical card in the game.
//
// Methods provided by this interface allow the state information
// for the physical card to be set or retrieved.
//
// The "blueprint" of the physical card identifies the
// Star Wars CCG card that this physical card is a copy of.
//
public interface PhysicalCard extends Filterable, Snapshotable<PhysicalCard> {

    /**
     * Gets the ID for a card that does not change over the course of the game.
     * @return the permanent card ID
     */
    int getPermanentCardId();

    /**
     * Sets the ID for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: If a character is persona replaced, or a location is converted, the new card gets the same card ID of the
     * replaced/converted card and the replaced/converted gets assigned a new card ID.
     * @param cardId the card ID
     */
    void setCardId(int cardId);

    /**
     * Gets the ID for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: If a character is persona replaced, or a location is converted, the new card gets the same card ID of the
     * replaced/converted card and the replaced/converted gets assigned a new card ID.
     * @return the card ID
     */
    int getCardId();

    /**
     * Sets the additional IDs for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: This is used when a squadron replaces starfighters so the squadron keeps all the replaced starfighters card IDs.
     * @param cardIds the card IDs
     */
    void setAdditionalCardIds(List<Integer> cardIds);

    /**
     * Gets the additional ID for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: This is used when a squadron replaces starfighters so the squadron keeps all the replaced starfighters card IDs.
     * @return the card IDs
     */
    List<Integer> getAdditionalCardIds();

    PlayCardOptionId getPlayCardOptionId();
    void setPlayCardOptionId(PlayCardOptionId optionId);

    PhysicalCard clone() throws CloneNotSupportedException;

    String getTitle();
    List<String> getTitles();

    void setZone(Zone zone);
    Zone getZone();

    void setZoneOwner(String playerId);
    String getZoneOwner();

    void setLocationZoneIndex(int index);
    int getLocationZoneIndex();

    boolean isDoubleSided();
    String getBlueprintId(boolean alwaysShowCardFront);
    String getBlueprintId(GameState gameState, boolean alwaysShowCardFront);
    SwccgCardBlueprint getBlueprint();
    SwccgCardBlueprint getOtherSideBlueprint();
    String getTestingText(GameState gameState, boolean alwaysShowCardFront, boolean showOtherSide);

    String getOwner();
    void setOwner(String playerId);

    boolean isStolen();

    void startAffectingGame(SwccgGame game);
    void stopAffectingGame();

    /**
     * Attaches the card to the specified card in a specified way.
     * @param physicalCard the card to attach this card to
     * @param asPilot true if attaching in pilot capacity slot, otherwise false
     * @param asPassenger true if attaching as passenger capacity slot, otherwise false
     * @param asVehicle true if attaching in cargo bay vehicle capacity slot, otherwise false
     * @param asStarfighterOrTIE true if attaching in cargo bay starfighter capacity slot, otherwise false
     * @param asCapitalStarship true if attaching in cargo bay capital starship capacity slot, otherwise false
     */
    void attachTo(PhysicalCard physicalCard, boolean asPilot, boolean asPassenger, boolean asVehicle, boolean asStarfighterOrTIE, boolean asCapitalStarship);

    /**
     * Gets the card that this card is attached to.
     * @return the card this is attached to, or null
     */
    PhysicalCard getAttachedTo();

    /**
     * Determines if this card in the pilot capacity slot of the card it is attached to.
     * @return true or false
     */
    boolean isPilotOf();

    /**
     * Determines if this card in the passenger capacity slot of the card it is attached to.
     * @return true or false
     */
    boolean isPassengerOf();

    /**
     * Determines if this card in cargo bay vehicle capacity slot of the card it is attached to.
     * @return true or false
     */
    boolean isInCargoHoldAsVehicle();

    /**
     * Determines if this card in cargo bay starfighter or TIE capacity slot of the card it is attached to.
     * @return true or false
     */
    boolean isInCargoHoldAsStarfighterOrTIE();

    /**
     * Determines if this card in cargo bay capital starship capacity slot of the card it is attached to.
     * @return true or false
     */
    boolean isInCargoHoldAsCapitalStarship();

    List<PhysicalCard> getCardsAttached();
    List<PhysicalCard> getCardsStacked();
    List<PhysicalCard> getCardsAtLocation();

    /**
     * Gets the card that is in the AT_LOCATION zone if this card is ATTACHED to another card.  This is used when adding
     * cards to the "in a duel" or "attacking/defending" group in the user interface since if the dueling/attacking/defending
     * character is attached to another card need to add the card is is attached to to the group.
     * @return card at location
     */
    PhysicalCard getCardAttachedToAtLocation();

    void stackOn(PhysicalCard physicalCard, boolean asInactive, boolean fromJediTest5);
    PhysicalCard getStackedOn();
    boolean isNotShownOnUserInterface();
    boolean isStackedAsInactive();
    boolean isStackedAsViaJediTest5();

    void atLocation(PhysicalCard physicalCard);
    PhysicalCard getAtLocation();

    void shipdockedWith(PhysicalCard physicalCard);
    PhysicalCard getShipdockedWith();

    void startingLocation(boolean startingLocation, boolean isBattleground);
    boolean isStartingLocation();
    boolean isStartingLocationBattleground();

    void setCrossedOver(boolean crossedOver);
    boolean isCrossedOver();

    void setInverted(boolean inverted);
    boolean isInverted();

    void setBlownAway(boolean faceDown);
    boolean isBlownAway();

    void setCollapsed(boolean collapsed);
    boolean isCollapsed();

    void setSideways(boolean sideways);
    boolean isSideways();

    CardState getPreviousCardState();

    void setHit(boolean hit);
    boolean isHit();
    boolean wasPreviouslyHit();
    boolean wasPreviouslyArmedWithLightsaber();

    float getLatestInPlayForfeitValue();
    void setLatestInPlayForfeitValue(float value);

    void setDisarmed(boolean disarmed);
    boolean isDisarmed();

    void setDamaged(boolean damaged);
    boolean isDamaged();

    void setCrashed(boolean crashed);
    boolean isCrashed();

    void setGameTextCanceled(boolean canceled);
    boolean isGameTextCanceled();
    void setLocationGameTextCanceledForPlayer(boolean canceled, String playerId);
    boolean isLocationGameTextCanceledForPlayer(String playerId);
    boolean wasPreviouslyCanceledGameText();
    void setLocationGameTextExpandedToSideFromCardId(Side toSide, Integer fromCardId);
    Integer getLocationGameTextExpandedToSideFromCardId(Side toSide);
    void setLocationGameTextExpandedToSideFromSide(Side toSide, Side fromSide);
    Side getLocationGameTextExpandedToSideFromSide(Side toSide);

    void setSuspended(boolean suspended);
    boolean isSuspended();

    void setBinaryOff(boolean binaryOff);
    boolean isBinaryOff();

    void setMouthClosed(boolean mouthClosed);
    boolean isMouthClosed();

    void setDestinyValueToUse(float value);
    Float getDestinyValueToUse();

    void setWhileInPlayData(WhileInPlayData data);
    WhileInPlayData getWhileInPlayData();

    void setLeavingTable(boolean leavingTable);
    boolean isLeavingTable();

    /**
     * Sets the Jedi Test status. This is only relevant if the card is a Jedi Test in play.
     * @param status the Jedi Test status
     */
    void setJediTestStatus(JediTestStatus status);

    /**
     * Gets the Jedi Test status. This is only relevant if the card is a Jedi Test in play.
     * @return the Jedi Test status
     */
    JediTestStatus getJediTestStatus();

    /**
     * Sets the Utinni Effect status. This is only relevant if the card is an Utinni Effect in play.
     * @param status the Utinni Effect status
     */
    void setUtinniEffectStatus(UtinniEffectStatus status);

    /**
     * Gets the Utinni Effect status. This is only relevant if the card is an Utinni Effect in play.
     * @return the Utinni Effect status
     */
    UtinniEffectStatus getUtinniEffectStatus();

    /**
     * Sets a card as explicitly targeted by this card.
     * @param targetId the target id
     * @param targetGroupId the target group id of the action that performed targeting, or null
     * @param targetCard the card to target
     * @param validTargetFilter the filter for cards that would be valid to target
     */
    void setTargetedCard(TargetId targetId, Integer targetGroupId, PhysicalCard targetCard, Filter validTargetFilter);

    /**
     * Gets the target group id of the action that was used to set the specified target id.
     * @param targetId the target id
     * @return the target group id, or null
     */
    Integer getTargetGroupId(TargetId targetId);

    /**
     * Gets the card targeted by this card with the specified target id.
     * @param gameState the game state
     * @param targetId the target id
     * @return the targeted card, or null
     */
    PhysicalCard getTargetedCard(GameState gameState, TargetId targetId);

    /**
     * Gets the cards targeted by this card by the currently set target ids.
     * @param gameState the game state
     * @return the map of target ids to targeted cards
     */
    Map<TargetId, PhysicalCard> getTargetedCards(GameState gameState);

    /**
     * Gets the filter for cards that would be valid to target by this card with the specified target id.
     * @param targetId the target id
     * @return the targeted card
     */
    Filter getValidTargetedFilter(TargetId targetId);

    /**
     * Updates the filter for cards that would be valid to target by this card with the specified target id.
     * @param targetId the target id
     * @param validTargetFilter the filter for cards that would be valid to target
     */
    void updateValidTargetedFilter(TargetId targetId, Filter validTargetFilter);

    /**
     * Clears the collection of cards explicitly targeted by this card.
     */
    void clearTargetedCards();


    void updateRememberedInPlayCardInfo(SwccgGame game);
    Collection<PhysicalCard> getCardsPreviouslyAttached();

    void setInserted(boolean inserted);
    boolean isInserted();
    void setInsertCardRevealed(boolean revealed);
    boolean isInsertCardRevealed();

    void setConcealed(boolean concealed);
    boolean isConcealed();

    void setUndercover(boolean undercover);
    boolean isUndercover();

    void setMissing(boolean missing);
    boolean isMissing();

    void setCapturedStarship(boolean capturedStarship);
    boolean isCapturedStarship();

    void setCaptive(boolean captive);
    boolean isCaptive();

    void setImprisoned(boolean imprisoned);
    boolean isImprisoned();

    void setFrozen(boolean frozen);
    boolean isFrozen();

    void setFlipped(boolean flipped);
    boolean isFlipped();

    void setObjectiveDeploymentComplete(boolean complete);
    boolean isObjectiveDeploymentComplete();

    void setProbeCard(boolean probeCard);
    boolean isProbeCard();

    void setHatredCard(boolean hatredCard);
    boolean isHatredCard();

    void setEnslavedCard(boolean enslavedCard);
    boolean isEnslavedCard();

    boolean isCoaxiumCard();

    void setCoaxiumCard(boolean coaxiumCard);

    void setLiberationCard(boolean liberationCard);
    boolean isLiberationCard();

    void setBluffCard(boolean bluffCard);
    boolean isBluffCard();

    void setCombatCard(boolean combatCard);
    boolean isCombatCard();

    void setSpaceSlugBelly(boolean spaceSlugBelly);
    boolean isSpaceSlugBelly();

    void setSoupEaten(float abilityWhenEaten);
    Float getSoupEaten();

    void setBeheaded();
    boolean isBeheaded();

    void setMakingBombingRun(boolean makingBombingRun);
    boolean isMakingBombingRun();

    void setDejarikHologramAtHolosite(boolean dejarik);
    boolean isDejarikHologramAtHolosite();

    void setRotated(boolean isRotated);
    boolean isRotated();

    void setPartOfSystem(String system);
    String getPartOfSystem();

    void setParsec(int parsec);
    int getParsec();

    void setSabaccValue(float value);
    float getSabaccValue();
    void setSabaccCardCloned(PhysicalCard card);
    PhysicalCard getSabaccCardCloned();

    String getRaceDestinyForPlayer();
    void setRaceDestinyForPlayer(String playerId);

    void setSystemOrbited(String system);
    String getSystemOrbited();

    void setRelatedStarshipOrVehicle(PhysicalCard card);
    PhysicalCard getRelatedStarshipOrVehicle();

    void setImmunityToAttritionLessThan(float immunity);
    float getImmunityToAttritionLessThan();

    void setImmunityToAttritionOfExactly(float immunity);
    float getImmunityToAttritionOfExactly();

    void setMovementDirection(MovementDirection direction);
    MovementDirection getMovementDirection();

    void clearForRemainderOfGameData();
    void setForRemainderOfGameData(Integer cardId, ForRemainderOfGameData data);
    Map<Integer, ForRemainderOfGameData> getForRemainderOfGameData();
}
