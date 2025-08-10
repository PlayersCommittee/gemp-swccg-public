package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.cache.CacheManager;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.LeagueDecklistEntry;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.state.SortPlayerByName;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.*;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.tournament.TournamentService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private final LeagueService _leagueService;
    private final TournamentService _tournamentService;
    private final CacheManager _cacheManager;
    private final HallServer _hallServer;
    private final SwccgCardBlueprintLibrary _cardLibrary;
    private final SwccgoFormatLibrary _formatLibrary;
    private final LeagueDAO _leagueDao;
    private final CollectionsManager _collectionManager;
    private final PlayerDAO _playerDAO;
    private final AdminService _adminService;
    private final GameHistoryService _gameHistoryService;
    private static final Logger _log = LogManager.getLogger(AdminRequestHandler.class);

    public AdminRequestHandler(Map<Type, Object> context) {
        super(context);
        _leagueService = extractObject(context, LeagueService.class);
        _tournamentService = extractObject(context, TournamentService.class);
        _cacheManager = extractObject(context, CacheManager.class);
        _hallServer = extractObject(context, HallServer.class);
        _cardLibrary = extractObject(context, SwccgCardBlueprintLibrary.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _leagueDao = extractObject(context, LeagueDAO.class);
        _playerDAO = extractObject(context, PlayerDAO.class);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _adminService = extractObject(context, AdminService.class);
        _gameHistoryService = extractObject(context, GameHistoryService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.equals("/clearcache") && request.method() == HttpMethod.POST) {
            clearCacheRequest(request, responseWriter);
        } else if (uri.equals("/shutdown") && request.method() == HttpMethod.POST) {
            shutdown(request, responseWriter);
        } else if (uri.equals("/motd/get") && request.method() == HttpMethod.GET) {
            getMotd(request, responseWriter);
        } else if (uri.equals("/motd/update") && request.method() == HttpMethod.POST) {
            setMotd(request, responseWriter);
        } else if (uri.equals("/league/sealed/preview") && request.method() == HttpMethod.POST) {
            processSealedLeague(request, responseWriter, true);
        } else if (uri.equals("/league/sealed/create") && request.method() == HttpMethod.POST) {
            processSealedLeague(request, responseWriter, false);
        } else if (uri.equals("/league/constructed/preview") && request.method() == HttpMethod.POST) {
            processConstructedLeague(request, responseWriter, true);
        } else if (uri.equals("/league/constructed/create") && request.method() == HttpMethod.POST) {
            processConstructedLeague(request, responseWriter, false);
        } else if (uri.equals("/league/addplayers") && request.method() == HttpMethod.POST) {
            addPlayersToLeague(request, responseWriter, remoteIp);
        } else if (uri.equals("/collections/additems") && request.method() == HttpMethod.POST) {
            addItems(request, responseWriter);
        } else if (uri.equals("/collections/addcurrency") && request.method() == HttpMethod.POST) {
            addCurrency(request, responseWriter);
        } else if (uri.equals("/collections/additemstoall") && request.method() == HttpMethod.POST) {
            addItemsToAllPlayers(request, responseWriter);
        } else if (uri.equals("/user/addflag") && request.method() == HttpMethod.POST) {
            addFlagToUser(request, responseWriter);
        } else if (uri.equals("/users/removeflag") && request.method() == HttpMethod.POST) {
            removeFlagFromUsers(request, responseWriter);
        } else if (uri.equals("/users/findwithflag") && request.method() == HttpMethod.POST) {
            showUsersWithFlag(request, responseWriter);
        } else if (uri.equals("/user/passwordreset") && request.method() == HttpMethod.POST) {
            resetUserPassword(request, responseWriter);
        } else if (uri.equals("/users/deactivate") && request.method() == HttpMethod.POST) {
            deactivateMultiple(request, responseWriter);
        } else if (uri.equals("/user/ban/permanent") && request.method() == HttpMethod.POST) {
            banUser(request, responseWriter);
        } else if (uri.equals("/users/ban/permanent") && request.method() == HttpMethod.POST) {
            banMultiple(request, responseWriter);
        } else if (uri.equals("/user/ban/temporary") && request.method() == HttpMethod.POST) {
            banUserTemp(request, responseWriter);
        } else if (uri.equals("/user/ban/acquit") && request.method() == HttpMethod.POST) {
            unBanUser(request, responseWriter);
        } else if (uri.equals("/users/detailedsearch") && request.method() == HttpMethod.POST) {
            findMultipleAccounts(request, responseWriter);
        } else if (uri.equals("/settings/privategames") && request.method() == HttpMethod.POST) {
            setPrivateGames(request, responseWriter);
        } else if (uri.equals("/settings/bonusabilities") && request.method() == HttpMethod.POST) {
            setBonusAbilities(request, responseWriter);
        } else if (uri.equals("/settings/stattracking") && request.method() == HttpMethod.POST) {
            setInGameStatTracking(request, responseWriter);
        } else if (uri.equals("/settings/newaccounts") && request.method() == HttpMethod.POST) {
            setNewAccountRegistration(request, responseWriter);
        } else if (uri.equals("/settings/purgestattrackers") && request.method() == HttpMethod.POST) {
            purgeInGameStatisticListeners(request, responseWriter);
		} else if (uri.equals("/league/deckcheck") && request.method() == HttpMethod.POST) {
            deckCheck(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void findMultipleAccounts(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String login = getFormParameterSafely(postDecoder, "login");

            List<Player> similarPlayers = _playerDAO.findSimilarAccounts(login);
            if (similarPlayers == null)
                throw new HttpProcessingException(404);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element players = doc.createElement("players");

            for (Player similarPlayer : similarPlayers) {
                if (similarPlayer != null && similarPlayer.getId() != 0) {
                    Element playerElem = doc.createElement("player");
                    playerElem.setAttribute("id", String.valueOf(similarPlayer.getId()));
                    playerElem.setAttribute("name", similarPlayer.getName());
                    playerElem.setAttribute("password", similarPlayer.getPassword());
                    playerElem.setAttribute("status", getStatus(similarPlayer));
                    playerElem.setAttribute("createIp", similarPlayer.getCreateIp());
                    playerElem.setAttribute("loginIp", similarPlayer.getLastIp());
                    playerElem.setAttribute("lastLoginReward", similarPlayer.getLastLoginReward() != null ? String.valueOf(similarPlayer.getLastLoginReward()) : "Never");
                    players.appendChild(playerElem);
                }
            }

            doc.appendChild(players);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private String getStatus(Player player) {
        if (!player.hasType(Player.Type.UNBANNED)) {
            if (player.getBannedUntil() != null) {
                if (player.getBannedUntil().before(new Date())) {
                    return "Unbanned (ban expired)";
                }
                else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    return "Banned until " + format.format(player.getBannedUntil());
                }
            }
            else {
                return "Banned permanently";
            }
        }

        String status = "Unbanned";
        if(player.hasType(Player.Type.ADMIN))
            status += ", Administrator";
        if(player.hasType(Player.Type.COMMENTARY_ADMIN))
            status += ", Commentary Admin";
        if(player.hasType(Player.Type.COMMENTATOR))
            status += ", Commentator";
        if(player.hasType(Player.Type.PLAYTESTING_ADMIN))
            status += ", Playtest Admin";
        if(player.hasType(Player.Type.PLAYTESTER))
            status += ", Playtester";
        if(player.hasType(Player.Type.LEAGUE_ADMIN))
            status += ", League Admin";
        if(player.hasType(Player.Type.DEACTIVATED))
            status += ", Deactivated";

        return status;
    }

    private void resetUserPassword(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String login = getFormParameterSafely(postDecoder, "login");

            if (login == null)
                throw new HttpProcessingException(404);

            if (!_adminService.resetUserPassword(login))
                throw new HttpProcessingException(404);

            clearCache();
            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void showUsersWithFlag(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            Player.Type flag = Player.Type.getFromName(getFormParameterSafely(postDecoder, "flag"));

            validateVariableAdmin(request, Objects.requireNonNull(flag));

            List<Player> players = _playerDAO.findPlayersWithFlag(flag);
            if (players == null)
                throw new HttpProcessingException(404);

            Collections.sort(players, new SortPlayerByName());

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element playerList = doc.createElement("players");

            for (Player player : players) {
                Element playerElem = doc.createElement("player");
                playerElem.setAttribute("name", player.getName());
                playerList.appendChild(playerElem);
            }

            doc.appendChild(playerList);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void addFlagToUser(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            Player.Type flag = Player.Type.getFromName(getFormParameterSafely(postDecoder, "flag"));

            validateVariableAdmin(request, Objects.requireNonNull(flag));

            String login = getFormParameterSafely(postDecoder, "login");

            if (login == null)
                throw new HttpProcessingException(404);

            if (!_adminService.setUserFlag(login, flag, true))
                throw new HttpProcessingException(404);

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void removeFlagFromUsers(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            Player.Type flag = Player.Type.getFromName(getFormParameterSafely(postDecoder, "flag"));

            validateVariableAdmin(request, Objects.requireNonNull(flag));

            List<String> logins = getFormParametersSafely(postDecoder, "logins");
            if (logins == null)
                throw new HttpProcessingException(404);

            for (String login : logins) {
                if (!_adminService.setUserFlag(login, flag, false))
                    throw new HttpProcessingException(404);
            }

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void banUser(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String login = getFormParameterSafely(postDecoder, "login");

            if (login==null)
                throw new HttpProcessingException(404);

            if (!_adminService.banUser(login))
                throw new HttpProcessingException(404);

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void deactivateMultiple(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            List<String> logins = getFormParametersSafely(postDecoder, "logins");
            if (logins == null)
                throw new HttpProcessingException(404);

            for (String login : logins) {
                if (!_adminService.deactivateUser(login))
                    throw new HttpProcessingException(404);
            }

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void banMultiple(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            List<String> logins = getFormParametersSafely(postDecoder, "logins");
            if (logins == null)
                throw new HttpProcessingException(404);

            for (String login : logins) {
                if (!_adminService.banUser(login))
                    throw new HttpProcessingException(404);
            }

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void banUserTemp(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String login = getFormParameterSafely(postDecoder, "login");
            int duration = Integer.parseInt(getFormParameterSafely(postDecoder, "duration"));

            if (!_adminService.banUserTemp(login, duration))
                throw new HttpProcessingException(404);

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void unBanUser(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String login = getFormParameterSafely(postDecoder, "login");

            if (!_adminService.unBanUser(login))
                throw new HttpProcessingException(404);

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void addItemsToAllPlayers(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String reason = getFormParameterSafely(postDecoder, "reason");
            String product = getFormParameterSafely(postDecoder, "product");
            String collectionType = getFormParameterSafely(postDecoder, "collectionType");

            Collection<CardCollection.Item> productItems = getProductItems(product);

            List<String> cannotAdd = validateItemsToAdd(productItems);

            if(!cannotAdd.isEmpty()) {
                responseWriter.writeHtmlResponse(listToString(cannotAdd));
            } else {
                Map<Player, CardCollection> playersCollection = _collectionManager.getPlayersCollection(collectionType);

                for (Map.Entry<Player, CardCollection> playerCollection : playersCollection.entrySet()) {
                    if (playerCollection.getKey() != null
                            && playerCollection.getKey().hasType(Player.Type.UNBANNED)
                            && playerCollection.getValue()!=null
                            && !playerCollection.getKey().getName().startsWith("rando_")) {
                        _collectionManager.addItemsToPlayerCollection(true, reason + " (" + getResourceOwnerSafely(request, null).getName() + ")", playerCollection.getKey(), createCollectionType(collectionType), productItems);
                    }
                }

                responseWriter.writeHtmlResponse("OK");
            }
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void addItems(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String players = getFormParameterSafely(postDecoder, "players");
            String product = getFormParameterSafely(postDecoder, "product");
            String collectionType = getFormParameterSafely(postDecoder, "collectionType");

            Collection<CardCollection.Item> productItems = getProductItems(product);

            List<String> cannotAdd = validateItemsToAdd(productItems);

            if(!cannotAdd.isEmpty()) {
                responseWriter.writeHtmlResponse(listToString(cannotAdd));
            } else {
                List<String> playerNames = getItems(players);

                List<String> invalidUsernames = getInvalidUsernameList(playerNames);

                if (!invalidUsernames.isEmpty()) {
                    responseWriter.writeHtmlResponse("Did not add any items. "+invalidUsernameListToString(invalidUsernames));
                } else {

                    for (String playerName : playerNames) {
                        Player player = _playerDao.getPlayer(playerName);

                        _collectionManager.addItemsToPlayerCollection(true, "Administrator action (" + getResourceOwnerSafely(request, null).getName() + ")", player, createCollectionType(collectionType), productItems);
                    }

                    responseWriter.writeHtmlResponse("OK");
                }
            }
        }
        finally {
            postDecoder.destroy();
        }
    }

    private String listToString(List<String> cannotAdd) {
        StringBuilder stringBuilder = new StringBuilder("Did not add any items. Unable to add:");
        for (String s : cannotAdd) {
            stringBuilder.append("<br>").append(s);
        }
        return stringBuilder.toString();
    }

    //Need to get up to Java 8, and then such functions can be replaced by one-liners like
    // String.join(", ", invalidUsernames)
    private String invalidUsernameListToString(List<String> invalidUsernames) {
        StringBuilder stringBuilder = new StringBuilder("Invalid usernames:");
        for (String s : invalidUsernames) {
            stringBuilder.append("<br>").append(s);
        }
        return stringBuilder.toString();
    }

    private List<String> validateItemsToAdd(Collection<CardCollection.Item> productItems) throws IOException {
        //check if all the items are formatted correctly (adding the wrong product to a collection can break it)
        List<String> cannotAdd = new ArrayList<>();

        //pack list
        Set<String> packList = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(AdminRequestHandler.class.getResourceAsStream("/packs.txt")),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                packList.add(line);
        }

        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();

        for(CardCollection.Item item: productItems) {
            switch(item.getType()) {
                case CARD:
                    if(library.getSwccgoCardBlueprint(item.getBlueprintId())==null || item.getBlueprintId().startsWith("0") || item.getBlueprintId().contains("_0"))
                        cannotAdd.add(item.getBlueprintId());
                    break;
                case PACK:
                case SELECTION:
                    if(!packList.contains(item.getBlueprintId()))
                        cannotAdd.add(item.getBlueprintId());
                    break;
                default:
                    cannotAdd.add(item.getBlueprintId());
            }
        }

        return cannotAdd;
    }

    private List<String> getInvalidUsernameList(Collection<String> playerNames) {
        List<String> cannotAdd = new ArrayList<>();
        for(String playerName: playerNames) {
            try {
                Player player = _playerDao.getPlayer(playerName);
                if (player == null)
                    cannotAdd.add(playerName);
            } catch(Exception e) {
                cannotAdd.add(playerName);
            }
        }
        return cannotAdd;
    }

    private void addCurrency(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String players = getFormParameterSafely(postDecoder, "players");
            int currencyAmount = Integer.parseInt(getFormParameterSafely(postDecoder, "currencyAmount"));

            int currencyAddLimit = 10000;

            if(currencyAmount>currencyAddLimit || currencyAmount<0) {
                responseWriter.writeHtmlResponse("Cannot add "+currencyAmount);
            } else {

                List<String> playerNames = getItems(players);

                List<String> invalidUsernames = getInvalidUsernameList(playerNames);

                if (!invalidUsernames.isEmpty()) {
                    responseWriter.writeHtmlResponse("Did not add any currency. " + invalidUsernameListToString(invalidUsernames));
                } else {
                    for (String playerName : playerNames) {
                        Player player = _playerDao.getPlayer(playerName);
                        _collectionManager.addCurrencyToPlayerCollection(true, "Administrator action (" + getResourceOwnerSafely(request, null).getName() + ")", player, createCollectionType("permanent"), currencyAmount);
                    }

                    responseWriter.writeHtmlResponse("OK");
                }
            }
        }
        finally {
            postDecoder.destroy();
        }
    }

    private List<String> getItems(String values) {
        List<String> result = new LinkedList<>();
        for (String pack : values.split("\n")) {
            String blueprint = pack.trim();
            if (blueprint.length() > 0)
                result.add(blueprint);
        }
        return result;
    }

    private Collection<CardCollection.Item> getProductItems(String values) {
        List<CardCollection.Item> result = new LinkedList<>();
        for (String item : values.split("\n")) {
            item = item.trim();
            if (item.length() > 0) {
                final String[] itemSplit = item.split("x", 2);
                if (itemSplit.length != 2)
                    throw new RuntimeException("Unable to parse the items");
                result.add(CardCollection.Item.createItem(itemSplit[1].trim(), Integer.parseInt(itemSplit[0].trim())));
            }
        }
        return result;
    }

    private CollectionType createCollectionType(String collectionType) {
        if (collectionType.equals("permanent"))
            return CollectionType.MY_CARDS;

        return _leagueService.getCollectionTypeByCode(collectionType);
    }

    /**
     * Processes the passed parameters for a theoretical Sealed League.  Based on the preview parameter, this will
     * either create the league for real, or just return the parsed values to the client so the admin can preview
     * the input.
     * @param request the request
     * @param responseWriter the response writer
     * @param preview If true, no league will be created and the client will have an XML payload returned representing
     *                what the league would be upon creation.  If false, the league will be created for real.
     * @throws Exception
     */
    private void processSealedLeague(HttpRequest request, ResponseWriter responseWriter, boolean preview) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String name = getFormParameterSafely(postDecoder, "name");
            String costStr = getFormParameterSafely(postDecoder, "cost");
            String startStr = getFormParameterSafely(postDecoder, "start");
            String format = getFormParameterSafely(postDecoder, "format");
            String serieDurationStr = getFormParameterSafely(postDecoder, "serieDuration");
            String maxMatchesStr = getFormParameterSafely(postDecoder, "maxMatches");
            String allowTimeExtensionsStr = getFormParameterSafely(postDecoder, "allowTimeExtensions");
            String allowSpectatorsStr = getFormParameterSafely(postDecoder, "allowSpectators");
            String showPlayerNamesStr = getFormParameterSafely(postDecoder, "showPlayerNames");
            String invitationOnlyStr = getFormParameterSafely(postDecoder, "invitationOnly");
            String registrationInfo = getFormParameterSafely(postDecoder, "registrationInfo");
            String decisionTimeoutStr = getFormParameterSafely(postDecoder, "decisionTimeoutSeconds");
            String timePerPlayerStr = getFormParameterSafely(postDecoder, "timePerPlayerMinutes");

            Throw400IfStringNull("name", name);
            int cost = Throw400IfNullOrNonInteger("cost", costStr);
            int start = Throw400IfNullOrNonInteger("start", startStr);
            if(startStr.length() != 8)
                throw new HttpProcessingException(400, "Parameter 'start' must be exactly 8 digits long: YYYYMMDD");
            Throw400IfStringNull("format", format);
            int serieDuration = Throw400IfNullOrNonInteger("serieDuration", serieDurationStr);
            int maxMatches = Throw400IfNullOrNonInteger("maxMatches", maxMatchesStr);
            boolean allowTimeExtensions = Throw400IfNullOrNonBoolean("allowTimeExtensions", allowTimeExtensionsStr);
            boolean allowSpectators = Throw400IfNullOrNonBoolean("allowSpectators", allowSpectatorsStr);
            boolean showPlayerNames = Throw400IfNullOrNonBoolean("showPlayerNames", showPlayerNamesStr);
            boolean invitationOnly = Throw400IfNullOrNonBoolean("invitationOnly", invitationOnlyStr);
            //Throw400IfStringNull("registrationInfo", registrationInfo);
            int decisionTimeoutSeconds = Throw400IfNullOrNonInteger("decisionTimeoutSeconds", decisionTimeoutStr);
            int timePerPlayerMinutes = Throw400IfNullOrNonInteger("timePerPlayerMinutes", timePerPlayerStr);

            if(registrationInfo.toLowerCase().contains("starwarsccg.org") && !registrationInfo.contains(" ")) {
                registrationInfo = "<a href='" + registrationInfo + "' target='_blank'>" + registrationInfo + "</a>";
            }

            String code = String.valueOf(System.currentTimeMillis());

            String parameters = format + "," + start + "," + serieDuration + "," + maxMatches + "," + code + "," + name;
            LeagueData leagueData = new NewSealedLeagueData(_cardLibrary, parameters);
            List<LeagueSeriesData> series = leagueData.getSeries();
            int leagueStart = series.get(0).getStart();
            int displayEnd = DateUtils.offsetDate(series.get(series.size() - 1).getEnd(), 2);

            if(!preview) {
                _leagueDao.addLeague(cost, name, code, leagueData.getClass().getName(), parameters, leagueStart, displayEnd,
                        allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo,
                        decisionTimeoutSeconds, timePerPlayerMinutes);
                _leagueService.clearCache();

                responseWriter.writeHtmlResponse("OK");
                return;
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            Element leagueElem = doc.createElement("league");

            leagueElem.setAttribute("name", name);
            leagueElem.setAttribute("cost", String.valueOf(cost));
            leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
            leagueElem.setAttribute("allowTimeExtensions", String.valueOf(allowTimeExtensions));
            leagueElem.setAttribute("allowSpectators", String.valueOf(allowSpectators));
            leagueElem.setAttribute("showPlayerNames", String.valueOf(showPlayerNames));
            leagueElem.setAttribute("invitationOnly", String.valueOf(invitationOnly));
            leagueElem.setAttribute("registrationInfo", registrationInfo);
            leagueElem.setAttribute("decisionTimeoutSeconds", String.valueOf(decisionTimeoutSeconds));
            leagueElem.setAttribute("timePerPlayerMinutes", String.valueOf(timePerPlayerMinutes));

            leagueElem.setAttribute("end", String.valueOf(displayEnd));

            for (LeagueSeriesData serie : series) {
                Element serieElem = doc.createElement("serie");
                serieElem.setAttribute("type", serie.getName());
                serieElem.setAttribute("maxMatches", String.valueOf(serie.getMaxMatches()));
                serieElem.setAttribute("start", String.valueOf(serie.getStart()));
                serieElem.setAttribute("end", String.valueOf(serie.getEnd()));
                serieElem.setAttribute("format", _formatLibrary.getFormat(serie.getFormat()).getName());
                serieElem.setAttribute("collection", serie.getCollectionType().getFullName());
                serieElem.setAttribute("limited", String.valueOf(serie.isLimited()));

                leagueElem.appendChild(serieElem);
            }

            doc.appendChild(leagueElem);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    /**
     * Processes the passed parameters for a theoretical Constructed League.  Based on the preview parameter, this will
     * either create the league for real, or just return the parsed values to the client so the admin can preview
     * the input.
     * @param request the request
     * @param responseWriter the response writer
     * @param preview If true, no league will be created and the client will have an XML payload returned representing
     *                what the league would be upon creation.  If false, the league will be created for real.
     * @throws Exception
     */
    private void processConstructedLeague(HttpRequest request, ResponseWriter responseWriter, boolean preview) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String name = getFormParameterSafely(postDecoder, "name");
            String costStr = getFormParameterSafely(postDecoder, "cost");
            String startStr = getFormParameterSafely(postDecoder, "start");
            String collectionType = getFormParameterSafely(postDecoder, "collectionType");
            //Individual serie definitions
            List<String> formats = getFormMultipleParametersSafely(postDecoder, "formats");
            List<String> serieDurationsStr = getFormMultipleParametersSafely(postDecoder, "serieDurations");
            List<String> maxMatchesStr = getFormMultipleParametersSafely(postDecoder, "maxMatches");
            String allowTimeExtensionsStr = getFormParameterSafely(postDecoder, "allowTimeExtensions");
            String allowSpectatorsStr = getFormParameterSafely(postDecoder, "allowSpectators");
            String showPlayerNamesStr = getFormParameterSafely(postDecoder, "showPlayerNames");
            String invitationOnlyStr = getFormParameterSafely(postDecoder, "invitationOnly");
            String registrationInfo = getFormParameterSafely(postDecoder, "registrationInfo");
            String decisionTimeoutStr = getFormParameterSafely(postDecoder, "decisionTimeoutSeconds");
            String timePerPlayerStr = getFormParameterSafely(postDecoder, "timePerPlayerMinutes");

            Throw400IfStringNull("name", name);
            int cost = Throw400IfNullOrNonInteger("cost", costStr);
            int start = Throw400IfNullOrNonInteger("start", startStr);
            if(startStr.length() != 8)
                throw new HttpProcessingException(400, "Parameter 'start' must be exactly 8 digits long: YYYYMMDD");
            Throw400IfAnyStringNull("formats", formats);
            Throw400IfStringNull("collectionType", collectionType);
            List<Integer> serieDurations = Throw400IfAnyNullOrNonInteger("serieDurations", serieDurationsStr);
            List<Integer> maxMatches = Throw400IfAnyNullOrNonInteger("maxMatches", maxMatchesStr);
            boolean allowTimeExtensions = Throw400IfNullOrNonBoolean("allowTimeExtensions", allowTimeExtensionsStr);
            boolean allowSpectators = Throw400IfNullOrNonBoolean("allowSpectators", allowSpectatorsStr);
            boolean showPlayerNames = Throw400IfNullOrNonBoolean("showPlayerNames", showPlayerNamesStr);
            boolean invitationOnly = Throw400IfNullOrNonBoolean("invitationOnly", invitationOnlyStr);
            //Throw400IfStringNull("registrationInfo", registrationInfo);
            int decisionTimeoutSeconds = Throw400IfNullOrNonInteger("decisionTimeoutSeconds", decisionTimeoutStr);
            int timePerPlayerMinutes = Throw400IfNullOrNonInteger("timePerPlayerMinutes", timePerPlayerStr);

            if(registrationInfo.toLowerCase().contains("starwarsccg.org") && !registrationInfo.contains(" ")) {
                registrationInfo = "<a href='" + registrationInfo + "' target='_blank'>" + registrationInfo + "</a>";
            }

            String code = String.valueOf(System.currentTimeMillis());

            StringBuilder sb = new StringBuilder();
            sb.append(start).append(",").append(collectionType).append(",").append(formats.size());
            for (int i = 0; i < formats.size(); ++i) {
                sb.append(",").append(formats.get(i)).append(",").append(serieDurations.get(i)).append(",").append(
                        maxMatches.get(i));
            }

            String parameters = sb.toString();
            LeagueData leagueData = new NewConstructedLeagueData(_cardLibrary, parameters);
            List<LeagueSeriesData> series = leagueData.getSeries();
            int leagueStart = series.get(0).getStart();
            int displayEnd = DateUtils.offsetDate(series.get(series.size() - 1).getEnd(), 2);

            if(!preview) {
                _leagueDao.addLeague(cost, name, code, leagueData.getClass().getName(), parameters, leagueStart, displayEnd,
                        allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo,
                        decisionTimeoutSeconds, timePerPlayerMinutes);

                _leagueService.clearCache();

                responseWriter.writeHtmlResponse("OK");
                return;
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            Element leagueElem = doc.createElement("league");

            leagueElem.setAttribute("name", name);
            leagueElem.setAttribute("cost", String.valueOf(cost));
            leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
            leagueElem.setAttribute("end", String.valueOf(displayEnd));
            leagueElem.setAttribute("collectionType", collectionType);
            leagueElem.setAttribute("allowTimeExtensions", String.valueOf(allowTimeExtensions));
            leagueElem.setAttribute("allowSpectators", String.valueOf(allowSpectators));
            leagueElem.setAttribute("showPlayerNames", String.valueOf(showPlayerNames));
            leagueElem.setAttribute("invitationOnly", String.valueOf(invitationOnly));
            leagueElem.setAttribute("registrationInfo", registrationInfo);
            leagueElem.setAttribute("decisionTimeoutSeconds", String.valueOf(decisionTimeoutSeconds));
            leagueElem.setAttribute("timePerPlayerMinutes", String.valueOf(timePerPlayerMinutes));

            for (LeagueSeriesData serie : series) {
                Element serieElem = doc.createElement("serie");
                serieElem.setAttribute("type", serie.getName());
                serieElem.setAttribute("maxMatches", String.valueOf(serie.getMaxMatches()));
                serieElem.setAttribute("start", String.valueOf(serie.getStart()));
                serieElem.setAttribute("end", String.valueOf(serie.getEnd()));
                serieElem.setAttribute("format", _formatLibrary.getFormat(serie.getFormat()).getName());
                serieElem.setAttribute("collection", serie.getCollectionType().getFullName());
                serieElem.setAttribute("limited", String.valueOf(serie.isLimited()));

                leagueElem.appendChild(serieElem);
            }

            doc.appendChild(leagueElem);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void addPlayersToLeague(HttpRequest request, ResponseWriter responseWriter, String remoteIp) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String leagueType = getFormParameterSafely(postDecoder, "leagueType");
            List<String> playerNames = getFormParametersSafely(postDecoder, "players");

            League league = _leagueService.getLeagueByType(leagueType);
            if (league == null) {
                throw new HttpProcessingException(400, "League '" + leagueType + "' does not exist.");
            }

            List<String> invalidUsernames = getInvalidUsernameList(playerNames);

            if (!invalidUsernames.isEmpty()) {
                throw new HttpProcessingException(400, "Added no players to the league. " +
                        invalidUsernameListToString(invalidUsernames) + ".");
            }

            for (String playerName : playerNames) {
                Player player = _playerDao.getPlayer(playerName);
                if (player != null) {
                    if (!_leagueService.isPlayerInLeague(league, player)) {
                        if (!_leagueService.playerJoinsLeague(league, player, remoteIp, true, true)) {
                            throw new HttpProcessingException(500, "Failed to add player '" + player + "' to the league.  Aborting.");
                        }
                    }
                }
            }

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void getMotd(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        String motd = _hallServer.getMOTD();

        if(motd != null) {
            responseWriter.writeJsonResponse(motd.replace("\n", "<br>"));
        }
    }

    private void setMotd(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String motd = getFormParameterSafely(postDecoder, "motd");

            _hallServer.setMOTD(motd);

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void shutdown(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            boolean shutdown = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "enabled"));

            if(shutdown) {
                _hallServer.setShutdown();
            }
            else {
                _hallServer.setOperational();
            }

            responseWriter.writeHtmlResponse("OK");
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void clearCacheRequest(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException {
        validateAdmin(request);

        int before = _cacheManager.getTotalCount();

        clearCache();

        int after = _cacheManager.getTotalCount();

        responseWriter.writeHtmlResponse("OK<br><br>Before: " + before + "<br><br>After: " + after);
    }

    private void clearCache()  {
        _leagueService.clearCache();
        _tournamentService.clearCache();
        _cacheManager.clearCaches();
        _hallServer.cleanup(true);
    }


    private void setPrivateGames(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            boolean enabled = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "enabled"));

            _hallServer.setPrivateGames(enabled);

            responseWriter.writeHtmlResponse("OK.  Private games enabled: " + _hallServer.privateGamesAllowed());
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void setBonusAbilities(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            boolean enabled = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "enabled"));

            _hallServer.setBonusAbilities(enabled);

            responseWriter.writeHtmlResponse("OK.  Bonus abilities enabled in casual games: " + _hallServer.bonusAbilitiesEnabled());
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void setNewAccountRegistration(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            boolean enabled = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "enabled"));

            _gempSettingDAO.setNewAccountRegistrationEnabled(enabled);

            responseWriter.writeHtmlResponse("New account registration enabled: " + enabled
                    + (enabled ? "" : " (remember to turn this back on)"));
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void setInGameStatTracking(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            boolean enabled = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "enabled"));

            _hallServer.setInGameStatisticsEnabled(enabled);

            responseWriter.writeHtmlResponse("OK.  In game statistics tracking enabled: " + _hallServer.inGameStatisticsEnabled());
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void purgeInGameStatisticListeners(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException {
        validateAdmin(request);
        int count = _hallServer.removeInGameStatisticsListeners();

        responseWriter.writeHtmlResponse("In game statistics tracking removed from " + count +
                " active games<br>In game statistics tracking enabled: " + _hallServer.inGameStatisticsEnabled());
    }

    private void deckCheck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String leagueId = getFormParameterSafely(postDecoder, "leagueId");

            Throw400IfStringNull("leagueId", leagueId);

            List<LeagueDecklistEntry> decklistEntries = _gameHistoryService.getDeckCheck(leagueId);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element deckCheckEntries = doc.createElement("deckCheckEntries");

            for(LeagueDecklistEntry entry:decklistEntries) {
                Element entryElement = doc.createElement("entry");
                entryElement.setAttribute("leagueName", entry.getLeagueName());
                entryElement.setAttribute("startTime", String.valueOf(entry.getStartTime().getTime()));
                entryElement.setAttribute("player", entry.getPlayer());
                entryElement.setAttribute("side", entry.getSide());
                entryElement.setAttribute("deck", entry.getDeck());
                deckCheckEntries.appendChild(entryElement);
            }
            doc.appendChild(deckCheckEntries);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void Throw400IfStringNull(String paramName, String value) throws HttpProcessingException {
        if(StringUtils.isEmpty(value)) {
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' cannot be blank.");
        }
    }

    private void Throw400IfAnyStringNull(String paramName, List<String> values) throws HttpProcessingException {
        if(values == null || values.isEmpty())
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' must have values set.");

        for (String value : values) {
            if(StringUtils.isEmpty(value)) {
                throw new HttpProcessingException(400, "Parameter '" + paramName + "' cannot be blank.");
            }
        }
    }

    private int Throw400IfNullOrNonInteger(String paramName, String value) throws HttpProcessingException {
        if(StringUtils.isEmpty(value)) {
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' cannot be blank.");
        }
        int newValue;
        try {
            newValue = Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' must be a valid numeric integer.");
        }

        return newValue;
    }

    private List<Integer> Throw400IfAnyNullOrNonInteger(String paramName, List<String> values) throws HttpProcessingException {
        List<Integer> newValues = new ArrayList<>();

        if(values == null || values.isEmpty())
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' must have values set.");
        
        for(String value : values) {
            if(StringUtils.isEmpty(value)) {
                throw new HttpProcessingException(400, "Parameter '" + paramName + "' cannot be blank.");
            }
            int newValue;
            try {
                newValue = Integer.parseInt(value);
            }
            catch (NumberFormatException ex) {
                throw new HttpProcessingException(400, "Parameter '" + paramName + "' must be a valid numeric integer: '" + value + "'.");
            }
            newValues.add(newValue);
        }

        return newValues;
    }

    private boolean Throw400IfNullOrNonBoolean(String paramName, String value) throws HttpProcessingException {
        if(StringUtils.isEmpty(value)) {
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' cannot be blank.");
        }
        boolean newValue;
        try {
            newValue = Boolean.parseBoolean(value);
        }
        catch (NumberFormatException ex) {
            throw new HttpProcessingException(400, "Parameter '" + paramName + "' must be a valid boolean value ('true' or 'false').");
        }

        return newValue;
    }

    /**
     * Verifies the request is from an admin user.
     * @param request the request
     * @throws HttpProcessingException an exception
     */
    private void validateAdmin(HttpRequest request) throws HttpProcessingException {
        Player player = getResourceOwnerSafely(request, null);

        if (!player.hasType(Player.Type.ADMIN))
            throw new HttpProcessingException(403);
    }

    /**
     * Verifies the request is from an admin (or league admin) user.
     * @param request the request
     * @throws HttpProcessingException an exception
     */
    private void validateLeagueAdmin(HttpRequest request) throws HttpProcessingException {
        Player player = getResourceOwnerSafely(request, null);

        if (!player.hasType(Player.Type.ADMIN) && !player.hasType(Player.Type.LEAGUE_ADMIN))
            throw new HttpProcessingException(403);
    }

    /**
     * Verifies the request is from an admin (or playtesting admin) user.
     * @param request the request
     * @throws HttpProcessingException an exception
     */
    private void validatePlaytestingAdmin(HttpRequest request) throws HttpProcessingException {
        Player player = getResourceOwnerSafely(request, null);

        if (!player.hasType(Player.Type.ADMIN) && !player.hasType(Player.Type.PLAYTESTING_ADMIN))
            throw new HttpProcessingException(403);
    }

    /**
     * Verifies the request is from an admin (or commentator admin) user.
     * @param request the request
     * @throws HttpProcessingException an exception
     */
    private void validateCommentatorAdmin(HttpRequest request) throws HttpProcessingException {
        Player player = getResourceOwnerSafely(request, null);

        if (!player.hasType(Player.Type.ADMIN) && !player.hasType(Player.Type.COMMENTARY_ADMIN))
            throw new HttpProcessingException(403);
    }

    private void validateVariableAdmin(HttpRequest request, Player.Type flag) throws Exception {
        switch (flag) {
            case PLAYTESTER:
                validatePlaytestingAdmin(request);
                break;
            case COMMENTATOR:
                validateCommentatorAdmin(request);
                break;
            default:
                validateAdmin(request);
                break;
        }
    }
}