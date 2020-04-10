package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.cache.CacheManager;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.state.SortPlayerByName;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.*;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.tournament.TournamentService;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private LeagueService _leagueService;
    private TournamentService _tournamentService;
    private CacheManager _cacheManager;
    private HallServer _hallServer;
    private SwccgCardBlueprintLibrary _cardLibrary;
    private SwccgoFormatLibrary _formatLibrary;
    private LeagueDAO _leagueDao;
    private CollectionsManager _collectionManager;
    private PlayerDAO _playerDAO;
    private AdminService _adminService;

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
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.equals("/clearCache") && request.getMethod() == HttpMethod.GET) {
            clearCache(request, responseWriter);
        } else if (uri.equals("/startup") && request.getMethod() == HttpMethod.GET) {
            startup(request, responseWriter);
        } else if (uri.equals("/shutdown") && request.getMethod() == HttpMethod.GET) {
            shutdown(request, responseWriter);
        } else if (uri.equals("/setMotd") && request.getMethod() == HttpMethod.POST) {
            setMotd(request, responseWriter);
        } else if (uri.equals("/previewSealedLeague") && request.getMethod() == HttpMethod.POST) {
            previewSealedLeague(request, responseWriter);
        } else if (uri.equals("/addSealedLeague") && request.getMethod() == HttpMethod.POST) {
            addSealedLeague(request, responseWriter);
        } else if (uri.equals("/previewConstructedLeague") && request.getMethod() == HttpMethod.POST) {
            previewConstructedLeague(request, responseWriter);
        } else if (uri.equals("/addConstructedLeague") && request.getMethod() == HttpMethod.POST) {
            addConstructedLeague(request, responseWriter);
        } else if (uri.equals("/addPlayersToLeague") && request.getMethod() == HttpMethod.POST) {
            addPlayersToLeague(request, responseWriter, e);
        } else if (uri.equals("/addItems") && request.getMethod() == HttpMethod.POST) {
            addItems(request, responseWriter);
        } else if (uri.equals("/addItemsToCollection") && request.getMethod() == HttpMethod.POST) {
            addItemsToCollection(request, responseWriter);
        } else if (uri.equals("/addPlaytester") && request.getMethod() == HttpMethod.POST) {
            addPlaytester(request, responseWriter);
        } else if (uri.equals("/removePlaytesters") && request.getMethod() == HttpMethod.POST) {
            removePlaytesters(request, responseWriter);
        } else if (uri.equals("/showPlaytesters") && request.getMethod() == HttpMethod.POST) {
            showPlaytesters(request, responseWriter);
        } else if (uri.equals("/resetUserPassword") && request.getMethod() == HttpMethod.POST) {
            resetUserPassword(request, responseWriter);
        } else if (uri.equals("/deactivateMultiple") && request.getMethod() == HttpMethod.POST) {
            deactivateMultiple(request, responseWriter);
        } else if (uri.equals("/banUser") && request.getMethod() == HttpMethod.POST) {
            banUser(request, responseWriter);
        } else if (uri.equals("/banMultiple") && request.getMethod() == HttpMethod.POST) {
            banMultiple(request, responseWriter);
        } else if (uri.equals("/banUserTemp") && request.getMethod() == HttpMethod.POST) {
            banUserTemp(request, responseWriter);
        } else if (uri.equals("/unBanUser") && request.getMethod() == HttpMethod.POST) {
            unBanUser(request, responseWriter);
        } else if (uri.equals("/findMultipleAccounts") && request.getMethod() == HttpMethod.POST) {
            findMultipleAccounts(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void showPlaytesters(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validatePlaytestingAdmin(request);

        List<Player> playtesters = _playerDAO.findPlaytesters();
        if (playtesters == null)
            throw new HttpProcessingException(404);

        Collections.sort(playtesters, new SortPlayerByName());
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element players = doc.createElement("players");

        for (Player playtester : playtesters) {
            Element playerElem = doc.createElement("player");
            playerElem.setAttribute("name", playtester.getName());
            players.appendChild(playerElem);
        }

        doc.appendChild(players);

        responseWriter.writeXmlResponse(doc);
    }

    private void findMultipleAccounts(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");

        List<Player> similarPlayers = _playerDAO.findSimilarAccounts(new Player(0, login, null, null, null, null, login, login));
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

    private String getStatus(Player player) {
        if (!player.hasType(Player.Type.UNBANNED)) {
            if (player.getBannedUntil() != null) {
                if (player.getBannedUntil().after(new Date())) {
                    return "OK";
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
        return "OK";
    }

    private void resetUserPassword(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");

        if (login==null)
            throw new HttpProcessingException(404);

        if (!_adminService.resetUserPassword(login))
            throw new HttpProcessingException(404);

        responseWriter.writeHtmlResponse("OK");
    }

    private void addPlaytester(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validatePlaytestingAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");

        if (login==null)
            throw new HttpProcessingException(404);

        if (!_adminService.setUserAsPlaytester(login, true))
            throw new HttpProcessingException(404);

        responseWriter.writeHtmlResponse("OK");
    }

    private void removePlaytesters(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validatePlaytestingAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        List<String> logins = getFormParametersSafely(postDecoder, "login");
        if (logins == null)
            throw new HttpProcessingException(404);

        for (String login : logins) {
            if (!_adminService.setUserAsPlaytester(login, false))
                throw new HttpProcessingException(404);
        }

        responseWriter.writeHtmlResponse("OK");
    }

    private void banUser(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");

        if (login==null)
            throw new HttpProcessingException(404);

        if (!_adminService.banUser(login))
            throw new HttpProcessingException(404);

        responseWriter.writeHtmlResponse("OK");
    }

    private void deactivateMultiple(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        List<String> logins = getFormParametersSafely(postDecoder, "login");
        if (logins == null)
            throw new HttpProcessingException(404);

        for (String login : logins) {
            if (!_adminService.deactivateUser(login))
                throw new HttpProcessingException(404);
        }

        responseWriter.writeHtmlResponse("OK");
    }

    private void banMultiple(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        List<String> logins = getFormParametersSafely(postDecoder, "login");
        if (logins == null)
            throw new HttpProcessingException(404);

        for (String login : logins) {
            if (!_adminService.banUser(login))
                throw new HttpProcessingException(404);
        }

        responseWriter.writeHtmlResponse("OK");
    }

    private void banUserTemp(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");
        int duration = Integer.parseInt(getFormParameterSafely(postDecoder, "duration"));

        if (!_adminService.banUserTemp(login, duration))
            throw new HttpProcessingException(404);

        responseWriter.writeHtmlResponse("OK");
    }

    private void unBanUser(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String login = getFormParameterSafely(postDecoder, "login");

        if (!_adminService.unBanUser(login))
            throw new HttpProcessingException(404);

        responseWriter.writeHtmlResponse("OK");
    }

    private void addItemsToCollection(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String reason = getFormParameterSafely(postDecoder, "reason");
        String product = getFormParameterSafely(postDecoder, "product");
        String collectionType = getFormParameterSafely(postDecoder, "collectionType");

        Collection<CardCollection.Item> productItems = getProductItems(product);

        Map<Player, CardCollection> playersCollection = _collectionManager.getPlayersCollection(collectionType);

        for (Map.Entry<Player, CardCollection> playerCollection : playersCollection.entrySet())
            _collectionManager.addItemsToPlayerCollection(true, reason, playerCollection.getKey(), createCollectionType(collectionType), productItems);

        responseWriter.writeHtmlResponse("OK");
    }

    private void addItems(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException, Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String players = getFormParameterSafely(postDecoder, "players");
        String product = getFormParameterSafely(postDecoder, "product");
        String collectionType = getFormParameterSafely(postDecoder, "collectionType");

        Collection<CardCollection.Item> productItems = getProductItems(product);

        List<String> playerNames = getItems(players);

        for (String playerName : playerNames) {
            Player player = _playerDao.getPlayer(playerName);

            _collectionManager.addItemsToPlayerCollection(true, "Administrator action", player, createCollectionType(collectionType), productItems);
        }

        responseWriter.writeHtmlResponse("OK");
    }

    private List<String> getItems(String values) {
        List<String> result = new LinkedList<String>();
        for (String pack : values.split("\n")) {
            String blueprint = pack.trim();
            if (blueprint.length() > 0)
                result.add(blueprint);
        }
        return result;
    }

    private Collection<CardCollection.Item> getProductItems(String values) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
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
     * Adds a constructed league using the specified parameters.
     * @param request the request
     * @param responseWriter the response writer
     * @throws Exception
     */
    private void addConstructedLeague(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String start = getFormParameterSafely(postDecoder, "start");
        String collectionType = getFormParameterSafely(postDecoder, "collectionType");
        List<String> formats = getFormMultipleParametersSafely(postDecoder, "format");
        List<String> seriesDurations = getFormMultipleParametersSafely(postDecoder, "seriesDuration");
        List<String> maxMatches = getFormMultipleParametersSafely(postDecoder, "maxMatches");
        String name = getFormParameterSafely(postDecoder, "name");
        int cost = Integer.parseInt(getFormParameterSafely(postDecoder, "cost"));

        String allowSpectatorsOnOff = getFormParameterSafely(postDecoder, "allowSpectators");
        boolean allowSpectators = allowSpectatorsOnOff != null && allowSpectatorsOnOff.equals("on");

        String allowTimeExtensionsOnOff = getFormParameterSafely(postDecoder, "allowTimeExtensions");
        boolean allowTimeExtensions = allowTimeExtensionsOnOff != null && allowTimeExtensionsOnOff.equals("on");

        String showPlayerNamesOnOff = getFormParameterSafely(postDecoder, "showPlayerNames");
        boolean showPlayerNames = showPlayerNamesOnOff != null && showPlayerNamesOnOff.equals("on");

        int decisionTimeoutSeconds = Integer.parseInt(getFormParameterSafely(postDecoder, "decisionTimeoutSeconds"));

        String code = String.valueOf(System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();
        sb.append(start + "," + collectionType + "," + formats.size());
        for (int i = 0; i < formats.size(); ++i) {
            sb.append("," + formats.get(i) + "," + seriesDurations.get(i) + "," + maxMatches.get(i));
        }

        String parameters = sb.toString();
        LeagueData leagueData = new NewConstructedLeagueData(_cardLibrary, parameters);
        List<LeagueSeriesData> series = leagueData.getSeries();
        int leagueStart = series.get(0).getStart();
        int displayEnd = DateUtils.offsetDate(series.get(series.size() - 1).getEnd(), 2);

        _leagueDao.addLeague(cost, name, code, leagueData.getClass().getName(), parameters, leagueStart, displayEnd, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds);

        _leagueService.clearCache();

        responseWriter.writeHtmlResponse("OK");
    }

    /**
     * Creates a response with a preview of the sealed league using the specified parameters.
     * @param request the request
     * @param responseWriter the response writer
     * @throws Exception
     */
    private void previewConstructedLeague(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String start = getFormParameterSafely(postDecoder, "start");
        String collectionType = getFormParameterSafely(postDecoder, "collectionType");
        List<String> formats = getFormMultipleParametersSafely(postDecoder, "format");
        List<String> seriesDurations = getFormMultipleParametersSafely(postDecoder, "seriesDuration");
        List<String> maxMatches = getFormMultipleParametersSafely(postDecoder, "maxMatches");
        String name = getFormParameterSafely(postDecoder, "name");
        int cost = Integer.parseInt(getFormParameterSafely(postDecoder, "cost"));

        StringBuilder sb = new StringBuilder();
        sb.append(start + "," + collectionType + "," + formats.size());
        for (int i = 0; i < formats.size(); ++i) {
            sb.append("," + formats.get(i) + "," + seriesDurations.get(i) + "," + maxMatches.get(i));
        }

        String parameters = sb.toString();
        LeagueData leagueData = new NewConstructedLeagueData(_cardLibrary, parameters);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        final List<LeagueSeriesData> series = leagueData.getSeries();

        int end = series.get(series.size() - 1).getEnd();

        Element leagueElem = doc.createElement("league");

        leagueElem.setAttribute("name", name);
        leagueElem.setAttribute("cost", String.valueOf(cost));
        leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
        leagueElem.setAttribute("end", String.valueOf(end));

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

    /**
     * Adds a sealed league using the specified parameters.
     * @param request the request
     * @param responseWriter the response writer
     * @throws Exception
     */
    private void addSealedLeague(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String format = getFormParameterSafely(postDecoder, "format");
        String start = getFormParameterSafely(postDecoder, "start");
        String seriesDuration = getFormParameterSafely(postDecoder, "seriesDuration");
        String maxMatches = getFormParameterSafely(postDecoder, "maxMatches");
        String name = getFormParameterSafely(postDecoder, "name");
        int cost = Integer.parseInt(getFormParameterSafely(postDecoder, "cost"));

        String allowSpectatorsOnOff = getFormParameterSafely(postDecoder, "allowSpectators");
        boolean allowSpectators = allowSpectatorsOnOff != null && allowSpectatorsOnOff.equals("on");

        String allowTimeExtensionsOnOff = getFormParameterSafely(postDecoder, "allowTimeExtensions");
        boolean allowTimeExtensions = allowTimeExtensionsOnOff != null && allowTimeExtensionsOnOff.equals("on");

        String showPlayerNamesOnOff = getFormParameterSafely(postDecoder, "showPlayerNames");
        boolean showPlayerNames = showPlayerNamesOnOff != null && showPlayerNamesOnOff.equals("on");

        int decisionTimeoutSeconds = Integer.parseInt(getFormParameterSafely(postDecoder, "decisionTimeoutSeconds"));

        String code = String.valueOf(System.currentTimeMillis());

        String parameters = format + "," + start + "," + seriesDuration + "," + maxMatches + "," + code + "," + name;
        LeagueData leagueData = new NewSealedLeagueData(_cardLibrary, parameters);
        List<LeagueSeriesData> series = leagueData.getSeries();
        int leagueStart = series.get(0).getStart();
        int displayEnd = DateUtils.offsetDate(series.get(series.size() - 1).getEnd(), 2);

        _leagueDao.addLeague(cost, name, code, leagueData.getClass().getName(), parameters, leagueStart, displayEnd, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds);

        _leagueService.clearCache();

        responseWriter.writeHtmlResponse("OK");
    }

    /**
     * Creates a response with a preview of the sealed league using the specified parameters.
     * @param request the request
     * @param responseWriter the response writer
     * @throws Exception
     */
    private void previewSealedLeague(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String format = getFormParameterSafely(postDecoder, "format");
        String start = getFormParameterSafely(postDecoder, "start");
        String seriesDuration = getFormParameterSafely(postDecoder, "seriesDuration");
        String maxMatches = getFormParameterSafely(postDecoder, "maxMatches");
        String name = getFormParameterSafely(postDecoder, "name");
        int cost = Integer.parseInt(getFormParameterSafely(postDecoder, "cost"));

        String code = String.valueOf(System.currentTimeMillis());

        String parameters = format + "," + start + "," + seriesDuration + "," + maxMatches + "," + code + "," + name;
        LeagueData leagueData = new NewSealedLeagueData(_cardLibrary, parameters);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        final List<LeagueSeriesData> series = leagueData.getSeries();

        int end = series.get(series.size() - 1).getEnd();

        Element leagueElem = doc.createElement("league");

        leagueElem.setAttribute("name", name);
        leagueElem.setAttribute("cost", String.valueOf(cost));
        leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
        leagueElem.setAttribute("end", String.valueOf(end));

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

    private void addPlayersToLeague(HttpRequest request, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        validateLeagueAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String leagueType = getFormParameterSafely(postDecoder, "leagueType");
        String players = getFormParameterSafely(postDecoder, "players");

        League league = _leagueService.getLeagueByType(leagueType);
        if (league == null) {
            throw new HttpProcessingException(409);
        }
        List<String> playerNames = getItems(players);

        for (String playerName : playerNames) {
            Player player = _playerDao.getPlayer(playerName);
            if (player != null) {
                if (!_leagueService.isPlayerInLeague(league, player)) {
                    if (!_leagueService.playerJoinsLeague(league, player, e.getRemoteAddress().toString(), true)) {
                        throw new HttpProcessingException(409);
                    }
                }
            }
        }

        responseWriter.writeHtmlResponse("OK");
    }

    private void setMotd(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException, Exception {
        validateAdmin(request);

        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);

        String motd = getFormParameterSafely(postDecoder, "motd");

        _hallServer.setMOTD(motd);

        responseWriter.writeHtmlResponse("OK");
    }

    private void startup(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException {
        validateAdmin(request);

        _hallServer.setOperational();

        responseWriter.writeHtmlResponse("OK");
    }

    private void shutdown(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException {
        validateAdmin(request);

        _hallServer.setShutdown();

        responseWriter.writeHtmlResponse("OK");
    }

    private void clearCache(HttpRequest request, ResponseWriter responseWriter) throws HttpProcessingException {
        validateAdmin(request);

        _leagueService.clearCache();
        _tournamentService.clearCache();

        int before = _cacheManager.getTotalCount();

        _cacheManager.clearCaches();

        int after = _cacheManager.getTotalCount();

        responseWriter.writeHtmlResponse("Before: " + before + "<br>OK<br>After: " + after);
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

        if (!player.hasType(Player.Type.ADMIN) && !player.hasType(Player.Type.PLAY_TESTING_ADMIN))
            throw new HttpProcessingException(403);
    }
}
