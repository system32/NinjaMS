/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.storage;

import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.odinms.client.MapleCharacter;

/**
 *
 * @author Oliver
 */
public class ServerPlayerStorage implements IServerPlayerStorage {
    private Map<Integer, MapleCharacter> characters = new LinkedHashMap<Integer, MapleCharacter>();

    public ServerPlayerStorage() {
    }

    public void addPlayerToWorldStorage(MapleCharacter character) {
        characters.put(character.getId(), character);
    }

    public void removePlayerFromWorldStorage(int id) {
        characters.remove(id);
    }

    public Map<Integer, MapleCharacter> getCharacters() {
        return characters;
    }
}
