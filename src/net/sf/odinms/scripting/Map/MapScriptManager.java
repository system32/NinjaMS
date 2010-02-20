/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.scripting.Map;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.sf.odinms.client.MapleClient;

/**
 *
 * @author Owner
 */
public class MapScriptManager {
    private static MapScriptManager instance = new MapScriptManager();
    private Map<String, MapScript> scripts = new HashMap<String, MapScript>();
    private ScriptEngineFactory sef;

    private MapScriptManager() {
        ScriptEngineManager sem = new ScriptEngineManager();
        sef = sem.getEngineByName("javascript").getFactory();
    }

    public static MapScriptManager getInstance() {
        return instance;
    }

    public boolean scriptExists(String scriptName, boolean firstUser) {
        File scriptFile = new File("scripts/map/" + (firstUser ? "onFirstUserEnter" : "onUserEnter") + "/" + scriptName + ".js");
        return scriptFile.exists();
    }

    public void getMapScript(MapleClient c, String scriptName, boolean firstUser) {
        if (scripts.containsKey(scriptName)) {
            scripts.get(scriptName).start(new MapScriptMethods(c));
            return;
        }
        String type = "onUserEnter";
        if (firstUser) {
            type = "onFirstUserEnter";
        }
        File scriptFile = new File("scripts/map/" + type + "/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            return;
        }
        FileReader fr = null;
        ScriptEngine portal = sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (ScriptException e) {
            System.err.println("THROW" + e);
        } catch (IOException e) {
            System.err.println("THROW" + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        MapScript script = ((Invocable) portal).getInterface(MapScript.class);
        scripts.put(scriptName, script);
        script.start(new MapScriptMethods(c));
    }

    public void clearScripts() {
        scripts.clear();
    }
}
