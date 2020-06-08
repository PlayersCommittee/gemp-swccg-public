package com.gempukku.swccgo.db;

import com.gempukku.swccgo.cache.Cached;

import java.util.Set;

public class CachedIpBanDAO implements IpBanDAO, Cached {
    private IpBanDAO _delegate;
    private Set<String> _bannedIps;
    private Set<String> _bannedIpPrefixes;

    public CachedIpBanDAO(IpBanDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _bannedIps = null;
        _bannedIpPrefixes = null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void addIpBan(String ip) {
        _delegate.addIpBan(ip);
        _bannedIps = null;
    }

    @Override
    public void addIpPrefixBan(String ipPrefix) {
        _delegate.addIpPrefixBan(ipPrefix);
        _bannedIpPrefixes = null;
    }

    @Override
    public Set<String> getIpBans() {
        Set<String> result = _bannedIps;
        if (result != null)
            return result;

        result = _delegate.getIpBans();
        _bannedIps = result;
        return result;
    }

    @Override
    public Set<String> getIpPrefixBans() {
        Set<String> result = _bannedIpPrefixes;
        if (result != null)
            return result;

        result = _delegate.getIpPrefixBans();
        _bannedIpPrefixes = result;
        return result;
    }
}
