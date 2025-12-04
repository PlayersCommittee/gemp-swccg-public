package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LeagueData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.league.NewSoloDraftLeagueData;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SoloDraftRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private CollectionsManager _collectionsManager;
    private SoloDraftDefinitions _soloDraftDefinitions;
    private LeagueService _leagueService;
    private SwccgCardBlueprintLibrary _library;

    public SoloDraftRequestHandler(Map<Type, Object> context) {
        super(context);
        _leagueService = extractObject(context, LeagueService.class);
        _soloDraftDefinitions = extractObject(context, SoloDraftDefinitions.class);
        _collectionsManager = extractObject(context, CollectionsManager.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            makePick(request, uri.substring(1), responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
            getAvailablePicks(request, uri.substring(1), responseWriter);
        } else {
            throw new HttpProcessingException(404);
        }
    }

    private void getAvailablePicks(HttpRequest request, String leagueType, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        League league = findLeagueByType(leagueType);

        if (league == null)
            throw new HttpProcessingException(404);

        LeagueData leagueData = league.getLeagueData(_soloDraftDefinitions);
        int leagueStart = leagueData.getSeries().get(0).getStart();

        if (!leagueData.isSoloDraftLeague() || DateUtils.getCurrentDate() < leagueStart)
            throw new HttpProcessingException(404);

        NewSoloDraftLeagueData soloDraftLeagueData = (NewSoloDraftLeagueData) leagueData;
        CollectionType collectionType = soloDraftLeagueData.getCollectionType();

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CardCollection collection = _collectionsManager.getPlayerCollection(resourceOwner, collectionType.getCode());

        Iterable<SoloDraft.DraftChoice> availableChoices;
        soloDraftLeagueData.repairExtraInformation(collection, resourceOwner);

        boolean finished = (Boolean) collection.getExtraInformation().get("finished");
        int stage = ((Number) collection.getExtraInformation().get("stage")).intValue();
        int stages = ((Number) collection.getExtraInformation().get("stageCount")).intValue();
        if (!finished) {
            long playerSeed = ((Number) collection.getExtraInformation().get("seed")).longValue();

            SoloDraft soloDraft = soloDraftLeagueData.getSoloDraft();
            availableChoices = soloDraft.getAvailableChoices(playerSeed, stage, collection, null);
        } else {
            availableChoices = Collections.emptyList();
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element availablePicksElem = doc.createElement("availablePicks");

        Element draftStateElem = doc.createElement("state");
        draftStateElem.setAttribute("stage",String.valueOf(stage));
        draftStateElem.setAttribute("stages",String.valueOf(stages));
        availablePicksElem.appendChild(draftStateElem);

        doc.appendChild(availablePicksElem);

        appendAvailablePics(doc, availablePicksElem, availableChoices);

        responseWriter.writeXmlResponse(doc);
    }

    private League findLeagueByType(String leagueType) {
        for (League activeLeague : _leagueService.getActiveLeagues()) {
            if (activeLeague.getType().equals(leagueType))
                return activeLeague;
        }
        return null;
    }

    private void makePick(HttpRequest request, String leagueType, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String selectedChoiceId = getFormParameterSafely(postDecoder, "choiceId");

        League league = findLeagueByType(leagueType);

        if (league == null)
            throw new HttpProcessingException(404);

        LeagueData leagueData = league.getLeagueData(_soloDraftDefinitions);
        int leagueStart = leagueData.getSeries().get(0).getStart();

        if (!leagueData.isSoloDraftLeague() || DateUtils.getCurrentDate() < leagueStart)
            throw new HttpProcessingException(404);

        NewSoloDraftLeagueData soloDraftLeagueData = (NewSoloDraftLeagueData) leagueData;
        CollectionType collectionType = soloDraftLeagueData.getCollectionType();

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CardCollection collection = _collectionsManager.getPlayerCollection(resourceOwner, collectionType.getCode());
        soloDraftLeagueData.repairExtraInformation(collection, resourceOwner);
        boolean finished = (Boolean) collection.getExtraInformation().get("finished");
        if (finished)
            throw new HttpProcessingException(404);

        int stage = ((Number) collection.getExtraInformation().get("stage")).intValue();
        int stages = ((Number) collection.getExtraInformation().get("stageCount")).intValue();
        long playerSeed = ((Number) collection.getExtraInformation().get("seed")).longValue();

        SoloDraft soloDraft = soloDraftLeagueData.getSoloDraft();
        Iterable<SoloDraft.DraftChoice> possibleChoices = soloDraft.getAvailableChoices(playerSeed, stage, collection, null);
        SoloDraft.DraftChoice draftChoice = getSelectedDraftChoice(selectedChoiceId, possibleChoices);
        if (draftChoice == null)
            throw new HttpProcessingException(400);

        CardCollection selectedCards = soloDraft.getCardsForChoiceId(selectedChoiceId, playerSeed, stage, collection);

        Map<String, Object> extraInformationChanges = new HashMap<String, Object>();
        boolean hasNextStage = soloDraft.hasNextStage(playerSeed, stage);
        extraInformationChanges.put("stage", stage + 1);
        if (!hasNextStage)
            extraInformationChanges.put("finished", true);

        _collectionsManager.addItemsToPlayerCollection(false, "Draft pick", resourceOwner, collectionType, selectedCards.getAll().values(), extraInformationChanges);
        collection = _collectionsManager.getPlayerCollection(resourceOwner, collectionType.getCode());

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element pickResultElem = doc.createElement("pickResult");

        Element draftStateElem = doc.createElement("state");
        draftStateElem.setAttribute("stage",String.valueOf(stage + 1));
        draftStateElem.setAttribute("stages",String.valueOf(stages));
        pickResultElem.appendChild(draftStateElem);

        doc.appendChild(pickResultElem);

        for (CardCollection.Item item : selectedCards.getAll().values()) {
            Element pickedCard = doc.createElement("pickedCard");
            String blueprintId = item.getBlueprintId();
            pickedCard.setAttribute("blueprintId", blueprintId);
            pickedCard.setAttribute("count", String.valueOf(item.getCount()));
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.isHorizontal()) {
                    pickedCard.setAttribute("horizontal", "true");
                }
                pickedCard.setAttribute("side", blueprint.getSide().toString().toLowerCase());
            }
            pickResultElem.appendChild(pickedCard);
        }

        if (hasNextStage) {
            Iterable<SoloDraft.DraftChoice> availableChoices = soloDraft.getAvailableChoices(playerSeed, stage + 1, collection, selectedChoiceId);
            appendAvailablePics(doc, pickResultElem, availableChoices);
        }

        responseWriter.writeXmlResponse(doc);
    }

    private void appendAvailablePics(Document doc, Element rootElem, Iterable<SoloDraft.DraftChoice> availablePics) {
        for (SoloDraft.DraftChoice availableChoice : availablePics) {
            String choiceId = availableChoice.getChoiceId();
            String blueprintId = availableChoice.getBlueprintId();
            String choiceUrl = availableChoice.getChoiceUrl();
            String packDesc = availableChoice.getObjPackDescription();
            Element availablePick = doc.createElement("availablePick");
            availablePick.setAttribute("id", choiceId);
            if (blueprintId != null) {
                availablePick.setAttribute("blueprintId", blueprintId);
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                if (blueprint != null) {
                    if (blueprint.isHorizontal()) {
                        availablePick.setAttribute("horizontal", "true");
                    }
                    availablePick.setAttribute("side", blueprint.getSide().toString().toLowerCase());
                }
            }
            if (choiceUrl != null)
                availablePick.setAttribute("url", choiceUrl);
            if (packDesc != null)
                availablePick.setAttribute("desc", packDesc);
            rootElem.appendChild(availablePick);
        }
    }

    private SoloDraft.DraftChoice getSelectedDraftChoice(String choiceId, Iterable<SoloDraft.DraftChoice> availableChoices) {
        for (SoloDraft.DraftChoice availableChoice : availableChoices) {
            if (availableChoice.getChoiceId().equals(choiceId))
                return availableChoice;
        }
        return null;
    }
}
